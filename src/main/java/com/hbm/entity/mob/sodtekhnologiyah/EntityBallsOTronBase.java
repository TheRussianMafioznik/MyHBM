package com.hbm.entity.mob.sodtekhnologiyah;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityBallsOTronBase extends EntityWormBase {

	public int attackCounter = 0;

	protected final Predicate<Entity> selector = ent -> {
		if (ent instanceof EntityWormBase && ((EntityWormBase) ent).getUniqueWormID() == EntityBallsOTronBase.this.getUniqueWormID()) {
			return false;
		}
		return true;
	};

	public EntityBallsOTronBase(World world) {
		super(world);
		this.setSize(2.0F, 2.0F);
		this.isImmuneToFire = true;
		this.isAirBorne = true;
		this.noClip = true;
		this.dragInAir = 0.995F;
		this.dragInGround = 0.98F;
		this.knockbackDivider = 1.0D;
	}

		protected void useLaser(Entity target, boolean isHead) {
		if (target == null || this.world.isRemote) return;

		float damage = isHead ? 18.0F : 8.0F;
		target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
		if (target instanceof EntityLivingBase) {
			target.setFire(isHead ? 5 : 2);
		}

		this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.HOSTILE, 2.0F, isHead ? 0.6F : 1.1F);

		if (this.world instanceof WorldServer) {
			WorldServer serverWorld = (WorldServer) this.world;
			double startX = this.posX;
			double startY = this.posY + this.getEyeHeight();
			double startZ = this.posZ;

			double endX = target.posX;
			double endY = target.posY + target.height * 0.5D;
			double endZ = target.posZ;

			double dx = endX - startX;
			double dy = endY - startY;
			double dz = endZ - startZ;
			double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
			int steps = (int) Math.min(dist * 3.0D, 180);

			for (int i = 0; i <= steps; i++) {
				double t = steps == 0 ? 0.0D : (double) i / (double) steps;
				double px = startX + dx * t;
				double py = startY + dy * t;
				double pz = startZ + dz * t;

				serverWorld.spawnParticle(EnumParticleTypes.REDSTONE, px, py, pz, 0, isHead ? 1.0D : 0.85D, 0.1D, isHead ? 0.2D : 0.9D, 1.0D);
				if (isHead && i % 3 == 0) {
					serverWorld.spawnParticle(EnumParticleTypes.END_ROD, px, py, pz, 1, 0.02D, 0.02D, 0.02D, 0.01D);
				}
			}
		}
	}

	@Override
	public boolean canEntityBeSeen(Entity entityIn) {
		return this.world.rayTraceBlocks(
				new Vec3d(this.posX, this.posY + (double) this.getEyeHeight(), this.posZ),
				new Vec3d(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ),
				false, true, false) == null;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5000.0D);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}
}
