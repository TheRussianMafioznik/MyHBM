package com.hbm.explosion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.hbm.config.CompatibilityConfig;
import com.hbm.main.MainRegistry;
import com.hbm.render.amlfrom1710.Vec3;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ExplosionNukeRayBatched {

	public HashMap<ChunkPos, HashSet<IntTriplet>> perChunk = new HashMap<ChunkPos, HashSet<IntTriplet>>();
	public List<ChunkPos> orderedChunks = new ArrayList<ChunkPos>();
	private CoordComparator comparator = new CoordComparator();
	public boolean isContained = true;
	int posX;
	int posY;
	int posZ;
	int chunkCenterX;
	int chunkCenterZ;
	World world;

	int strength;
	int radius;

	int gspNumMax;
	int gspNum;
	double gspX;
	double gspY;

	private static final int maxY = 255;
	private static final int minY = 0;
	private static final int LARGE_BLAST_WORKERS = 4;
	private static final int LARGE_BLAST_RADIUS = 128;
	private static final int RAYS_PER_TASK = 512;
	private static final ExecutorService WORKERS = Executors.newFixedThreadPool(LARGE_BLAST_WORKERS, new NukeThreadFactory());

	private Long2ObjectOpenHashMap<ExtendedBlockStorage[]> chunkStorageSnapshot = null;
	private boolean snapshotBuilt = false;

	public boolean isAusf3Complete = false;

	public ExplosionNukeRayBatched(World world, int x, int y, int z, int strength, int radius) {
		this.world = world;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.chunkCenterX = x >> 4;
		this.chunkCenterZ = z >> 4;
		this.strength = strength;
		this.radius = radius;

		this.gspNumMax = (int) (2.5 * Math.PI * Math.pow(this.strength, 2));
		this.gspNum = 1;

		this.gspX = Math.PI;
		this.gspY = 0.0;
	}

	private void generateGspUp() {
		if (this.gspNum < this.gspNumMax) {
			int k = this.gspNum + 1;
			double hk = -1.0 + 2.0 * (k - 1.0) / (this.gspNumMax - 1.0);
			this.gspX = Math.acos(hk);

			double prev_lon = this.gspY;
			double lon = prev_lon + 3.6 / Math.sqrt(this.gspNumMax) / Math.sqrt(1.0 - hk * hk);
			this.gspY = lon % (Math.PI * 2);
		} else {
			this.gspX = 0.0;
			this.gspY = 0.0;
		}
		this.gspNum++;
	}

	private Vec3 getSpherical2cartesian() {
		double dx = Math.sin(this.gspX) * Math.cos(this.gspY);
		double dz = Math.sin(this.gspX) * Math.sin(this.gspY);
		double dy = Math.cos(this.gspX);
		return Vec3.createVectorHelper(dx, dy, dz);
	}

	public void addPos(int x, int y, int z) {
		ChunkPos chunkPos = new ChunkPos(x >> 4, z >> 4);
		HashSet<IntTriplet> triplets = perChunk.get(chunkPos);

		if (triplets == null) {
			triplets = new HashSet<IntTriplet>();
			perChunk.put(chunkPos, triplets);
		}

		triplets.add(new IntTriplet(x, y, z));
	}

	private void buildChunkSnapshot() {
		if (snapshotBuilt) return;
		int chunkRadius = (this.radius >> 4) + 2;
		int diameter = chunkRadius * 2 + 1;
		chunkStorageSnapshot = new Long2ObjectOpenHashMap<ExtendedBlockStorage[]>(diameter * diameter);

		for (int cx = chunkCenterX - chunkRadius; cx <= chunkCenterX + chunkRadius; cx++) {
			for (int cz = chunkCenterZ - chunkRadius; cz <= chunkCenterZ + chunkRadius; cz++) {
				Chunk chunk = world.getChunkProvider().getLoadedChunk(cx, cz);
				if (chunk != null) {
					chunkStorageSnapshot.put(ChunkPos.asLong(cx, cz), chunk.getBlockStorageArray());
				}
			}
		}
		snapshotBuilt = true;
	}

	public void collectTip(int time) {
		if (!CompatibilityConfig.isWarDim(world)) {
			isAusf3Complete = true;
			return;
		}
		if (this.radius >= LARGE_BLAST_RADIUS) {
			collectTipParallel(time);
			return;
		}
		MutableBlockPos pos = new MutableBlockPos();
		long raysProcessed = 0;
		long start = System.currentTimeMillis();

		IBlockState blockState;
		Block b;
		int iX, iY, iZ;
		int rayRadius = (int) Math.ceil(this.radius);
		float rayStrength;
		Vec3 vec;

		while (this.gspNumMax >= this.gspNum) {
			vec = this.getSpherical2cartesian();
			rayStrength = strength * 0.3F;

			for (int r = 0; r < rayRadius + 1; r++) {
				iY = (int) Math.floor(posY + (vec.yCoord * r));

				if (iY < minY || iY > maxY) {
					isContained = false;
					break;
				}

				iX = (int) Math.floor(posX + (vec.xCoord * r));
				iZ = (int) Math.floor(posZ + (vec.zCoord * r));

				pos.setPos(iX, iY, iZ);
				if (!world.isBlockLoaded(pos, false)) {
					isContained = false;
					break;
				}
				blockState = world.getBlockState(pos);
				b = blockState.getBlock();

				if (b == Blocks.AIR) {
					if (r >= rayRadius) {
						isContained = false;
					}
					continue;
				}

				if (b.getExplosionResistance(world, pos, null, null) >= 2_000_000) {
					break;
				}

				rayStrength -= Math.pow(getNukeResistance(blockState, b) + 1, 3 * ((double) r) / ((double) rayRadius)) - 1;

				if (rayStrength > 0) {
					addPos(iX, iY, iZ);
					if (r >= rayRadius) {
						isContained = false;
					}
				} else {
					break;
				}
			}

			this.generateGspUp();
			raysProcessed++;
			if (raysProcessed % 50 == 0 && System.currentTimeMillis() + 1 > start + time) {
				return;
			}
		}

		orderedChunks.addAll(perChunk.keySet());
		orderedChunks.sort(comparator);
		isAusf3Complete = true;
	}

	private void collectTipParallel(int time) {
		buildChunkSnapshot();
		long start = System.currentTimeMillis();
		int workersCount = Math.max(1, LARGE_BLAST_WORKERS);

		while (this.gspNumMax >= this.gspNum && System.currentTimeMillis() < start + time) {
			List<RayBatchTask> tasks = new ArrayList<RayBatchTask>(workersCount);

			for (int w = 0; w < workersCount && this.gspNumMax >= this.gspNum; w++) {
				List<Vec3> batchVectors = new ArrayList<Vec3>(RAYS_PER_TASK);
				for (int i = 0; i < RAYS_PER_TASK && this.gspNumMax >= this.gspNum; i++) {
					batchVectors.add(this.getSpherical2cartesian());
					this.generateGspUp();
				}
				tasks.add(new RayBatchTask(batchVectors, chunkStorageSnapshot));
			}

			try {
				List<Future<RayResult>> results = WORKERS.invokeAll(tasks, Math.max(1L, (long) time), TimeUnit.MILLISECONDS);
				for (Future<RayResult> future : results) {
					if (!future.isDone() || future.isCancelled()) {
						continue;
					}
					RayResult result = future.get();
					if (!result.contained) {
						isContained = false;
					}
					for (IntTriplet triplet : result.positions) {
						addPos(triplet.xCoord, triplet.yCoord, triplet.zCoord);
					}
				}
			} catch (Exception e) {
				MainRegistry.logger.warn("Batched nuke ray collection failed, stopping early", e);
				break;
			}
		}

		if (this.gspNumMax < this.gspNum) {
			chunkStorageSnapshot = null;
			orderedChunks.addAll(perChunk.keySet());
			orderedChunks.sort(comparator);
			isAusf3Complete = true;
		}
	}

		@SuppressWarnings("deprecation")
	public static float getNukeResistance(IBlockState blockState, Block b) {
		if (blockState.getMaterial().isLiquid()) {
			return 0.1F;
		} else {
			if (b == Blocks.SANDSTONE) return 4F;
			if (b == Blocks.OBSIDIAN) return 18F;
			return b.getExplosionResistance(null);
		}
	}

	public class CoordComparator implements Comparator<ChunkPos> {
		@Override
		public int compare(ChunkPos o1, ChunkPos o2) {
			int diff1 = Math.abs(chunkCenterX - o1.x) + Math.abs(chunkCenterZ - o1.z);
			int diff2 = Math.abs(chunkCenterX - o2.x) + Math.abs(chunkCenterZ - o2.z);
			return Integer.compare(diff1, diff2);
		}
	}

	public void processChunk(int time) {
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() < start + time) {
			if (this.perChunk.isEmpty()) break;
			processChunkBlocks(start, time);
		}
	}

	HashSet<IntTriplet> positions = new HashSet<IntTriplet>();
	ChunkPos chunk;

	public void processChunkBlocks(long start, int time) {
		if (!CompatibilityConfig.isWarDim(world)) {
			this.perChunk.clear();
		}
		if (this.perChunk.isEmpty()) return;
		int i = 0;

		if (positions.isEmpty()) {
			chunk = orderedChunks.get(0);
			positions = perChunk.get(chunk);
		}

		List<IntTriplet> done = new ArrayList<IntTriplet>();
		MutableBlockPos pos = new MutableBlockPos();
		IBlockState airState = Blocks.AIR.getDefaultState();

		for (IntTriplet coord : positions) {
			pos.setPos(coord.xCoord, coord.yCoord, coord.zCoord);
			if (!world.isBlockLoaded(pos, false)) {
				done.add(coord);
				continue;
			}
			world.setBlockState(pos, airState, 2);
			done.add(coord);
			i++;
			if (i % Math.max(256, time * 16) == 0 && System.currentTimeMillis() + 1 > start + time) {
				break;
			}
		}
		positions.removeAll(done);

		if (positions.isEmpty()) {
			perChunk.remove(chunk);
			orderedChunks.remove(0);
		}
	}

	public static class IntTriplet {
		public final int xCoord;
		public final int yCoord;
		public final int zCoord;
		private final int hashCode;

		public IntTriplet(int x, int y, int z) {
			this.xCoord = x;
			this.yCoord = y;
			this.zCoord = z;
			this.hashCode = (x * 31 + y) * 31 + z;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof IntTriplet)) return false;
			IntTriplet other = (IntTriplet) obj;
			return xCoord == other.xCoord && yCoord == other.yCoord && zCoord == other.zCoord;
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}
	}

	private class RayBatchTask implements Callable<RayResult> {
		private final List<Vec3> vectors;
		private final Long2ObjectOpenHashMap<ExtendedBlockStorage[]> snapshot;

private RayBatchTask(List<Vec3> vectors, Long2ObjectOpenHashMap<ExtendedBlockStorage[]> snapshot) {
this.vectors = vectors;
this.snapshot = snapshot;
}
@SuppressWarnings("deprecation")
@Override
public RayResult call() {
RayResult result = new RayResult();
int rayRadius = (int) Math.ceil(ExplosionNukeRayBatched.this.radius);
float startStrength = ExplosionNukeRayBatched.this.strength * 0.3F;
for (int v = 0; v < vectors.size(); v++) {
Vec3 vec = vectors.get(v);
float rayStrength = startStrength;
for (int r = 0; r < rayRadius + 1; r++) {
int iY = (int) Math.floor(posY + (vec.yCoord * r));
if (iY < minY || iY > maxY) {
result.contained = false;
break;
}
int iX = (int) Math.floor(posX + (vec.xCoord * r));
int iZ = (int) Math.floor(posZ + (vec.zCoord * r));
ExtendedBlockStorage[] storages = snapshot.get(ChunkPos.asLong(iX >> 4, iZ >> 4));
if (storages == null) {
result.contained = false;
break;
}
ExtendedBlockStorage storage = storages[iY >> 4];
if (storage == null || storage.isEmpty()) {
if (r >= rayRadius) {
result.contained = false;
}
continue;
}
IBlockState blockState = storage.get(iX & 15, iY & 15, iZ & 15);
Block b = blockState.getBlock();
if (b == Blocks.AIR) {
if (r >= rayRadius) {
result.contained = false;
}
continue;
}
if (b.getExplosionResistance(null) >= 2_000_000) {
break;
}
rayStrength -= Math.pow(getNukeResistance(blockState, b) + 1, 3 * ((double) r) / ((double) rayRadius)) - 1;
if (rayStrength > 0) {
result.positions.add(new IntTriplet(iX, iY, iZ));
if (r >= rayRadius) {
result.contained = false;
}
} else {
break;
}
}
}
return result;
}
}
	private static class RayResult {
		private boolean contained = true;
		private final List<IntTriplet> positions = new ArrayList<IntTriplet>(1024);
	}
private static class NukeThreadFactory implements ThreadFactory {
private int index;
@Override
public Thread newThread(Runnable runnable) {
Thread thread = new Thread(runnable, "NTM-Nuke-Ray-" + index++);
thread.setDaemon(true);
return thread;
}
}
}