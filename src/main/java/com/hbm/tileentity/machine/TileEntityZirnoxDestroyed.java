package com.hbm.tileentity.machine;

import java.util.List;

import com.hbm.main.MainRegistry;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityZirnoxDestroyed extends TileEntity implements ITickable {

    private AxisAlignedBB bb;
    public boolean onFire = true;

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("onFire")) {
            onFire = nbt.getBoolean("onFire");
        } else {
            onFire = nbt.getBoolean("fire");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setBoolean("onFire", onFire);
        return super.writeToNBT(nbt);
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            radiate(world, this.pos.getX(), this.pos.getY(), this.pos.getZ());

            if (this.world.rand.nextInt(5000) == 0)
                onFire = false;

            if (onFire && this.world.getTotalWorldTime() % 50 == 0) {
                NBTTagCompound data = new NBTTagCompound();
                data.setString("type", "rbmkflame");
                data.setInteger("maxAge", 90);
                double px = pos.getX() + 0.25 + world.rand.nextDouble() * 0.5;
                double py = pos.getY() + 1.75;
                double pz = pos.getZ() + 0.25 + world.rand.nextDouble() * 0.5;
                PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, px, py, pz), new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 1.75, pos.getZ() + 0.5, 75));
                MainRegistry.proxy.effectNT(data);
                world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + world.rand.nextFloat(), world.rand.nextFloat() * 0.7F + 0.3F);
            }
        }
    }

    private void radiate(World world, int x, int y, int z) {
        float rads = onFire ? 500000F : 75000F;
        double range = 100D;

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(x + 0.5, y + 0.5, z + 0.5, x + 0.5, y + 0.5, z + 0.5).expand(range, range, range));

        for (EntityLivingBase e : entities) {
            Vec3d vec = new Vec3d(e.posX - (x + 0.5), (e.posY + e.getEyeHeight()) - (y + 0.5), e.posZ - (z + 0.5));
            double len = vec.length();
            vec = vec.normalize();

            float res = 0;
            for (int i = 1; i < len; i++) {
                int ix = (int) Math.floor(x + 0.5 + vec.x * i);
                int iy = (int) Math.floor(y + 0.5 + vec.y * i);
                int iz = (int) Math.floor(z + 0.5 + vec.z * i);
                BlockPos bPos = new BlockPos(ix, iy, iz);
                res += world.getBlockState(bPos).getBlockHardness(world, bPos);
            }

            if (res < 1) res = 1;

            float eRads = rads / res / (float) (len * len);
            ContaminationUtil.contaminate(e, HazardType.RADIATION, ContaminationType.CREATIVE, eRads);

            if (onFire && len < 5) {
                e.attackEntityFrom(DamageSource.ON_FIRE, 2);
            }
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (bb == null) bb = new AxisAlignedBB(pos.getX() - 3, pos.getY(), pos.getZ() - 3, pos.getX() + 4, pos.getY() + 3, pos.getZ() + 4);
        return bb;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0D;
    }
}