package com.hbm.handler.guncfg;

import java.util.List;
import java.util.Random;

import com.hbm.entity.projectile.EntityBulletBase;
import com.hbm.handler.BulletConfigSyncingUtil;
import com.hbm.handler.BulletConfiguration;
import com.hbm.handler.threading.PacketThreading;
import com.hbm.interfaces.IBulletImpactBehavior;
import com.hbm.interfaces.IBulletUpdateBehavior;
import com.hbm.items.ModItems;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.render.amlfrom1710.Vec3;
import com.hbm.util.BobMathUtil;
import com.hbm.util.ContaminationUtil;
import net.minecraft.util.math.Vec3d;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class GunNPCFactory {

	public static BulletConfiguration getMaskmanOrb() {

		BulletConfiguration bullet = new BulletConfiguration();

		bullet.ammo = ModItems.coin_maskman;
		bullet.velocity = 0.25F;
		bullet.spread = 0.000F;
		bullet.wear = 10;
		bullet.bulletsMin = 1;
		bullet.bulletsMax = 1;
		bullet.dmgMin = 100;
		bullet.dmgMax = 100;
		bullet.gravity = 0.0D;
		bullet.maxAge = 60;
		bullet.doesRicochet = false;
		bullet.ricochetAngle = 0;
		bullet.HBRC = 0;
		bullet.LBRC = 0;
		bullet.bounceMod = 1.0;
		bullet.doesPenetrate = false;
		bullet.doesBreakGlass = false;
		bullet.style = BulletConfiguration.STYLE_ORB;
		bullet.trail = 1;
		bullet.explosive = 1.5F;

				bullet.bUpdate = b -> {
			if (b.world.isRemote || b.ticksExisted % 10 != 5)
				return;

			List<EntityPlayer> players = b.world.getEntitiesWithinAABB(EntityPlayer.class, b.getEntityBoundingBox().grow(50, 50, 50));

			for (EntityPlayer player : players) {
				double dx = player.posX - b.posX;
				double dy = (player.posY + player.getEyeHeight()) - b.posY;
				double dz = player.posZ - b.posZ;
				double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
				if (len > 0.0D) {
					dx /= len;
					dy /= len;
					dz /= len;
				}

				EntityBulletBase bolt = new EntityBulletBase(b.world, BulletConfigSyncingUtil.MASKMAN_BOLT);
				bolt.shooter = b.shooter;
				bolt.setPosition(b.posX, b.posY, b.posZ);
				bolt.shoot(dx, dy, dz, 0.5F, 0.05F);
				b.world.spawnEntity(bolt);
			}
		};

		return bullet;
	}

	public static BulletConfiguration getMaskmanBolt() {

		BulletConfiguration bullet = BulletConfigFactory.standardBulletConfig();

		bullet.ammo = ModItems.coin_maskman;
		bullet.spread = 0.0F;
		bullet.dmgMin = 15;
		bullet.dmgMax = 20;
		bullet.wear = 10;
		bullet.leadChance = 0;
		bullet.explosive = 0.5F;
		bullet.setToBolt(BulletConfiguration.BOLT_LACUNAE);
		bullet.vPFX = "reddust";

		return bullet;
	}

	public static BulletConfiguration getMaskmanBullet() {

		BulletConfiguration bullet = BulletConfigFactory.standardBulletConfig();

		bullet.ammo = ModItems.coin_maskman;
		bullet.spread = 0.0F;
		bullet.dmgMin = 5;
		bullet.dmgMax = 10;
		bullet.wear = 10;
		bullet.leadChance = 15;
		bullet.style = BulletConfiguration.STYLE_FLECHETTE;
		bullet.vPFX = "bluedust";

		return bullet;
	}

	public static BulletConfiguration getMaskmanTracer() {

		BulletConfiguration bullet = BulletConfigFactory.standardBulletConfig();

		bullet.ammo = ModItems.coin_maskman;
		bullet.spread = 0.0F;
		bullet.dmgMin = 15;
		bullet.dmgMax = 20;
		bullet.wear = 10;
		bullet.leadChance = 0;
		bullet.setToBolt(BulletConfiguration.BOLT_NIGHTMARE);
		bullet.vPFX = "reddust";

		bullet.bImpact = new IBulletImpactBehavior() {

			@Override
			public void behaveBlockHit(EntityBulletBase bullet, int x, int y, int z) {

				if(bullet.world.isRemote)
					return;

				EntityBulletBase meteor = new EntityBulletBase(bullet.world, BulletConfigSyncingUtil.MASKMAN_METEOR);
				meteor.setPosition(bullet.posX, bullet.posY + 30 + meteor.world.rand.nextInt(10), bullet.posZ);
				meteor.motionY = -1D;
				meteor.shooter = bullet.shooter;
				bullet.world.spawnEntity(meteor);
			}
		};

		return bullet;
	}

	public static BulletConfiguration getMaskmanRocket() {

		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();

		bullet.ammo = ModItems.coin_maskman;
		bullet.gravity = 0.1D;
		bullet.velocity = 1.0F;
		bullet.dmgMin = 15;
		bullet.dmgMax = 20;
		bullet.blockDamage = false;
		bullet.explosive = 5.0F;
		bullet.style = BulletConfiguration.STYLE_ROCKET;

		return bullet;
	}

	public static BulletConfiguration getMaskmanMeteor() {

		BulletConfiguration bullet = BulletConfigFactory.standardGrenadeConfig();

		bullet.ammo = ModItems.coin_maskman;
		bullet.gravity = 0.1D;
		bullet.velocity = 1.0F;
		bullet.dmgMin = 20;
		bullet.dmgMax = 30;
		bullet.incendiary = 3;
		bullet.explosive = 2.5F;
		bullet.style = BulletConfiguration.STYLE_METEOR;

		bullet.bUpdate = new IBulletUpdateBehavior() {

			@Override
			public void behaveUpdate(EntityBulletBase bullet) {

				if(!bullet.world.isRemote)
					return;

				Random rand = bullet.world.rand;

				for(int i = 0; i < 5; i++) {
					NBTTagCompound nbt = new NBTTagCompound();
					nbt.setString("type", "vanillaExt");
					nbt.setString("mode", "flame");
					nbt.setDouble("posX", bullet.posX + rand.nextDouble() * 0.5 - 0.25);
					nbt.setDouble("posY", bullet.posY + rand.nextDouble() * 0.5 - 0.25);
					nbt.setDouble("posZ", bullet.posZ + rand.nextDouble() * 0.5 - 0.25);
					MainRegistry.proxy.effectNT(nbt);
				}
			}
		};

		return bullet;
	}
	
	public static BulletConfiguration getWormBolt() {

		BulletConfiguration bullet = BulletConfigFactory.standardBulletConfig();

		bullet.ammo = ModItems.coin_worm;
		bullet.spread = 0.0F;
		bullet.maxAge = 60;
		bullet.dmgMin = 15;
		bullet.dmgMax = 25;
		bullet.leadChance = 0;
		bullet.doesRicochet = false;
		bullet.setToBolt(BulletConfiguration.BOLT_WORM);

		return bullet;
	}

	public static BulletConfiguration getWormHeadBolt() {

		BulletConfiguration bullet = BulletConfigFactory.standardBulletConfig();

		bullet.ammo = ModItems.coin_worm;
		bullet.spread = 0.0F;
		bullet.maxAge = 100;
		bullet.dmgMin = 35;
		bullet.dmgMax = 60;
		bullet.leadChance = 0;
		bullet.doesRicochet = false;
		bullet.setToBolt(BulletConfiguration.BOLT_LASER);

		return bullet;
	}
	
		public static BulletConfiguration getRocketUFOConfig() {

		BulletConfiguration bullet = GunRocketFactory.getRocketConfig();

		bullet.vPFX = "reddust";
		bullet.destroysBlocks = false;
		bullet.explosive = 0F;

		bullet.bUpdate = new IBulletUpdateBehavior() {

			final double angle = 90;
			final double range = 100;
			final double rangeSq = range * range;

			@Override
			public void behaveUpdate(EntityBulletBase b) {
				if (b.world.isRemote)
					return;

				int targetId = b.getEntityData().getInteger("homingTarget");
				Entity target = targetId != 0 ? b.world.getEntityByID(targetId) : null;

				if (target == null || !target.isEntityAlive()) {
					target = chooseTarget(b);
				}

				if (target != null) {
					if (5.0D > b.getDistance(target)) {
						b.getConfig().bImpact.behaveBlockHit(b, -1, -1, -1);
						b.setDead();
						return;
					}

					double dx = target.posX - b.posX;
					double dy = target.posY + target.height * 0.5D - b.posY;
					double dz = target.posZ - b.posZ;
					double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

					if (dist > 0.0D) {
						double vel = Math.sqrt(b.motionX * b.motionX + b.motionY * b.motionY + b.motionZ * b.motionZ);
						double scale = vel / dist;
						b.motionX = dx * scale;
						b.motionY = dy * scale;
						b.motionZ = dz * scale;
					}
				}
			}

			private EntityLivingBase chooseTarget(EntityBulletBase b) {
				List<EntityLivingBase> entities = b.world.getEntitiesWithinAABB(EntityLivingBase.class, b.getEntityBoundingBox().grow(range, range, range));
				Vec3 mot = Vec3.createVectorHelper(b.motionX, b.motionY, b.motionZ);
				Vec3d bulletPos = new Vec3d(b.posX, b.posY, b.posZ);

				EntityLivingBase bestTarget = null;
				double bestAngle = angle;

				for (EntityLivingBase e : entities) {
					if (!e.isEntityAlive() || e == b.shooter)
						continue;

					double distSq = e.getDistanceSq(b);
					if (distSq >= rangeSq)
						continue;

					Vec3 delta = Vec3.createVectorHelper(e.posX - b.posX, e.posY + e.height * 0.5D - b.posY, e.posZ - b.posZ);
					double deltaAngle = BobMathUtil.getCrossAngle(mot, delta);
					if (deltaAngle >= bestAngle)
						continue;

					RayTraceResult r = b.world.rayTraceBlocks(bulletPos, new Vec3d(e.posX, e.posY + e.height * 0.5D, e.posZ), false, true, false);
					if (r != null && r.typeOfHit != Type.MISS)
						continue;

					bestTarget = e;
					bestAngle = deltaAngle;
				}

				if (bestTarget != null) {
					b.getEntityData().setInteger("homingTarget", bestTarget.getEntityId());
				}
				return bestTarget;
			}
		};

		bullet.bImpact = (b, x, y, z) -> {
			b.world.playSound(null, b.posX, b.posY, b.posZ, HBMSoundHandler.ufoBlast, SoundCategory.HOSTILE, 5.0F, 0.9F + b.world.rand.nextFloat() * 0.2F);
			b.world.playSound(null, b.posX, b.posY, b.posZ, SoundEvents.ENTITY_FIREWORK_BLAST, SoundCategory.HOSTILE, 5.0F, 0.5F);
			ContaminationUtil.radiate(b.world, b.posX, b.posY, b.posZ, 50, 0, 0, 500);

			for (int i = 0; 3 > i; i++) {
				NBTTagCompound data = new NBTTagCompound();
				data.setString("type", "plasmablast");
				data.setFloat("r", 0.0F);
				data.setFloat("g", 0.75F);
				data.setFloat("b", 1.0F);
				data.setFloat("pitch", -30F + 30F * i);
				data.setFloat("yaw", b.world.rand.nextFloat() * 180F);
				data.setFloat("scale", 5F);
				PacketThreading.createAllAroundThreadedPacket(new AuxParticlePacketNT(data, b.posX, b.posY, b.posZ),
						new TargetPoint(b.world.provider.getDimension(), b.posX, b.posY, b.posZ, 100));
			}
		};

		return bullet;
	}
}
