package com.hbm.tileentity.machine;

import api.hbm.energy.IEnergyUser;
import com.hbm.forgefluid.FFUtils;
import com.hbm.inventory.PUREXRecipes;
import com.hbm.inventory.PUREXRecipes.PUREXRecipe;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import api.hbm.energy.IBatteryItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMachinePUREX extends TileEntityMachineBase implements ITickable, IEnergyUser, IFluidHandler {

    public FluidTank[] tanks;
    public Fluid[] types = new Fluid[3];

    public long power;
    public long maxPower = 1_000_000;
    public int progress = 0;
    public int maxProgress = 100;
    public boolean didProcess = false;

    public boolean frame = false;
    public int anim;
    public int prevAnim;

    private AxisAlignedBB bb = null;

    public TileEntityMachinePUREX() {
        super(13);
        tanks = new FluidTank[3];
        for (int i = 0; i < 3; i++) {
            tanks[i] = new FluidTank(24000);
        }
    }

    @Override
    public String getName() {
        return "container.machinePUREX";
    }

        @Override
    public void update() {
        if (!world.isRemote) {
            this.power = Library.chargeTEFromItems(inventory, 0, power, maxPower);
            this.updateStandardConnections(world, pos);

            didProcess = false;

            PUREXRecipe activeRecipe = null;
            int activeSlot = -1;

            for (int slot = 4; slot <= 6; slot++) {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!stack.isEmpty()) {
                    PUREXRecipe rec = PUREXRecipes.getRecipe(stack, tanks[0].getFluid(), tanks[1].getFluid(), tanks[2].getFluid());
                    if (rec != null && canFitOutputs(rec.outputs)) {
                        activeRecipe = rec;
                        activeSlot = slot;
                        break;
                    }
                }
            }

            if (activeRecipe != null) {
                int speedLvl = getUpgradeLevel(UpgradeType.SPEED);
                int powerLvl = getUpgradeLevel(UpgradeType.POWER);
                int overdriveLvl = getUpgradeLevel(UpgradeType.OVERDRIVE);

                double speedMult = 1.0 + (speedLvl / 3.0) + overdriveLvl;
                double powerMult = Math.max(0.25, 1.0 - (powerLvl * 0.25) + speedLvl + (overdriveLvl * 3.33));

                long reqPower = (long) (activeRecipe.powerPerTick * powerMult);
                this.maxProgress = Math.max(1, (int) (activeRecipe.duration / speedMult));

                if (this.power >= reqPower) {
                    this.power -= reqPower;
                    this.progress++;
                    this.didProcess = true;

                    if (this.progress >= this.maxProgress) {
                        this.progress = 0;
                        consumeInputs(activeSlot, activeRecipe);
                        produceOutputs(activeRecipe.outputs);
                        this.markDirty();
                    }
                }
            } else {
                this.progress = 0;
            }

            for (int i = 0; i < 3; i++) {
                types[i] = tanks[i].getFluid() != null ? tanks[i].getFluid().getFluid() : null;
            }

            this.networkPackNT(100);
        } else {
            this.prevAnim = this.anim;
            if (this.didProcess) this.anim++;

            if (world.getTotalWorldTime() % 20 == 0) {
                BlockPos upFive = pos.up(5);
                IBlockState state = world.getBlockState(upFive);
                frame = !state.getBlock().isAir(state, world, upFive);
            }
        }
    }

    private int getUpgradeLevel(UpgradeType type) {
        int level = 0;
        for (int i = 2; i <= 3; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemMachineUpgrade) {
                ItemMachineUpgrade upg = (ItemMachineUpgrade) stack.getItem();
                if (upg.type == type) level++;
            }
        }
        return Math.min(level, 3);
    }

    private boolean canFitOutputs(ItemStack[] outputs) {
        ItemStack[] sim = new ItemStack[6];
        for (int i = 0; i < 6; i++) {
            sim[i] = inventory.getStackInSlot(7 + i).copy();
        }

        for (ItemStack out : outputs) {
            int rem = out.getCount();
            for (int i = 0; i < 6; i++) {
                if (sim[i].isEmpty()) {
                    sim[i] = out.copy();
                    rem = 0;
                    break;
                } else if (ItemStack.areItemsEqual(sim[i], out) && ItemStack.areItemStackTagsEqual(sim[i], out)) {
                    int space = sim[i].getMaxStackSize() - sim[i].getCount();
                    int add = Math.min(space, rem);
                    sim[i].grow(add);
                    rem -= add;
                    if (rem <= 0) break;
                }
            }
            if (rem > 0) return false;
        }
        return true;
    }

    private void consumeInputs(int itemSlot, PUREXRecipe recipe) {
        inventory.extractItem(itemSlot, 1, false);
        for (FluidStack req : recipe.inputFluids) {
            for (int i = 0; i < 3; i++) {
                if (tanks[i].getFluid() != null && tanks[i].getFluid().getFluid() == req.getFluid()) {
                    tanks[i].drain(req.amount, true);
                    break;
                }
            }
        }
    }

    private void produceOutputs(ItemStack[] outputs) {
        for (ItemStack out : outputs) {
            ItemStack rem = out.copy();
            for (int i = 7; i <= 12; i++) {
                rem = inventory.insertItem(i, rem, false);
                if (rem.isEmpty()) break;
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeLong(maxPower);
        buf.writeInt(progress);
        buf.writeInt(maxProgress);
        buf.writeBoolean(didProcess);
        for (int i = 0; i < 3; i++) {
            FluidStack fs = tanks[i].getFluid();
            buf.writeInt(fs != null ? fs.amount : 0);
            String name = fs != null ? fs.getFluid().getName() : "";
            byte[] bytes = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.power = buf.readLong();
        this.maxPower = buf.readLong();
        this.progress = buf.readInt();
        this.maxProgress = buf.readInt();
        this.didProcess = buf.readBoolean();
        for (int i = 0; i < 3; i++) {
            int amount = buf.readInt();
            int len = buf.readInt();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            String name = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
            if (!name.isEmpty() && amount > 0) {
                Fluid f = net.minecraftforge.fluids.FluidRegistry.getFluid(name);
                if (f != null) {
                    tanks[i].setFluid(new FluidStack(f, amount));
                    types[i] = f;
                }
            } else {
                tanks[i].setFluid(null);
                types[i] = null;
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.power = nbt.getLong("power");
        this.progress = nbt.getInteger("progress");
        FFUtils.deserializeTankArray(nbt.getTagList("tanks", 10), tanks);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setLong("power", power);
        nbt.setInteger("progress", progress);
        nbt.setTag("tanks", FFUtils.serializeTankArray(tanks));
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 0) return stack.getItem() instanceof IBatteryItem;
        if (slot >= 2 && slot <= 3) return stack.getItem() instanceof ItemMachineUpgrade;
        if (slot >= 4 && slot <= 6) return PUREXRecipes.isValidInput(stack);
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return slot >= 7 && slot <= 12;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(EnumFacing side) {
        return new int[] { 4, 5, 6, 7, 8, 9, 10, 11, 12 };
    }

    @Override public long getPower() { return power; }
    @Override public void setPower(long power) { this.power = power; }
    @Override public long getMaxPower() { return maxPower; }

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
return new IFluidTankProperties[] { tanks[0].getTankProperties()[0], tanks[1].getTankProperties()[0], tanks[2].getTankProperties()[0] };
}
@Override
public int fill(FluidStack resource, boolean doFill) {
if (resource == null) return 0;
for (int i = 0; i < 3; i++) {
if (tanks[i].getFluid() != null && tanks[i].getFluid().getFluid() == resource.getFluid()) {
return tanks[i].fill(resource, doFill);
}
}
for (int i = 0; i < 3; i++) {
if (tanks[i].getFluid() == null) {
return tanks[i].fill(resource, doFill);
}
}
return 0;
}
@Override public FluidStack drain(FluidStack resource, boolean doDrain) { return null; }
@Override public FluidStack drain(int maxDrain, boolean doDrain) { return null; }
@Override
public AxisAlignedBB getRenderBoundingBox() {
if (bb == null) {
bb = new AxisAlignedBB(pos.getX() - 2, pos.getY(), pos.getZ() - 2, pos.getX() + 3, pos.getY() + 5, pos.getZ() + 3);
}
return bb;
}
@Override
@SideOnly(Side.CLIENT)
public double getMaxRenderDistanceSquared() {
return 65536.0D;
}
}