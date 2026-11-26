package com.hbm.entity.mob;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAINearestAttackableTargetNT extends EntityAITarget {

	private final Class<? extends Entity> targetClass;
	private final int targetChance;
	private final Predicate<Entity> targetEntitySelector;
	private EntityLivingBase targetEntity;
	private final double searchRange;

	public EntityAINearestAttackableTargetNT(EntityCreature owner, Class<? extends Entity> targetClass, int targetChance, boolean shouldCheckSight, boolean nearbyOnly, final Predicate<Entity> selector, double range) {
		super(owner, shouldCheckSight, nearbyOnly);
		this.targetClass = targetClass;
		this.targetChance = targetChance;
		this.searchRange = range;
		setMutexBits(1);

		this.targetEntitySelector = entity -> {
			if (selector != null && !selector.apply(entity)) {
				return false;
			}
			return entity instanceof EntityLivingBase && EntityAINearestAttackableTargetNT.this.isSuitableTarget((EntityLivingBase) entity, false);
		};
	}

	@Override
	protected double getTargetDistance() {
		return this.searchRange;
	}

	@Override
	public boolean shouldExecute() {
		if (this.targetChance > 0 && this.taskOwner.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		}

		double range = getTargetDistance();
		List<? extends Entity> targets = this.taskOwner.world.getEntitiesWithinAABB(
				this.targetClass,
				this.taskOwner.getEntityBoundingBox().grow(range, range, range),
				this.targetEntitySelector);

		if (targets.isEmpty()) {
			return false;
		}

		EntityLivingBase closest = null;
		double minDistSq = Double.MAX_VALUE;

		for (Entity candidate : targets) {
			if (candidate instanceof EntityLivingBase) {
				double distSq = this.taskOwner.getDistanceSq(candidate);
				if (minDistSq > distSq) {
					minDistSq = distSq;
					closest = (EntityLivingBase) candidate;
				}
			}
		}

		this.targetEntity = closest;
		return this.targetEntity != null;
	}

	@Override
	public void resetTask() {
		this.taskOwner.setAttackTarget(targetEntity);
	}
}
