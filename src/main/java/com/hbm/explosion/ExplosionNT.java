package com.hbm.explosion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.hbm.blocks.ModBlocks;
import com.hbm.config.CompatibilityConfig;
import com.hbm.render.amlfrom1710.Vec3;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class ExplosionNT extends Explosion {

	public Set<ExAttrib> atttributes = new HashSet<>();

	private Random explosionRNG = new Random();
	private World worldObj;
	protected int field_77289_h = 16;
    protected Map<EntityPlayer, Vec3> affectedEntities = new HashMap<>();
	public float explosionSize;
	public double explosionX;
	public double explosionY;
	public double explosionZ;
	public Entity exploder;
	/** A list of ChunkPositions of blocks affected by this explosion */
	public final List<BlockPos> affectedBlockPositions;
	
	public static final List<ExAttrib> nukeAttribs = Arrays.asList(new ExAttrib[] {ExAttrib.FIRE, ExAttrib.NOPARTICLE, ExAttrib.NOSOUND, ExAttrib.NODROP, ExAttrib.NOHURT});

	public ExplosionNT(World world, Entity exploder, double x, double y, double z, float strength) {
		super(world, exploder, x, y, z, strength, false, true);
		this.worldObj = world;
		this.explosionSize = strength;
		this.explosionX = x;
		this.explosionY = y;
		this.explosionZ = z;
		this.exploder = exploder;
		this.affectedBlockPositions = Lists.<BlockPos> newArrayList();
	}

	public ExplosionNT addAttrib(ExAttrib attrib) {
		atttributes.add(attrib);
		return this;
	}
	
	public ExplosionNT addAllAttrib(List<ExAttrib> attrib) {
		atttributes.addAll(attrib);
		return this;
	}
	
	public ExplosionNT overrideResolution(int res) {
		field_77289_h = res;
		return this;
	}

	public void explode() {
		if(CompatibilityConfig.isWarDim(worldObj)){
			doNTExplosionA();
    		doNTExplosionB();
		}
    }
	
		private void doNTExplosionA() {
		float f = this.explosionSize;
		Set<BlockPos> hashset = new HashSet<>();

		for(int i = 0; i < this.field_77289_h; ++i) {
			for(int j = 0; j < this.field_77289_h; ++j) {
				for(int k = 0; k < this.field_77289_h; ++k) {
					if(i == 0 || i == this.field_77289_h - 1 || j == 0 || j == this.field_77289_h - 1 || k == 0 || k == this.field_77289_h - 1) {
						double d0 = (double) ((float) i / ((float) this.field_77289_h - 1.0F) * 2.0F - 1.0F);
						double d1 = (double) ((float) j / ((float) this.field_77289_h - 1.0F) * 2.0F - 1.0F);
						double d2 = (double) ((float) k / ((float) this.field_77289_h - 1.0F) * 2.0F - 1.0F);
						double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
						d0 /= d3;
						d1 /= d3;
						d2 /= d3;
						float f1 = this.explosionSize * (0.7F + this.worldObj.rand.nextFloat() * 0.6F);
						double d5 = this.explosionX;
						double d6 = this.explosionY;
						double d7 = this.explosionZ;

						for(float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F) {
							BlockPos pos = new BlockPos(MathHelper.floor(d5), MathHelper.floor(d6), MathHelper.floor(d7));
							IBlockState block = this.worldObj.getBlockState(pos);

							if(block.getMaterial() != Material.AIR) {
								float f3 = this.exploder != null
										? this.exploder.getExplosionResistance(this, this.worldObj, pos, block)
										: block.getBlock().getExplosionResistance(worldObj, pos, null, this);
								f1 -= (f3 + 0.3F) * f2;
							}

							if(f1 > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.worldObj, pos, block, f1))) {
								hashset.add(pos);
							}

							d5 += d0 * (double) f2;
							d6 += d1 * (double) f2;
							d7 += d2 * (double) f2;
						}
					}
				}
			}
		}

		this.affectedBlockPositions.addAll(hashset);

		if(!has(ExAttrib.NOHURT)) {
			this.explosionSize *= 2.0F;
			int minX = MathHelper.floor(this.explosionX - (double) this.explosionSize - 1.0D);
			int maxX = MathHelper.floor(this.explosionX + (double) this.explosionSize + 1.0D);
			int minY = MathHelper.floor(this.explosionY - (double) this.explosionSize - 1.0D);
			int maxY = MathHelper.floor(this.explosionY + (double) this.explosionSize + 1.0D);
			int minZ = MathHelper.floor(this.explosionZ - (double) this.explosionSize - 1.0D);
			int maxZ = MathHelper.floor(this.explosionZ + (double) this.explosionSize + 1.0D);
			List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this.exploder, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));
			net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.worldObj, this, list, this.explosionSize);
			Vec3d origin = new Vec3d(this.explosionX, this.explosionY, this.explosionZ);

			for(Entity entity : list) {
				double d4 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double) this.explosionSize;

				if(1.0D >= d4) {
					double d5 = entity.posX - this.explosionX;
					double d6 = entity.posY + (double) entity.getEyeHeight() - this.explosionY;
					double d7 = entity.posZ - this.explosionZ;
					double d9 = (double) MathHelper.sqrt(d5 * d5 + d6 * d6 + d7 * d7);

					if(d9 != 0.0D) {
						d5 /= d9;
						d6 /= d9;
						d7 /= d9;
						double d10 = (double) this.worldObj.getBlockDensity(origin, entity.getEntityBoundingBox());
						double d11 = (1.0D - d4) * d10;
						entity.attackEntityFrom(DamageSource.causeExplosionDamage(this), (float) ((int) ((d11 * d11 + d11) / 2.0D * 8.0D * (double) this.explosionSize + 1.0D)));
						double d8 = d11;
						if(entity instanceof EntityLivingBase) {
							d8 = EnchantmentProtection.getBlastDamageReduction((EntityLivingBase) entity, d11);
						}
						entity.motionX += d5 * d8;
						entity.motionY += d6 * d8;
						entity.motionZ += d7 * d8;

						if(entity instanceof EntityPlayer) {
							this.affectedEntities.put((EntityPlayer) entity, Vec3.createVectorHelper(d5 * d11, d6 * d11, d7 * d11));
						}
					}
				}
			}

			this.explosionSize = f;
		}
	}

		private void doNTExplosionB() {
		if(!has(ExAttrib.NOSOUND))
			this.worldObj.playSound(null, this.explosionX, this.explosionY, this.explosionZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

		if(!has(ExAttrib.NOPARTICLE)) {
			if(this.explosionSize >= 2.0F) {
				this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
			} else {
				this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
			}
		}

		BlockPos.MutableBlockPos neighborPos = new BlockPos.MutableBlockPos();

		for(BlockPos chunkposition : this.affectedBlockPositions) {
			int i = chunkposition.getX();
			int j = chunkposition.getY();
			int k = chunkposition.getZ();
			IBlockState block = this.worldObj.getBlockState(chunkposition);

			if(!has(ExAttrib.NOPARTICLE)) {
				double d0 = (double) ((float) i + this.worldObj.rand.nextFloat());
				double d1 = (double) ((float) j + this.worldObj.rand.nextFloat());
				double d2 = (double) ((float) k + this.worldObj.rand.nextFloat());
				double d3 = d0 - this.explosionX;
				double d4 = d1 - this.explosionY;
				double d5 = d2 - this.explosionZ;
				double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
				d3 /= d6;
				d4 /= d6;
				d5 /= d6;
				double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
				d7 *= (double) (this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
				d3 *= d7;
				d4 *= d7;
				d5 *= d7;
				this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX) / 2.0D, (d1 + this.explosionY) / 2.0D, (d2 + this.explosionZ) / 2.0D, d3, d4, d5);
				this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
			}

			if(block.getMaterial() != Material.AIR) {
				if(block.getBlock().canDropFromExplosion(this) && !has(ExAttrib.NODROP)) {
					float chance = 1.0F;

					if(!has(ExAttrib.ALLDROP))
						chance = 1.0F / this.explosionSize;
					if(has(ExAttrib.ALLDROP) && (block.getBlock() == Blocks.STONE || block.getBlock() == Blocks.DIRT || block.getBlock() == Blocks.GRASS || block.getBlock() == ModBlocks.tektite || block.getBlock() == Blocks.COBBLESTONE || block.getBlock() == Blocks.GRAVEL || block.getBlock() == Blocks.SAND || block.getBlock() == Blocks.SANDSTONE || block.getBlock() == ModBlocks.basalt_smooth))
						chance = 0F;

					block.getBlock().dropBlockAsItemWithChance(this.worldObj, chunkposition, block, chance, 0);
				}

				block.getBlock().onBlockExploded(this.worldObj, chunkposition, this);

				if(block.isNormalCube()) {
					if(has(ExAttrib.DIGAMMA)) {
						this.worldObj.setBlockState(chunkposition, ModBlocks.ash_digamma.getDefaultState());
						neighborPos.setPos(i, j + 1, k);
						if(this.explosionRNG.nextInt(5) == 0 && this.worldObj.isAirBlock(neighborPos))
							this.worldObj.setBlockState(neighborPos.toImmutable(), ModBlocks.fire_digamma.getDefaultState());

					} else if(has(ExAttrib.DIGAMMA_CIRCUIT)) {
						if(i % 3 == 0 && k % 3 == 0) {
							this.worldObj.setBlockState(chunkposition, ModBlocks.pribris_digamma.getDefaultState());
						} else if((i % 3 == 0 || k % 3 == 0) && this.explosionRNG.nextBoolean()) {
							this.worldObj.setBlockState(chunkposition, ModBlocks.pribris_digamma.getDefaultState());
						} else {
							this.worldObj.setBlockState(chunkposition, ModBlocks.ash_digamma.getDefaultState());
							neighborPos.setPos(i, j + 1, k);
							if(this.explosionRNG.nextInt(5) == 0 && this.worldObj.isAirBlock(neighborPos))
								this.worldObj.setBlockState(neighborPos.toImmutable(), ModBlocks.fire_digamma.getDefaultState());
						}
					} else if(has(ExAttrib.LAVA_V)) {
						this.worldObj.setBlockState(chunkposition, ModBlocks.volcanic_lava_block.getDefaultState());
					}
				}
			}
		}

		if(has(ExAttrib.FIRE) || has(ExAttrib.BALEFIRE) || has(ExAttrib.LAVA)) {
			for(BlockPos chunkposition : this.affectedBlockPositions) {
				IBlockState block = this.worldObj.getBlockState(chunkposition);
				neighborPos.setPos(chunkposition.getX(), chunkposition.getY() - 1, chunkposition.getZ());
				IBlockState block1 = this.worldObj.getBlockState(neighborPos);

				boolean shouldReplace = has(ExAttrib.ALLMOD) || this.explosionRNG.nextInt(3) == 0;

				if(block.getMaterial() == Material.AIR && block1.isFullBlock() && shouldReplace) {
					if(has(ExAttrib.FIRE))
						this.worldObj.setBlockState(chunkposition, Blocks.FIRE.getDefaultState());
					else if(has(ExAttrib.BALEFIRE))
						this.worldObj.setBlockState(chunkposition, ModBlocks.balefire.getDefaultState());
					else if(has(ExAttrib.LAVA))
						this.worldObj.setBlockState(chunkposition, Blocks.FLOWING_LAVA.getDefaultState());
				}
			}
		}
	}

public Map<EntityPlayer, Vec3> func_77277_b() {
	return this.affectedEntities;
}

	public EntityLivingBase getExplosivePlacedBy() {
		return this.exploder == null ? null : (this.exploder instanceof EntityTNTPrimed ? ((EntityTNTPrimed) this.exploder).getTntPlacedBy() : (this.exploder instanceof EntityLivingBase ? (EntityLivingBase) this.exploder : null));
	}

	// unconventional name, sure, but it's short
	public boolean has(ExAttrib attrib) {
		return this.atttributes.contains(attrib);
	}

	// this solution is a bit hacky but in the end easier to work with
	public static enum ExAttrib {
		FIRE,		//classic vanilla fire explosion
		BALEFIRE,	//same with but with balefire
		DIGAMMA,
		DIGAMMA_CIRCUIT,
		LAVA,		//again the same thing but lava
		LAVA_V,		//again the same thing but volcaniclava
		ALLMOD,		//block placer attributes like fire are applied for all destroyed blocks
		ALLDROP,	//miner TNT!
		NODROP,		//the opposite
		NOPARTICLE,
		NOSOUND,
		NOHURT
	}

}
