package com.hbm.entity.mob.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIMaskmanCasualApproach extends EntityAIBase {

	World worldObj;
	EntityCreature attacker;
	int attackTick;
	double speedTowardsTarget;
	boolean longMemory;
	Path entityPathEntity;
	Class<? extends EntityLivingBase> classTarget;
	private int pathTimer;
	private double lastX;
	private double lastY;
	private double lastZ;

	private int failedPathFindingPenalty;

	public EntityAIMaskmanCasualApproach(EntityCreature owner, Class<? extends EntityLivingBase> target, double speed, boolean longMemory) {
		this(owner, speed, longMemory);
		this.classTarget = target;
	}

	public EntityAIMaskmanCasualApproach(EntityCreature owner, double speed, boolean longMemory) {
		this.attacker = owner;
		this.worldObj = owner.world;
		this.speedTowardsTarget = speed;
		this.longMemory = longMemory;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

		if (entitylivingbase == null || !entitylivingbase.isEntityAlive()) {
			return false;
		} else if (this.classTarget != null && !this.classTarget.isAssignableFrom(entitylivingbase.getClass())) {
			return false;
		} else {
			if (0 >= --this.pathTimer) {
				Vec3d pos = getApproachVec();
				this.entityPathEntity = this.attacker.getNavigator().getPathToXYZ(pos.x, pos.y, pos.z);
				this.pathTimer = 4 + this.attacker.getRNG().nextInt(7);
				return this.entityPathEntity != null;
			} else {
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();

		if (entitylivingbase == null || !entitylivingbase.isEntityAlive()) {
			return false;
		}
		if (!this.longMemory) {
			return !this.attacker.getNavigator().noPath();
		}
		return this.attacker.isWithinHomeDistanceFromPosition(entitylivingbase.getPosition());
	}

	@Override
	public void startExecuting() {
		this.attacker.getNavigator().setPath(this.entityPathEntity, this.speedTowardsTarget);
		this.pathTimer = 0;
	}

	@Override
	public void resetTask() {
		this.attacker.getNavigator().clearPath();
	}

	@Override
	public void updateTask() {
		EntityLivingBase entitylivingbase = this.attacker.getAttackTarget();
		if (entitylivingbase == null) return;

		this.attacker.getLookHelper().setLookPositionWithEntity(entitylivingbase, 30.0F, 30.0F);
		double d0 = this.attacker.getDistanceSq(entitylivingbase.posX, entitylivingbase.getEntityBoundingBox().minY, entitylivingbase.posZ);

		this.pathTimer--;

		if ((this.longMemory || this.attacker.getEntitySenses().canSee(entitylivingbase)) && 0 >= this.pathTimer
				&& (this.lastX == 0.0D && this.lastY == 0.0D && this.lastZ == 0.0D
				|| entitylivingbase.getDistanceSq(this.lastX, this.lastY, this.lastZ) >= 1.0D
				|| 0.05F > this.attacker.getRNG().nextFloat())) {

			this.lastX = entitylivingbase.posX;
			this.lastY = entitylivingbase.getEntityBoundingBox().minY;
			this.lastZ = entitylivingbase.posZ;
			this.pathTimer = failedPathFindingPenalty + 4 + this.attacker.getRNG().nextInt(7);

			Path currentPath = this.attacker.getNavigator().getPath();
			if (currentPath != null) {
				PathPoint finalPathPoint = currentPath.getFinalPathPoint();
				if (finalPathPoint != null && 1.0D > entitylivingbase.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z)) {
					failedPathFindingPenalty = 0;
				} else {
					failedPathFindingPenalty += 10;
				}
			} else {
				failedPathFindingPenalty += 10;
			}

			if (d0 > 1024.0D) {
				this.pathTimer += 10;
			} else if (d0 > 256.0D) {
				this.pathTimer += 5;
			}

			Vec3d pos = getApproachVec();
			if (!this.attacker.getNavigator().tryMoveToXYZ(pos.x, pos.y, pos.z, speedTowardsTarget)) {
				this.pathTimer += 15;
			}
		}

		this.attackTick = Math.max(this.attackTick - 1, 0);
	}

	public Vec3d getApproachVec() {
		EntityLivingBase target = this.attacker.getAttackTarget();
		if (target == null) {
			return new Vec3d(this.attacker.posX, this.attacker.posY, this.attacker.posZ);
		}

		double dx = this.attacker.posX - target.posX;
		double dy = this.attacker.posY - target.posY;
		double dz = this.attacker.posZ - target.posZ;
		double length = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);

		double range = Math.min(length, 20.0D) - 10.0D;

		if (length > 0.0D) {
			dx /= length;
			dy /= length;
			dz /= length;
		}

		double x = this.attacker.posX + dx * range + this.attacker.getRNG().nextGaussian() * 2.0D;
		double y = this.attacker.posY + dy - 5.0D + this.attacker.getRNG().nextInt(11);
		double z = this.attacker.posZ + dz * range + this.attacker.getRNG().nextGaussian() * 2.0D;

		return new Vec3d(x, y, z);
	}
}
