package com.hbm.explosion;

import java.util.List;
import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.EntityGrenadeTau;
import com.hbm.config.CompatibilityConfig;
import com.hbm.entity.grenade.EntityGrenadeZOMG;
import com.hbm.entity.particle.EntityChlorineFX;
import com.hbm.entity.particle.EntityCloudFX;
import com.hbm.entity.particle.EntityModFX;
import com.hbm.entity.particle.EntityOrangeFX;
import com.hbm.entity.particle.EntityPinkCloudFX;
import com.hbm.entity.projectile.EntityBullet;
import com.hbm.entity.projectile.EntityMiniNuke;
import com.hbm.entity.projectile.EntityRainbow;
import com.hbm.entity.projectile.EntityRocket;
import com.hbm.entity.projectile.EntityRubble;
import com.hbm.entity.projectile.EntitySchrab;
import com.hbm.handler.ArmorUtil;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.ModDamageSource;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorRegistry.HazardClass;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ExplosionChaos {

	private static final Random rand = new Random();

	public static void explode(World world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)) {
			return;
		}
		MutableBlockPos pos = new BlockPos.MutableBlockPos();
		int r = bombStartStrength;
		int r22 = (r * r) / 2;
		for(int xx = -r; xx < r; xx++) {
			int X = xx + x;
			int XX = xx * xx;
			for(int yy = -r; yy < r; yy++) {
				int Y = yy + y;
				int YY = XX + yy * yy;
				for(int zz = -r; zz < r; zz++) {
					int Z = zz + z;
					if(r22 > YY + zz * zz) {
						destruction(world, pos.setPos(X, Y, Z));
					}
				}
			}
		}
	}

	public static void destruction(World world, BlockPos pos) {
		Block b = world.getBlockState(pos).getBlock();
		if(b != Blocks.BEDROCK && b != ModBlocks.reinforced_brick && b != ModBlocks.reinforced_sand
				&& b != ModBlocks.reinforced_glass && b != ModBlocks.reinforced_lamp_on && b != ModBlocks.reinforced_lamp_off) {
			world.setBlockToAir(pos);
		}
	}

	public static void spawnExplosion(World world, int x, int y, int z, int bound) {
		if(!CompatibilityConfig.isWarDim(world)) {
			return;
		}
		int[] signs = { 1, -1 };
		for(int i = 0; i < 25; i++) {
			for(int sx : signs) {
				for(int sy : signs) {
					for(int sz : signs) {
						int rx = rand.nextInt(bound) * sx;
						int ry = rand.nextInt(bound) * sy;
						int rz = rand.nextInt(bound) * sz;
						world.createExplosion(null, x + rx, y + ry, z + rz, 10.0F, true);
					}
				}
			}
		}
	}

	// cloudPoisoning
	public static void c(World world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)) {
			return;
		}
		double wat = bombStartStrength * 2.0D;
		double maxDist = bombStartStrength * 2.0D;

		int minX = MathHelper.floor(x - wat - 1.0D);
		int maxX = MathHelper.floor(x + wat + 1.0D);
		int minY = MathHelper.floor(y - wat - 1.0D);
		int maxY = MathHelper.floor(y + wat + 1.0D);
		int minZ = MathHelper.floor(z - wat - 1.0D);
		int maxZ = MathHelper.floor(z + wat + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));

		for(Entity entity : list) {
			double d4 = entity.getDistance(x, y, z) / maxDist;
			if(1.0D >= d4) {
				double dx = entity.posX - x;
				double dy = entity.posY + entity.getEyeHeight() - y;
				double dz = entity.posZ - z;
				double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				if(wat > dist) {
					if(entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if(!ArmorRegistry.hasProtection(player, EntityEquipmentSlot.HEAD, HazardClass.GAS_CORROSIVE)) {
							ArmorUtil.damageSuit(player, 0, 5);
							ArmorUtil.damageSuit(player, 1, 5);
							ArmorUtil.damageSuit(player, 2, 5);
							ArmorUtil.damageSuit(player, 3, 5);
						}
						if(ArmorUtil.checkForHazmat(player)) {
							continue;
						}
					}

					if(entity instanceof EntityLivingBase) {
						EntityLivingBase living = (EntityLivingBase) entity;
						if(living.isPotionActive(HbmPotion.taint)) {
							living.removePotionEffect(HbmPotion.taint);
							living.addPotionEffect(new PotionEffect(HbmPotion.mutation, 60 * 60 * 20 * 5, 0, false, true));
						} else if(ArmorRegistry.hasProtection(living, EntityEquipmentSlot.HEAD, HazardClass.BACTERIA)) {
							ArmorUtil.damageGasMaskFilter(living, 1);
						} else {
							entity.attackEntityFrom(ModDamageSource.cloud, 3);
						}
					}
				}
			}
		}
	}

	/**
	 * Sets all flammable blocks on fire
	 */
	public static void flameDeath(World world, BlockPos pos, int bound) {
		if(!CompatibilityConfig.isWarDim(world)) {
			return;
		}
		MutableBlockPos mPos = new BlockPos.MutableBlockPos();
		MutableBlockPos mPosUp = new BlockPos.MutableBlockPos();

		int r22 = (bound * bound) / 2;
		for(int xx = -bound; xx < bound; xx++) {
			int X = xx + pos.getX();
			int XX = xx * xx;
			for(int yy = -bound; yy < bound; yy++) {
				int Y = yy + pos.getY();
				int YY = XX + yy * yy;
				for(int zz = -bound; zz < bound; zz++) {
					int Z = zz + pos.getZ();
					if(r22 > YY + zz * zz) {
						mPos.setPos(X, Y, Z);
						mPosUp.setPos(X, Y + 1, Z);
						if(world.getBlockState(mPos).getBlock().isFlammable(world, mPos, EnumFacing.UP) && world.isAirBlock(mPosUp)) {
							world.setBlockState(mPosUp, Blocks.FIRE.getDefaultState());
						}
					}
				}
			}
		}
	}

	/**
	 * Sets all blocks on fire
	 */
	public static void burn(World world, BlockPos pos, int bound) {
		if(!CompatibilityConfig.isWarDim(world)) {
			return;
		}
		MutableBlockPos mPos = new BlockPos.MutableBlockPos();
		MutableBlockPos mPosUp = new BlockPos.MutableBlockPos();

		int r22 = (bound * bound) / 2;
		for(int xx = -bound; xx < bound; xx++) {
			int X = xx + pos.getX();
			int XX = xx * xx;
			for(int yy = -bound; yy < bound; yy++) {
				int Y = yy + pos.getY();
				int YY = XX + yy * yy;
				for(int zz = -bound; zz < bound; zz++) {
					int Z = zz + pos.getZ();
					if(r22 > YY + zz * zz) {
						mPos.setPos(X, Y, Z);
						mPosUp.setPos(X, Y + 1, Z);
						Block upBlock = world.getBlockState(mPosUp).getBlock();
						if((upBlock == Blocks.AIR || upBlock == Blocks.SNOW_LAYER) && !world.isAirBlock(mPos)) {
							world.setBlockState(mPosUp, Blocks.FIRE.getDefaultState());
						}
					}
				}
			}
		}
	}

	public static void spawnChlorine(World world, double x, double y, double z, int count, double speed, int type) {
		if(!CompatibilityConfig.isWarDim(world)) {
			return;
		}
		for(int i = 0; i < count; i++) {
			EntityModFX fx;
			if(type == 0) {
				fx = new EntityChlorineFX(world, x, y, z, 0.0, 0.0, 0.0);
			} else if(type == 1) {
				fx = new EntityCloudFX(world, x, y, z, 0.0, 0.0, 0.0);
			} else if(type == 2) {
				fx = new EntityPinkCloudFX(world, x, y, z, 0.0, 0.0, 0.0);
			} else {
				fx = new EntityOrangeFX(world, x, y, z, 0.0, 0.0, 0.0);
			}

			fx.motionY = rand.nextGaussian() * speed;
			fx.motionX = rand.nextGaussian() * speed;
			fx.motionZ = rand.nextGaussian() * speed;
			world.spawnEntity(fx);
		}
	}

	// pinkCloudPoisoning
	public static void pc(World world, int x, int y, int z, int bombStartStrength) {
		if(!CompatibilityConfig.isWarDim(world)) {
			return;
		}
		double wat = bombStartStrength * 2.0D;
		double maxDist = bombStartStrength * 2.0D;

		int minX = MathHelper.floor(x - wat - 1.0D);
		int maxX = MathHelper.floor(x + wat + 1.0D);
		int minY = MathHelper.floor(y - wat - 1.0D);
		int maxY = MathHelper.floor(y + wat + 1.0D);
		int minZ = MathHelper.floor(z - wat - 1.0D);
		int maxZ = MathHelper.floor(z + wat + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));

		for(Entity entity : list) {
			double d4 = entity.getDistance(x, y, z) / maxDist;
			if(1.0D >= d4) {
				double dx = entity.posX - x;
				double dy = entity.posY + entity.getEyeHeight() - y;
				double dz = entity.posZ - z;
				double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				if(wat > dist) {
					if(entity instanceof EntityPlayer) {
						EntityPlayer player = (EntityPlayer) entity;
						if(!ArmorRegistry.hasProtection(player, EntityEquipmentSlot.HEAD, HazardClass.GAS_CORROSIVE)) {
							ArmorUtil.damageSuit(player, 0, 25);
							ArmorUtil.damageSuit(player, 1, 25);
							ArmorUtil.damageSuit(player, 2, 25);
							ArmorUtil.damageSuit(player, 3, 25);
						}
					}
					if(entity instanceof EntityLivingBase) {
						EntityLivingBase living = (EntityLivingBase) entity;
						if(ArmorRegistry.hasAllProtection(living, EntityEquipmentSlot.HEAD, HazardClass.BACTERIA, HazardClass.SAND)) {
							ArmorUtil.damageGasMaskFilter(living, 2);
						} else {
							entity.attackEntityFrom(ModDamageSource.pc, 5);
						}
					}
				}
			}
		}
	}

	// Used by grenades and Chlorine seal gas blocks
public static void poison(World world, int x, int y, int z, int bombStartStrength) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
double wat = bombStartStrength * 2.0D;
double maxDist = bombStartStrength * 2.0D;
int minX = MathHelper.floor(x - wat - 1.0D);
int maxX = MathHelper.floor(x + wat + 1.0D);
int minY = MathHelper.floor(y - wat - 1.0D);
int maxY = MathHelper.floor(y + wat + 1.0D);
int minZ = MathHelper.floor(z - wat - 1.0D);
int maxZ = MathHelper.floor(z + wat + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));

		for(Entity entity : list) {
			double d4 = entity.getDistance(x, y, z) / maxDist;
			if(1.0D >= d4) {

double dx = entity.posX - x;
double dy = entity.posY + entity.getEyeHeight() - y;
double dz = entity.posZ - z;
double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
if(wat > dist && entity instanceof EntityLivingBase) {
EntityLivingBase entityLiving = (EntityLivingBase) entity;
if(ArmorRegistry.hasAllProtection(entityLiving, EntityEquipmentSlot.HEAD, HazardClass.NERVE_AGENT)) {
ArmorUtil.damageGasMaskFilter(entityLiving, 1);
} else {
entityLiving.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 5 * 20, 0));
entityLiving.addPotionEffect(new PotionEffect(MobEffects.POISON, 20 * 20, 2));
entityLiving.addPotionEffect(new PotionEffect(MobEffects.WITHER, 20, 1));
entityLiving.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 30 * 20, 1));
entityLiving.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 30 * 20, 2));
}
}
}
}
}
public static void cluster(World world, int x, int y, int z, int count, double gravity) {
cluster(world, x, y, z, count, gravity, 5);
}
public static void cluster(World world, int x, int y, int z, int count, double gravity, int size) {
for(int i = 0; i < count; i++) {
double mx = rand.nextGaussian() * 0.1 * size;
double my = rand.nextGaussian();
double mz = rand.nextGaussian() * 0.1 * size;
EntityRocket fragment = new EntityRocket(world, x, y, z, mx, my, mz, gravity);
fragment.explosionSize = size;
world.spawnEntity(fragment);
}
}
public static void miniMirv(World world, double x, double y, double z) {
double modifier = 1.25;
double zeta = Math.sqrt(2.0D) / 2.0D;
double vy = -rand.nextDouble();
double[][] dirs = {
{ 1.0D, 0.0D },
{ 0.0D, 1.0D },
{ -1.0D, 0.0D },
{ 0.0D, -1.0D },
{ zeta, zeta },
{ -zeta, zeta },
{ -zeta, -zeta },
{ zeta, -zeta }
};
for(double[] dir : dirs) {
EntityMiniNuke mirv = new EntityMiniNuke(world);
mirv.posX = x;
mirv.posY = y;
mirv.posZ = z;
mirv.motionX = dir[0] * modifier;
mirv.motionY = vy;
mirv.motionZ = dir[1] * modifier;
world.spawnEntity(mirv);
}
}
public static void explodeZOMG(World world, int x, int y, int z, int bombStartStrength) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
MutableBlockPos pos = new BlockPos.MutableBlockPos();
int r = bombStartStrength;
int r22 = (r * r) / 2;
for(int xx = -r; xx < r; xx++) {
int X = xx + x;
int XX = xx * xx;
for(int yy = -r; yy < r; yy++) {
int Y = yy + y;
int YY = XX + yy * yy;
for(int zz = -r; zz < r; zz++) {
int Z = zz + z;
if(r22 > YY + zz * zz) {
pos.setPos(X, Y, Z);
if(!(world.getBlockState(pos).getBlock() == Blocks.BEDROCK && 0 >= Y)) {
world.setBlockToAir(pos);
}
}
}
}
}
}
public static void frag(World world, int x, int y, int z, int count, boolean flame, Entity shooter) {
for(int i = 0; i < count; i++) {
double d1 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
double d2 = rand.nextDouble();
double d3 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
EntityArrow fragment = new EntityTippedArrow(world, x, y, z);
fragment.motionX = d1;
fragment.motionY = d2;
fragment.motionZ = d3;
fragment.shootingEntity = shooter;
fragment.setIsCritical(true);
if(flame) {
fragment.setFire(1000);
}
fragment.setDamage(2.5);
world.spawnEntity(fragment);
}
}
public static void schrab(World world, int x, int y, int z, int count, int gravity) {
for(int i = 0; i < count; i++) {
double d1 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
double d2 = rand.nextDouble();
double d3 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
EntitySchrab fragment = new EntitySchrab(world, x, y, z, d1, d2, d3, 0.0125D);
world.spawnEntity(fragment);
}
}
public static void pulse(World world, int x, int y, int z, int bombStartStrength) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
MutableBlockPos pos = new BlockPos.MutableBlockPos();
int r = bombStartStrength;
int r22 = (r * r) / 2;
for(int xx = -r; xx < r; xx++) {
int X = xx + x;
int XX = xx * xx;
for(int yy = -r; yy < r; yy++) {
int Y = yy + y;
int YY = XX + yy * yy;
for(int zz = -r; zz < r; zz++) {
int Z = zz + z;
if(r22 > YY + zz * zz) {
pos.setPos(X, Y, Z);
IBlockState state = world.getBlockState(pos);
if(70.0F >= state.getBlock().getExplosionResistance(world, pos, null, null)) {
pDestruction(world, pos, state);
}
}
}
}
}
}
public static void pDestruction(World world, int x, int y, int z) {
BlockPos pos = new BlockPos(x, y, z);
pDestruction(world, pos, world.getBlockState(pos));
}
private static void pDestruction(World world, BlockPos pos, IBlockState state) {
EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, state);
world.spawnEntity(entityfallingblock);
}
public static void plasma(World world, int x, int y, int z, int radius) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
MutableBlockPos pos = new BlockPos.MutableBlockPos();
int r22 = (radius * radius) / 2;
for(int xx = -radius; xx < radius; xx++) {
int X = xx + x;
int XX = xx * xx;
for(int yy = -radius; yy < radius; yy++) {
int Y = yy + y;
int YY = XX + yy * yy;
for(int zz = -radius; zz < radius; zz++) {
int Z = zz + z;
if(r22 + world.rand.nextInt(Math.max(1, r22 / 2)) > YY + zz * zz) {
pos.setPos(X, Y, Z);
Block block = world.getBlockState(pos).getBlock();
if(block.getExplosionResistance(world, pos, null, null) > 0.1F) continue;
if(block != Blocks.BEDROCK && block != ModBlocks.statue_elb && block != ModBlocks.statue_elb_g
&& block != ModBlocks.statue_elb_w && block != ModBlocks.statue_elb_f) {
world.setBlockState(pos, ModBlocks.plasma.getDefaultState());
}
}
}
}
}
}
public static void tauMeSinPi(World world, double x, double y, double z, int count, Entity shooter, EntityGrenadeTau tau) {
if(shooter instanceof EntityPlayer) {
EntityPlayer player = (EntityPlayer) shooter;
for(int i = 0; i < count; i++) {
double d1 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
double d2 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
double d3 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
EntityBullet fragment;
if(rand.nextInt(5) == 0) {
fragment = new EntityBullet(world, player, 3.0F, 35, 45, false, "tauDay", tau);
fragment.setDamage(rand.nextInt(301) + 100);
} else {
fragment = new EntityBullet(world, player, 3.0F, 35, 45, false, "eyyOk", tau);
fragment.setDamage(rand.nextInt(11) + 35);
}
fragment.motionX = d1 * 5;
fragment.motionY = d2 * 5;
fragment.motionZ = d3 * 5;
fragment.shootingEntity = shooter;
fragment.setIsCritical(true);
world.spawnEntity(fragment);
}
}
}
public static void zomg(World world, double x, double y, double z, int count, Entity shooter, EntityGrenadeZOMG zomg) {
EntityPlayer player = shooter instanceof EntityPlayer ? (EntityPlayer) shooter : null;
for(int i = 0; i < count; i++) {
double d1 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
double d2 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
double d3 = rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble();
EntityRainbow entityZomg = new EntityRainbow(world, player, 1F, 10000, 100000, zomg);
entityZomg.motionX = d1;
entityZomg.motionY = d2;
entityZomg.motionZ = d3;
entityZomg.shootingEntity = shooter;
world.spawnEntity(entityZomg);
world.playSound(null, zomg.posX, zomg.posY, zomg.posZ, HBMSoundHandler.zomgShoot, SoundCategory.AMBIENT, 10.0F, 0.8F + (rand.nextFloat() * 0.4F));
}
}
public static void spawnVolley(World world, double x, double y, double z, int count, double speed) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
for(int i = 0; i < count; i++) {
EntityModFX fx = new EntityOrangeFX(world, x, y, z, 0.0, 0.0, 0.0);
fx.motionX = rand.nextGaussian() * speed;
fx.motionZ = rand.nextGaussian() * speed;
fx.motionY = rand.nextDouble() * speed * 7.5D;
world.spawnEntity(fx);
}
}
public static void floater(World world, BlockPos pos, int radi, int height) {
floater(world, pos.getX(), pos.getY(), pos.getZ(), radi, height);
}
public static void floater(World world, int x, int y, int z, int radi, int height) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
MutableBlockPos pos = new BlockPos.MutableBlockPos();
int r22 = (radi * radi) / 2;
for(int xx = -radi; xx < radi; xx++) {
int X = xx + x;
int XX = xx * xx;
for(int yy = -radi; yy < radi; yy++) {
int Y = yy + y;
int YY = XX + yy * yy;
for(int zz = -radi; zz < radi; zz++) {
int Z = zz + z;
if(r22 > YY + zz * zz) {
pos.setPos(X, Y, Z);
IBlockState save = world.getBlockState(pos);
world.setBlockState(pos, Blocks.AIR.getDefaultState());
if(save.getBlock() != Blocks.AIR) {
world.setBlockState(pos.setPos(X, Y + height, Z), save);
}
}
}
}
}
}
public static void move(World world, BlockPos pos, int radius, int a, int b, int c) {
move(world, pos.getX(), pos.getY(), pos.getZ(), radius, a, b, c);
}
public static void move(World world, int x, int y, int z, int radius, int a, int b, int c) {
double wat = radius;
double maxDist = radius * 2.0D;
int minX = MathHelper.floor(x - wat - 1.0D);
int maxX = MathHelper.floor(x + wat + 1.0D);
int minY = MathHelper.floor(y - wat - 1.0D);
int maxY = MathHelper.floor(y + wat + 1.0D);
int minZ = MathHelper.floor(z - wat - 1.0D);
int maxZ = MathHelper.floor(z + wat + 1.0D);
		List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ));

		for(Entity entity : list) {
			double d4 = entity.getDistance(x, y, z) / maxDist;
			if(1.0D >= d4) {

double dx = entity.posX - x;
double dy = entity.posY + entity.getEyeHeight() - y;
double dz = entity.posZ - z;
if(entity instanceof EntityLiving && !(entity instanceof EntitySheep)) {
((EntityLiving) entity).setCustomNameTag(rand.nextBoolean() ? "Dinnerbone" : "Grumm");
}
if(entity instanceof EntitySheep) {
((EntityLiving) entity).setCustomNameTag("jeb_");
}
double dist = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
if(wat > dist) {
entity.setPosition(entity.posX + a, entity.posY + b, entity.posZ + c);
}
}
}
}
public static void levelDown(World world, int x, int y, int z, int radius) {
if(!CompatibilityConfig.isWarDim(world) || world.isRemote) {
return;
}
MutableBlockPos pos = new BlockPos.MutableBlockPos();
for(int i = x - radius; i <= x + radius; i++) {
for(int j = z - radius; j <= z + radius; j++) {
pos.setPos(i, y, j);
IBlockState b = world.getBlockState(pos);
float hardness = b.getBlockHardness(world, pos);
if(6000.0F > hardness && hardness > 0.0F && b.getBlock() != Blocks.AIR) {
EntityRubble rubble = new EntityRubble(world);
rubble.posX = i + 0.5F;
rubble.posY = y;
rubble.posZ = j + 0.5F;
rubble.motionY = 0.025F * 10 + 0.15F;
rubble.setMetaBasedOnBlock(b.getBlock(), b.getBlock().getMetaFromState(b));
world.spawnEntity(rubble);
world.setBlockState(pos, Blocks.AIR.getDefaultState());
}
}
}
}
@SuppressWarnings("deprecation")
public static void decontaminate(World world, BlockPos pos) {
IBlockState b = world.getBlockState(pos);
Block bblock = b.getBlock();
if(bblock == ModBlocks.waste_earth && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.GRASS.getDefaultState());
} else if(bblock == ModBlocks.waste_dirt && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.DIRT.getDefaultState());
} else if(bblock == ModBlocks.waste_sand && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.SAND.getDefaultState());
} else if(bblock == ModBlocks.waste_sand_red && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND));
} else if(bblock == Blocks.SANDSTONE && rand.nextInt(3) != 0) {
world.setBlockState(pos, ModBlocks.waste_sandstone.getDefaultState());
} else if((bblock == Blocks.HARDENED_CLAY || bblock == Blocks.STAINED_HARDENED_CLAY) && rand.nextInt(3) != 0) {
world.setBlockState(pos, ModBlocks.waste_sandstone_red.getDefaultState());
} else if(bblock == Blocks.RED_SANDSTONE && rand.nextInt(3) != 0) {
world.setBlockState(pos, ModBlocks.waste_sandstone_red.getDefaultState());
} else if(bblock == ModBlocks.waste_grass_tall && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.TALLGRASS.getDefaultState());
} else if(bblock == ModBlocks.waste_gravel && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
} else if(bblock == ModBlocks.waste_mycelium && rand.nextInt(5) == 0) {
world.setBlockState(pos, Blocks.MYCELIUM.getDefaultState());
} else if(bblock == ModBlocks.waste_leaves && rand.nextInt(5) != 0) {
world.setBlockState(pos, Blocks.LEAVES.getDefaultState());
} else if(bblock == ModBlocks.waste_trinitite && rand.nextInt(3) == 0) {
world.setBlockState(pos, Blocks.SAND.getDefaultState());
} else if(bblock == ModBlocks.waste_trinitite_red && rand.nextInt(3) == 0) {
world.setBlockState(pos, Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND));
} else if(bblock == ModBlocks.waste_log && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(b.getValue(BlockLog.AXIS))));
} else if(bblock == ModBlocks.waste_planks && rand.nextInt(3) != 0) {
world.setBlockState(pos, Blocks.PLANKS.getDefaultState());
} else if(bblock == ModBlocks.block_trinitite && rand.nextInt(10) == 0) {
world.setBlockState(pos, ModBlocks.block_lead.getDefaultState());
} else if(bblock == ModBlocks.block_waste && rand.nextInt(10) == 0) {
world.setBlockState(pos, ModBlocks.block_lead.getDefaultState());
} else if(bblock == ModBlocks.sellafield_core && rand.nextInt(10) == 0) {
world.setBlockState(pos, ModBlocks.sellafield_4.getStateFromMeta(world.rand.nextInt(4)));
} else if(bblock == ModBlocks.sellafield_4 && rand.nextInt(5) == 0) {
world.setBlockState(pos, ModBlocks.sellafield_3.getStateFromMeta(world.rand.nextInt(4)));
} else if(bblock == ModBlocks.sellafield_3 && rand.nextInt(5) == 0) {
world.setBlockState(pos, ModBlocks.sellafield_2.getStateFromMeta(world.rand.nextInt(4)));
} else if(bblock == ModBlocks.sellafield_2 && rand.nextInt(5) == 0) {
world.setBlockState(pos, ModBlocks.sellafield_1.getStateFromMeta(world.rand.nextInt(4)));
} else if(bblock == ModBlocks.sellafield_1 && rand.nextInt(5) == 0) {
world.setBlockState(pos, ModBlocks.sellafield_0.getStateFromMeta(world.rand.nextInt(4)));
} else if(bblock == ModBlocks.sellafield_0 && rand.nextInt(5) == 0) {
world.setBlockState(pos, ModBlocks.sellafield_slaked.getStateFromMeta(world.rand.nextInt(4)));
} else if(bblock == ModBlocks.sellafield_slaked && rand.nextInt(5) == 0) {
world.setBlockState(pos, Blocks.STONE.getDefaultState());
}
}
public static void hardenVirus(World world, int x, int y, int z, int bombStartStrength) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
MutableBlockPos pos = new BlockPos.MutableBlockPos();
int r22 = (bombStartStrength * bombStartStrength) / 2;
for(int xx = -bombStartStrength; xx < bombStartStrength; xx++) {
int X = xx + x;
int XX = xx * xx;
for(int yy = -bombStartStrength; yy < bombStartStrength; yy++) {
int Y = yy + y;
int YY = XX + yy * yy;
for(int zz = -bombStartStrength; zz < bombStartStrength; zz++) {
int Z = zz + z;
if(r22 > YY + zz * zz) {
if(world.getBlockState(pos.setPos(X, Y, Z)).getBlock() == ModBlocks.crystal_virus) {
world.setBlockState(pos, ModBlocks.crystal_hardened.getDefaultState());
}
}
}
}
}
}
public static void spreadVirus(World world, int x, int y, int z, int bombStartStrength) {
if(!CompatibilityConfig.isWarDim(world)) {
return;
}
MutableBlockPos pos = new BlockPos.MutableBlockPos();
int r22 = (bombStartStrength * bombStartStrength) / 2;
for(int xx = -bombStartStrength; xx < bombStartStrength; xx++) {
int X = xx + x;
int XX = xx * xx;
for(int yy = -bombStartStrength; yy < bombStartStrength; yy++) {
int Y = yy + y;
int YY = XX + yy * yy;
for(int zz = -bombStartStrength; zz < bombStartStrength; zz++) {
int Z = zz + z;
if(r22 > YY + zz * zz) {
if(rand.nextInt(15) == 0 && !world.isAirBlock(pos.setPos(X, Y, Z))) {
world.setBlockState(pos, ModBlocks.cheater_virus_seed.getDefaultState());
}
}
}
}
}
}
}
