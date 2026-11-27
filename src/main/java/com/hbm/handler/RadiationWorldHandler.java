package com.hbm.handler;

import java.util.Collection;
import java.util.Map.Entry;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.handler.RadiationSystemNT.RadPocket;
import com.hbm.main.MainRegistry;
import com.hbm.saveddata.RadiationSaveStructure;
import com.hbm.saveddata.RadiationSavedData;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockSnowBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;

public class RadiationWorldHandler {

	public static void handleWorldDestruction(World world) {
		if (!(world instanceof WorldServer))
			return;
		if (!RadiationConfig.worldRadEffects || !GeneralConfig.enableRads)
			return;

		int threshold = 5;

		if (GeneralConfig.advancedRadiation) {
			if (GeneralConfig.enableDebugMode) {
				MainRegistry.logger.info("[Debug] Starting world destruction processing");
			}

			Collection<RadPocket> activePockets = RadiationSystemNT.getActiveCollection(world);
			if (activePockets.isEmpty())
				return;

			int randIdx = world.rand.nextInt(activePockets.size());
			int itr = 0;
			for (RadPocket p : activePockets) {
				if (itr == randIdx) {
					if (threshold > p.radiation)
						return;

					BlockPos startPos = p.getSubChunkPos();
					int startX = startPos.getX();
					int startY = startPos.getY();
					int startZ = startPos.getZ();
					RadPocket[] pocketsByBlock = p.parent.pocketsByBlock;
					MutableBlockPos pos = new MutableBlockPos();

					for (int i = 0; 16 > i; i++) {
						for (int j = 0; 16 > j; j++) {
							for (int k = 0; 16 > k; k++) {
								if (world.rand.nextInt(3) != 0)
									continue;
								if (pocketsByBlock != null && pocketsByBlock[i * 256 + j * 16 + k] != p)
									continue;

								pos.setPos(startX + i, startY + j, startZ + k);
								IBlockState state = world.getBlockState(pos);
								if (state.getBlock() != Blocks.AIR) {
									contaminateBlock(world, pos, state, false);
								}
							}
						}
					}
					break;
				}
				itr++;
			}

			if (GeneralConfig.enableDebugMode) {
				MainRegistry.logger.info("[Debug] Finished world destruction processing");
			}
			return;
		}

		WorldServer serv = (WorldServer) world;
		RadiationSavedData data = RadiationSavedData.getData(serv);
		int size = data.contamination.size();
		if (size == 0)
			return;

		int randIdx = world.rand.nextInt(size);
		int itr = 0;
		Entry<ChunkPos, RadiationSaveStructure> randEnt = null;
		for (Entry<ChunkPos, RadiationSaveStructure> entry : data.contamination.entrySet()) {
			if (itr == randIdx) {
				randEnt = entry;
				break;
			}
			itr++;
		}

		if (randEnt == null || threshold > randEnt.getValue().radiation)
			return;

		ChunkPos coords = randEnt.getKey();
		ChunkProviderServer provider = (ChunkProviderServer) serv.getChunkProvider();

		if (provider.chunkExists(coords.x, coords.z)) {
			MutableBlockPos pos = new MutableBlockPos();
			for (int a = 0; 16 > a; a++) {
				for (int b = 0; 16 > b; b++) {
					if (world.rand.nextInt(3) != 0)
						continue;

					int x = coords.getXStart() + a;
					int z = coords.getZStart() + b;
					int y = world.getHeight(x, z) - world.rand.nextInt(2);
					pos.setPos(x, y, z);
					IBlockState state = world.getBlockState(pos);
					if (state.getBlock() != Blocks.AIR) {
						contaminateBlock(world, pos, state, true);
					}
				}
			}
		}
	}

	private static void contaminateBlock(World world, BlockPos pos, IBlockState state, boolean allowTrinitite) {
		Block bblock = state.getBlock();

		if (bblock == Blocks.GRASS) {
			world.setBlockState(pos, ModBlocks.waste_earth.getDefaultState());
		} else if (bblock == Blocks.DIRT || (!allowTrinitite && bblock == Blocks.FARMLAND)) {
			world.setBlockState(pos, ModBlocks.waste_dirt.getDefaultState());
		} else if (bblock == Blocks.SAND) {
			BlockSand.EnumType meta = state.getValue(BlockSand.VARIANT);
			if (allowTrinitite && world.rand.nextInt(60) == 0) {
				world.setBlockState(pos, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_trinitite.getDefaultState() : ModBlocks.waste_trinitite_red.getDefaultState());
			} else {
				world.setBlockState(pos, meta == BlockSand.EnumType.SAND ? ModBlocks.waste_sand.getDefaultState() : ModBlocks.waste_sand_red.getDefaultState());
			}
		} else if (bblock == Blocks.SANDSTONE) {
			world.setBlockState(pos, ModBlocks.waste_sandstone.getDefaultState());
		} else if (bblock == Blocks.RED_SANDSTONE) {
			world.setBlockState(pos, ModBlocks.waste_sandstone_red.getDefaultState());
		} else if (bblock == Blocks.HARDENED_CLAY || bblock == Blocks.STAINED_HARDENED_CLAY) {
			world.setBlockState(pos, ModBlocks.waste_terracotta.getDefaultState());
		} else if (bblock == Blocks.GRAVEL) {
			world.setBlockState(pos, ModBlocks.waste_gravel.getDefaultState());
		} else if (bblock == Blocks.MYCELIUM) {
			world.setBlockState(pos, ModBlocks.waste_mycelium.getDefaultState());
		} else if (bblock instanceof BlockSnow) {
			world.setBlockState(pos, ModBlocks.waste_snow.getDefaultState());
		} else if (bblock instanceof BlockSnowBlock) {
			world.setBlockState(pos, ModBlocks.waste_snow_block.getDefaultState());
		} else if (bblock instanceof BlockIce) {
			world.setBlockState(pos, ModBlocks.waste_ice.getDefaultState());
		} else if (bblock instanceof BlockBush) {
			world.setBlockState(pos, ModBlocks.waste_grass_tall.getDefaultState());
		} else if (bblock == ModBlocks.waste_leaves) {
			if (world.rand.nextInt(8) == 0) {
				world.setBlockToAir(pos);
			}
		} else if (bblock instanceof BlockLeaves) {
			world.setBlockState(pos, ModBlocks.waste_leaves.getDefaultState());
		}
	}
}
