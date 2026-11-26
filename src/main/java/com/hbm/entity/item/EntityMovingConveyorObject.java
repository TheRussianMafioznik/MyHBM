package com.hbm.entity.item;

import com.hbm.tileentity.network.TileEntityCraneBase;

import api.hbm.block.IConveyorBelt;
import api.hbm.block.IEnterableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityMovingConveyorObject extends Entity {
    protected int turnProgress;
    protected double syncPosX;
    protected double syncPosY;
    protected double syncPosZ;
    @SideOnly(Side.CLIENT) protected double velocityX;
    @SideOnly(Side.CLIENT) protected double velocityY;
    @SideOnly(Side.CLIENT) protected double velocityZ;

    public EntityMovingConveyorObject(World worldIn) {
        super(worldIn);
        this.noClip = true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
public boolean canBeAttackedWithItem() {
    return true;
}

    @Override
    public boolean hitByEntity(Entity attacker) {
        if (attacker instanceof EntityPlayer) {
            this.setDead();
        }
        return false;
    }

    @Override
    protected boolean canTriggerWalking() {
        return true;
    }

    @Override
    public void onUpdate() {
        if (world.isRemote) {
            if (this.turnProgress > 0) {
                double interpX = this.posX + (this.syncPosX - this.posX) / (double) this.turnProgress;
                double interpY = this.posY + (this.syncPosY - this.posY) / (double) this.turnProgress;
                double interpZ = this.posZ + (this.syncPosZ - this.posZ) / (double) this.turnProgress;
                --this.turnProgress;
                this.setPosition(interpX, interpY, interpZ);
            } else {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
            return;
        }

        ticksExisted++;

        if (this.ticksExisted <= 5) {
            return;
        }

        int blockX = MathHelper.floor(posX);
        int blockY = MathHelper.floor(posY);
        int blockZ = MathHelper.floor(posZ);
        BlockPos lastPos = new BlockPos(blockX, blockY, blockZ);
        Block b = world.getBlockState(lastPos).getBlock();
        Vec3d currentPos = new Vec3d(posX, posY, posZ);

        boolean isOnConveyor = b instanceof IConveyorBelt && ((IConveyorBelt) b).canItemStay(world, blockX, blockY, blockZ, currentPos);

        if (!isOnConveyor) {
            if (onLeaveConveyor()) {
                return;
            }
        } else {
            Vec3d target = ((IConveyorBelt) b).getTravelLocation(world, blockX, blockY, blockZ, currentPos, getMoveSpeed());
            this.motionX = target.x - posX;
            this.motionY = target.y - posY;
            this.motionZ = target.z - posZ;
        }

        this.move(MoverType.SELF, motionX, motionY, motionZ);
        BlockPos newPos = new BlockPos(posX, posY, posZ);

        if (!lastPos.equals(newPos)) {
            IBlockState newState = world.getBlockState(newPos);
            Block newBlock = newState.getBlock();

            if (newBlock instanceof IEnterableBlock) {
                IEnterableBlock enterable = (IEnterableBlock) newBlock;
                EnumFacing dir = getEntryDirection(lastPos, newPos);

                TileEntity tileEntity = world.getTileEntity(newPos);
                if (tileEntity instanceof TileEntityCraneBase) {
                    TileEntityCraneBase craneBase = (TileEntityCraneBase) tileEntity;
                    if (dir == craneBase.getInputSide()) {
                        enterBlock(enterable, newPos, dir);
                    }
                } else {
                    enterBlock(enterable, newPos, dir);
                }
            } else if (!newState.getMaterial().isSolid()) {
                Block belowBlock = world.getBlockState(newPos.down()).getBlock();
                if (belowBlock instanceof IEnterableBlock) {
                    enterBlockFalling((IEnterableBlock) belowBlock, newPos);
                }
            }
        }
    }

    private EnumFacing getEntryDirection(BlockPos lastPos, BlockPos newPos) {
        int dx = lastPos.getX() - newPos.getX();
        int dy = lastPos.getY() - newPos.getY();
        int dz = lastPos.getZ() - newPos.getZ();

        if (dy == 0 && dz == 0) {
            if (dx > 0) return EnumFacing.EAST;
            if (dx < 0) return EnumFacing.WEST;
        } else if (dx == 0 && dz == 0) {
            if (dy > 0) return EnumFacing.UP;
            if (dy < 0) return EnumFacing.DOWN;
        } else if (dx == 0 && dy == 0) {
            if (dz > 0) return EnumFacing.SOUTH;
            if (dz < 0) return EnumFacing.NORTH;
        }
        return null;
    }

    public abstract void enterBlock(IEnterableBlock enterable, BlockPos pos, EnumFacing dir);

    public void enterBlockFalling(IEnterableBlock enterable, BlockPos pos) {
        this.enterBlock(enterable, pos.down(), EnumFacing.UP);
    }

    public abstract boolean onLeaveConveyor();

    public double getMoveSpeed() {
        return 0.0625D;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setVelocity(double motionX, double motionY, double motionZ) {
        this.velocityX = this.motionX = motionX;
        this.velocityY = this.motionY = motionY;
        this.velocityZ = this.motionZ = motionZ;
    }

    @Override
@SideOnly(Side.CLIENT)
public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    this.syncPosX = x;
    this.syncPosY = y;
    this.syncPosZ = z;
    this.turnProgress = posRotationIncrements + 2;
    this.motionX = this.velocityX;
    this.motionY = this.velocityY;
    this.motionZ = this.velocityZ;
}
}
