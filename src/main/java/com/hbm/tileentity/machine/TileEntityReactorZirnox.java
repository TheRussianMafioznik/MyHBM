package com.hbm.tileentity.machine;

import java.util.HashMap;
import java.util.List;

import com.hbm.blocks.BlockDummyable;
import com.hbm.config.MobConfig;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.RecipesCommon.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemZirnoxRod;
import com.hbm.items.machine.ItemZirnoxRod.EnumZirnoxType;
import com.hbm.items.machine.ItemZirnoxRodDepleted.EnumZirnoxTypeDepleted;
import com.hbm.lib.ForgeDirection;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.main.MainRegistry;
import com.hbm.packet.AuxParticlePacketNT;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.TileEntityMachineBase;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReactorZirnox extends TileEntityMachineBase implements ITickable, IControlReceiver, IFluidHandler {

    private AxisAlignedBB bb;
    public static final int maxHeat = 100000;
    private boolean redstonePowered = false;
    public static final int maxPressure = 100000;
    public static final HashMap<ComparableStack, ItemStack> fuelMap = new HashMap<ComparableStack, ItemStack>();
    private static final int[] slots_io = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};

    static {
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.NATURAL_URANIUM_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.NATURAL_URANIUM_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.URANIUM_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.URANIUM_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.TH232_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox, 1, EnumZirnoxType.THORIUM_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.THORIUM_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.THORIUM_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.MOX_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.MOX_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.PLUTONIUM_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.PLUTONIUM_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.U233_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.U233_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.U235_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.U235_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.LES_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.LES_FUEL.ordinal()));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.LITHIUM_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_tritium));
        fuelMap.put(new ComparableStack(ModItems.rod_zirnox, 1, EnumZirnoxType.ZFB_MOX_FUEL.ordinal()), new ItemStack(ModItems.rod_zirnox_depleted, 1, EnumZirnoxTypeDepleted.ZFB_MOX_FUEL.ordinal()));
    }

    public int heat;
    public int pressure;
    public boolean isOn = false;
    public FluidTank[] tanks;
    public Fluid[] types = new Fluid[3];
    protected int output;

    public TileEntityReactorZirnox() {
        super(28);
        tanks = new FluidTank[3];
        // 0: Superhot Steam, 1: Carbon Dioxide, 2: Water
        tanks[0] = new FluidTank(ModForgeFluids.superhotsteam, 0, 8000);
        types[0] = ModForgeFluids.superhotsteam;

        tanks[1] = new FluidTank(ModForgeFluids.carbondioxide, 0, 16000);
        types[1] = ModForgeFluids.carbondioxide;

        tanks[2] = new FluidTank(FluidRegistry.WATER, 0, 32000);
        types[2] = FluidRegistry.WATER;
    }

    public void setRedstonePowered(boolean powered) {
        if (!powered && this.redstonePowered) {
            isOn = false;
        }
        this.redstonePowered = powered;
    }

    @Override
    public String getName() {
        return "container.zirnox";
    }

    @Override
    public int[] getAccessibleSlotsFromSide(EnumFacing e) {
        return slots_io;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack) {
        return i < 24 && stack.getItem() instanceof ItemZirnoxRod;
    }

    @Override
    public boolean canExtractItem(int i, ItemStack stack, int j) {
        return i < 24 && !(stack.getItem() instanceof ItemZirnoxRod);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        heat = nbt.getInteger("heat");
        pressure = nbt.getInteger("pressure");
        isOn = nbt.getBoolean("isOn");
        redstonePowered = nbt.getBoolean("redstonePowered");
        FFUtils.deserializeTankArray(nbt.getTagList("tanks", 10), tanks);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("heat", heat);
        nbt.setInteger("pressure", pressure);
        nbt.setBoolean("isOn", isOn);
        nbt.setBoolean("redstonePowered", redstonePowered);
        nbt.setTag("tanks", FFUtils.serializeTankArray(tanks));
        return super.writeToNBT(nbt);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(this.heat);
        buf.writeInt(this.pressure);
        buf.writeBoolean(this.isOn);
        buf.writeBoolean(this.redstonePowered);
        for (int i = 0; i < 3; i++) {
            buf.writeInt(tanks[i].getFluidAmount());
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.heat = buf.readInt();
        this.pressure = buf.readInt();
        this.isOn = buf.readBoolean();
        this.redstonePowered = buf.readBoolean();
        for (int i = 0; i < 3; i++) {
            int amount = buf.readInt();
            if (types[i] != null) {
                tanks[i].setFluid(new FluidStack(types[i], amount));
            }
        }
    }

    public int getGaugeScaled(int i, int type) {
        switch (type) {
            case 0: return (tanks[0].getFluidAmount() * i) / tanks[0].getCapacity();
            case 1: return (tanks[1].getFluidAmount() * i) / tanks[1].getCapacity();
            case 2: return (tanks[2].getFluidAmount() * i) / tanks[2].getCapacity();
            case 3: return (this.heat * i) / maxHeat;
            case 4: return (this.pressure * i) / maxPressure;
            default: return 1;
        }
    }

    private int[] getNeighbouringSlots(int id) {
        switch (id) {
            case 0: return new int[]{1, 7};
            case 1: return new int[]{0, 2, 8};
            case 2: return new int[]{1, 9};
            case 3: return new int[]{4, 10};
            case 4: return new int[]{3, 5, 11};
            case 5: return new int[]{4, 6, 12};
            case 6: return new int[]{5, 13};
            case 7: return new int[]{0, 8, 14};
            case 8: return new int[]{1, 7, 9, 15};
            case 9: return new int[]{2, 8, 16};
            case 10: return new int[]{3, 11, 17};
            case 11: return new int[]{4, 10, 12, 18};
            case 12: return new int[]{5, 11, 13, 19};
            case 13: return new int[]{6, 12, 20};
            case 14: return new int[]{7, 15, 21};
            case 15: return new int[]{8, 14, 16, 22};
            case 16: return new int[]{9, 15, 23};
            case 17: return new int[]{10, 18};
            case 18: return new int[]{11, 17, 19};
            case 19: return new int[]{12, 18, 20};
            case 20: return new int[]{13, 19};
            case 21: return new int[]{14, 22};
            case 22: return new int[]{15, 21, 23};
            case 23: return new int[]{16, 22};
            default: return null;
        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (redstonePowered) {
                isOn = true;
            }
            this.output = 0;

            FFUtils.fillFromFluidContainer(inventory, tanks[1], 24, 26);
            FFUtils.fillFromFluidContainer(inventory, tanks[2], 25, 27);

            if (isOn) {
                for (int i = 0; i < 24; i++) {
                    if (!inventory.getStackInSlot(i).isEmpty()) {
                        if (inventory.getStackInSlot(i).getItem() instanceof ItemZirnoxRod)
                            decay(i);
                        else if (inventory.getStackInSlot(i).getItem() == ModItems.meteorite_sword_bred)
inventory.setStackInSlot(i, new ItemStack(ModItems.meteorite_sword_irradiated));
}
}
}
this.pressure = (this.tanks[1].getFluidAmount() * 2) + (int) ((float) this.heat * ((float) this.tanks[1].getFluidAmount() / (float) this.tanks[1].getCapacity()));
if (this.heat > 0 && this.heat < maxHeat) {
if (this.tanks[2].getFluidAmount() > 0 && this.tanks[1].getFluidAmount() > 0 && this.tanks[0].getFluidAmount() < this.tanks[0].getCapacity()) {
generateSteam();
this.heat -= (int) ((float) this.heat * (float) this.pressure / 1000000F);
} else {
this.heat -= 10;
}
}
fillFluidInit(tanks[0]);
checkIfMeltdown();
this.networkPackNT(150);
}
}
private void generateSteam() {
if (this.heat > 10256) {
int cycle = (int) ((((float) heat - 10256F) / (float) maxHeat) * Math.min(((float) tanks[1].getFluidAmount() / 14000F), 1F) * 25F * 7.5F);
this.output = cycle;
tanks[2].drain(cycle, true);
tanks[0].fill(new FluidStack(types[0], cycle), true);
}
}
private boolean hasFuelRod(int id) {
if (!inventory.getStackInSlot(id).isEmpty()) {
if (inventory.getStackInSlot(id).getItem() instanceof ItemZirnoxRod) {
EnumZirnoxType num = ItemZirnoxRod.getEnum(inventory.getStackInSlot(id).getItemDamage());
return !num.breeding;
}
}
return false;
}
private int getNeighbourCount(int id) {
int[] neighbours = this.getNeighbouringSlots(id);
if (neighbours == null) return 0;
int count = 0;
for (int neighbour : neighbours) {
if (hasFuelRod(neighbour)) count++;
}
return count;
}
private void decay(int id) {
int decay = getNeighbourCount(id);
ItemStack zirnoxRodItemStack = inventory.getStackInSlot(id);
EnumZirnoxType num = ItemZirnoxRod.getEnum(zirnoxRodItemStack.getItemDamage());
if (!num.breeding)
decay++;
for (int i = 0; i < decay; i++) {
this.heat += num.heat;
ItemZirnoxRod.incrementLifeTime(zirnoxRodItemStack);
if (ItemZirnoxRod.getLifeTime(zirnoxRodItemStack) > num.maxLife) {
ItemStack depleted = fuelMap.get(new ComparableStack(zirnoxRodItemStack));
inventory.setStackInSlot(id, depleted != null ? depleted.copy() : ItemStack.EMPTY);
break;
}
}
}
private void checkIfMeltdown() {
if (this.pressure > maxPressure || this.heat > maxHeat) {
meltdown();
}
}
private void meltdown() {
for (int i = 0; i < inventory.getSlots(); i++) {
this.inventory.setStackInSlot(i, ItemStack.EMPTY);
}
NBTTagCompound data = new NBTTagCompound();
data.setString("type", "rbmkmush");
data.setFloat("scale", 4);
PacketDispatcher.wrapper.sendToAllAround(new AuxParticlePacketNT(data, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), new TargetPoint(world.provider.getDimension(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 250));
MainRegistry.proxy.effectNT(data);
for (int ox = -2; ox <= 2; ox++) {
for (int oz = -2; oz <= 2; oz++) {
for (int oy = 0; oy <= 4; oy++) {
world.setBlockToAir(pos.add(ox, oy, oz));
}
}
}
world.playSound(null, pos.getX(), pos.getY() + 2, pos.getZ(), HBMSoundHandler.rbmk_explosion, SoundCategory.BLOCKS, 10.0F, 1.0F);
world.createExplosion(null, pos.getX(), pos.getY() + 3, pos.getZ(), 12.0F, true);
ExplosionNukeGeneric.waste(world, pos.getX(), pos.getY(), pos.getZ(), 35);
        if (MobConfig.enableElementals) {
            List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(100));
            for (EntityPlayer player : players) {
                player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setBoolean("radMark", true);
            }
        }
}
public void fillFluidInit(FluidTank tank) {
ForgeDirection dir = ForgeDirection.getOrientation(this.getBlockMetadata() - BlockDummyable.offset);
ForgeDirection rot = dir.getRotation(ForgeDirection.UP);
FFUtils.fillFluid(this, tank, world, pos.add(rot.offsetX * 3, 1, rot.offsetZ * 3), 8000);
FFUtils.fillFluid(this, tank, world, pos.add(rot.offsetX * 3, 3, rot.offsetZ * 3), 8000);
FFUtils.fillFluid(this, tank, world, pos.add(rot.offsetX * -3, 1, rot.offsetZ * -3), 8000);
FFUtils.fillFluid(this, tank, world, pos.add(rot.offsetX * -3, 3, rot.offsetZ * -3), 8000);
}
@Override
public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
return super.getCapability(capability, facing);
}
@Override
public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
}
@Override
public IFluidTankProperties[] getTankProperties() {
return new IFluidTankProperties[]{tanks[0].getTankProperties()[0], tanks[1].getTankProperties()[0], tanks[2].getTankProperties()[0]};
}
@Override
public int fill(FluidStack resource, boolean doFill) {
if (resource == null) return 0;
if (resource.getFluid() == types[1]) return tanks[1].fill(resource, doFill);
if (resource.getFluid() == types[2]) return tanks[2].fill(resource, doFill);
return 0;
}
@Override
public FluidStack drain(FluidStack resource, boolean doDrain) {
if (resource != null && resource.getFluid() == types[0]) {
return tanks[0].drain(resource, doDrain);
}
return null;
}
@Override
public FluidStack drain(int maxDrain, boolean doDrain) {
return tanks[0].drain(maxDrain, doDrain);
}
@Override
public boolean hasPermission(EntityPlayer player) {
return player.getDistanceSq(pos) <= 400;
}
@Override
public void receiveControl(NBTTagCompound data) {
if (data.hasKey("control") && !redstonePowered) {
this.isOn = !this.isOn;
}
if (data.hasKey("vent")) {
this.tanks[1].drain(1000, true);
}
this.markDirty();
}
@Override
public AxisAlignedBB getRenderBoundingBox() {
if (bb == null) bb = new AxisAlignedBB(pos.getX() - 2, pos.getY(), pos.getZ() - 2, pos.getX() + 3, pos.getY() + 5, pos.getZ() + 3);
return bb;
}
@SideOnly(Side.CLIENT)
public double getMaxRenderDistanceSquared() {
return 65536.0D;
}
}