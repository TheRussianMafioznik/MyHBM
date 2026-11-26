package com.hbm.entity.mob.ai;

import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

public class EntityAI_MLPF extends EntityAIBase {

	private final Class<? extends EntityLivingBase> targetClass;
	private EntityLivingBase target;
	private final EntityLiving mover;
	private final int range;
	private final int distance;
	private static final int VERTICAL = 10;
	private final double speed;

	public EntityAI_MLPF(EntityLiving entity, Class<? extends EntityLivingBase> targetClass, int range, double speed, int distance) {
		this.mover = entity;
		this.targetClass = targetClass;
		this.range = range;
		this.speed = speed;
		this.distance = distance;
	}

	@Override
	public boolean shouldExecute() {
		if (mover.getRNG().nextInt(100) < 5 && mover.getAttackTarget() == null) {
			calculateTarget();
			return target != null;
		}
		return false;
	}

	@Override
	public void startExecuting() {
		if (target == null) return;

		double dx = target.posX - mover.posX;
		double dy = target.posY - mover.posY;
		double dz = target.posZ - mover.posZ;
		double length = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

		if (length < 16.0D) {
			mover.setAttackTarget(target);
		}

		if (length > 0.0D) {
			double scale = distance / length;
			dx *= scale;
			dy *= scale;
			dz *= scale;
		}

		double x = mover.posX + dx;
		double y = mover.posY + dy - 5.0D + mover.getRNG().nextInt(11);
		double z = mover.posZ + dz;

		this.mover.getNavigator().tryMoveToXYZ(x, y, z, this.speed);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return !this.mover.getNavigator().noPath();
	}

	@Override
	public void resetTask() {
		target = null;
	}

	private void calculateTarget() {
		List<? extends EntityLivingBase> list = mover.world.getEntitiesWithinAABB(targetClass, new AxisAlignedBB(
				mover.posX - range,
				mover.posY - VERTICAL,
				mover.posZ - range,
				mover.posX + range,
				mover.posY + VERTICAL,
				mover.posZ + range));

		EntityLivingBase closest = null;
		double minDistSq = Double.MAX_VALUE;

		for (EntityLivingBase candidate : list) {
			double distSq = mover.getDistanceSq(candidate);
			if (distSq < minDistSq) {
				minDistSq = distSq;
				closest = candidate;
			}
		}

		target = closest;
	}

	public static class Sorter implements Comparator<Entity> {
		private final Entity theEntity;

		public Sorter(Entity entity) {
			this.theEntity = entity;
		}

		@Override
		public int compare(Entity first, Entity second) {
			return Double.compare(this.theEntity.getDistanceSq(first), this.theEntity.getDistanceSq(second));
		}
	}
}
