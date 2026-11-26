package com.hbm.tileentity.machine.oil;

import com.hbm.forgefluid.FFUtils;
import com.hbm.forgefluid.ModForgeFluids;
import com.hbm.interfaces.ITankPacketAcceptor;
import com.hbm.inventory.RefineryRecipes;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.Library;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.Tuple.Pair;

import api.hbm.energy.IEnergyUser;
import api.hbm.tile.IHeatSource;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMachineRefinery extends TileEntityMachineBase implements ITickable, IEnergyUser, IFluidHandler, ITankPacketAcceptor {

	public long power = 0;
	public int itemOutputTimer = 0;
	public static final int totalItemTime = 50;
	public static final long maxPower = 10000;

	public int heat = 0;
	public static final int maxHeat = 100_000;
	//one operation per tick, so this is also the TU per tick while running
	public static final int heatPerOp = 1_000;
	public static final double diffusion = 0.05D;
	public int age = 0;
	public boolean needsUpdate = false;
	public FluidTank[] tanks;
	public Fluid[] tankTypes;

	private int soundTimer = 0;
	private boolean isProcessing = false;

	public TileEntityMachineRefinery() {
		super(12);
		tanks = new FluidTank[5];
		tankTypes = new Fluid[] {ModForgeFluids.hotoil, ModForgeFluids.heavyoil, ModForgeFluids.naphtha, ModForgeFluids.lightoil, ModForgeFluids.petroleum};
		tanks[0] = new FluidTank(64000);
		tanks[1] = new FluidTank(24000);
		tanks[2] = new FluidTank(24000);
		tanks[3] = new FluidTank(24000);
		tanks[4] = new FluidTank(24000);
	}

	public String getName() {
		return "container.machineRefinery";
	}

		@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(nbt.hasKey("f")) {
			String fluidName = nbt.getString("f");
			this.tankTypes[0] = fluidName.isEmpty() ? null : FluidRegistry.getFluid(fluidName);
		}
		power = nbt.getLong("power");
		heat = nbt.getInteger("heat");
		itemOutputTimer = nbt.getInteger("itemOutputTimer");
		if(nbt.hasKey("tanks")) {
			FFUtils.deserializeTankArray(nbt.getTagList("tanks", 10), tanks);
		}
		for(int i = 0; i < tanks.length; i++) {
			if(tanks[i].getFluid() != null) {
				tankTypes[i] = tanks[i].getFluid().getFluid();
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		if(tankTypes[0] != null) {
			nbt.setString("f", tankTypes[0].getName());
		} else if(tanks[0].getFluid() != null) {
			nbt.setString("f", tanks[0].getFluid().getFluid().getName());
		} else {
			nbt.setString("f", "");
		}
		nbt.setLong("power", power);
		nbt.setInteger("heat", heat);
		nbt.setInteger("itemOutputTimer", itemOutputTimer);
		nbt.setTag("tanks", FFUtils.serializeTankArray(tanks));
		return nbt;
	}

		@Override
	public void onLoad() {
		super.onLoad();
		if (!world.isRemote) {
			for (BlockPos dummyPos : dummyBlocks) {
				if (world.isBlockLoaded(dummyPos)) {
					world.notifyNeighborsOfStateChange(dummyPos, world.getBlockState(dummyPos).getBlock(), false);
				}
			}
		}
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		this.readFromNBT(tag);
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			if(needsUpdate){
				needsUpdate = false;
			}
			this.updateConnections();
			power = Library.chargeTEFromItems(inventory, 0, power, maxPower);

			this.tryPullHeat();

			age++;
			if(age >= 20)
			{
				age = 0;
			}

			if(age == 1 || age == 11) {
				fillFluidInit(tanks[1]);
			}
			if(age == 2 || age == 12){
				fillFluidInit(tanks[2]);
			}
			if(age == 3 || age == 13){
				fillFluidInit(tanks[3]);
			}
			if(age == 4 || age == 14){
				fillFluidInit(tanks[4]);
			}
			if(this.inputValidForTank(0, 1))
				FFUtils.fillFromFluidContainer(inventory, tanks[0], 1, 2);

			isProcessing = canRefine();
			refine();

			FFUtils.fillFluidContainer(inventory, tanks[1], 3, 4);
			FFUtils.fillFluidContainer(inventory, tanks[2], 5, 6);
			FFUtils.fillFluidContainer(inventory, tanks[3], 7, 8);
			FFUtils.fillFluidContainer(inventory, tanks[4], 9, 10);

			if(isProcessing) {
				if(soundTimer <= 0) {
					world.playSound(null, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, HBMSoundHandler.machine_refinery_loop, SoundCategory.BLOCKS, 1.0F, 1.0F);
					soundTimer = 20;
				}
				soundTimer--;
			} else {
				soundTimer = 0;
			}

			detectAndSendChanges();

			//heat has no packet of its own, the buf sync covers it; the bar does not need 20Hz
			if(age % 5 == 0)
				networkPackNT(20);
		}
	}

		@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeLong(power);
		buf.writeInt(heat);
		buf.writeInt(itemOutputTimer);
		for (int i = 0; i < tanks.length; i++) {
			NBTTagCompound tag = new NBTTagCompound();
			tanks[i].writeToNBT(tag);
			net.minecraftforge.fml.common.network.ByteBufUtils.writeTag(buf, tag);
		}
	}

	@Override
	public void deserialize(ByteBuf buf) {
		super.deserialize(buf);
		this.power = buf.readLong();
		this.heat = buf.readInt();
		this.itemOutputTimer = buf.readInt();
		for (int i = 0; i < tanks.length; i++) {
			NBTTagCompound tag = net.minecraftforge.fml.common.network.ByteBufUtils.readTag(buf);
			if (tag != null) {
				tanks[i].readFromNBT(tag);
				if (tanks[i].getFluid() != null) {
					tankTypes[i] = tanks[i].getFluid().getFluid();
				}
			}
		}
	}

	private boolean canRefine() {
		Pair<FluidStack[], ItemStack> recipe = RefineryRecipes.getRecipe(tankTypes[0]);
		if(recipe == null) return false;
		FluidStack[] outputFluids = recipe.getKey();
		if(outputFluids == null) return false;

		return power >= 50 && heat >= heatPerOp && tanks[0].getFluidAmount() >= 100 &&
				tanks[1].getFluidAmount() + outputFluids[0].amount <= tanks[1].getCapacity() &&
				tanks[2].getFluidAmount() + outputFluids[1].amount <= tanks[2].getCapacity() &&
				tanks[3].getFluidAmount() + outputFluids[2].amount <= tanks[3].getCapacity() &&
				tanks[4].getFluidAmount() + outputFluids[3].amount <= tanks[4].getCapacity();
	}

	//heat comes from a heater directly below the core, same diffusion rate as the heat boiler
	private void tryPullHeat() {

		if(this.heat >= maxHeat) return;

		TileEntity con = world.getTileEntity(pos.down());

		if(con instanceof IHeatSource source) {
			int diff = source.getHeatStored() - this.heat;

			if(diff > 0) {
				diff = (int) Math.ceil(diff * diffusion);
				source.useUpHeat(diff);
				this.heat = Math.min(this.heat + diff, maxHeat);
			}
		}
	}

	private void updateConnections() {
		this.trySubscribe(world, pos.add(2, 0, 1), Library.POS_X);
		this.trySubscribe(world, pos.add(2, 0, -1), Library.POS_X);
		this.trySubscribe(world, pos.add(-2, 0, 1), Library.NEG_X);
		this.trySubscribe(world, pos.add(-2, 0, -1), Library.NEG_X);
		this.trySubscribe(world, pos.add(1, 0, 2), Library.POS_Z);
		this.trySubscribe(world, pos.add(-1, 0, 2), Library.POS_Z);
		this.trySubscribe(world, pos.add(1, 0, -2), Library.NEG_Z);
		this.trySubscribe(world, pos.add(-1, 0, -2), Library.NEG_Z);
	}

	private void setupTanks(FluidStack[] fluids){
		if(fluids != null){
			setTankType(1, fluids[0].getFluid());
			setTankType(2, fluids[1].getFluid());
			setTankType(3, fluids[2].getFluid());
			setTankType(4, fluids[3].getFluid());
		}
	}

	public void setTankType(int idx, Fluid type){
		if(tankTypes[idx] != type){
			tankTypes[idx] = type;
			if(type != null){
				tanks[idx].setFluid(new FluidStack(type, 0));
			}else {
				tanks[idx].setFluid(null);
			}
		}
	}

	private void refine(){
		Pair<FluidStack[], ItemStack> recipe = RefineryRecipes.getRecipe(tankTypes[0]);
		if(recipe == null || recipe.getKey() == null) return; // no recipe for current input fluid
		FluidStack[] outputFluids = recipe.getKey();
		ItemStack outputItem = recipe.getValue();
		setupTanks(outputFluids);

		if(power >= 50 && heat >= heatPerOp && tanks[0].getFluidAmount() >= 100 &&
				tanks[1].getFluidAmount() + outputFluids[0].amount <= tanks[1].getCapacity() &&
				tanks[2].getFluidAmount() + outputFluids[1].amount <= tanks[2].getCapacity() &&
				tanks[3].getFluidAmount() + outputFluids[2].amount <= tanks[3].getCapacity() &&
				tanks[4].getFluidAmount() + outputFluids[3].amount <= tanks[4].getCapacity()) {

			tanks[0].drain(100, true);
			tanks[1].fill(outputFluids[0].copy(), true);
			tanks[2].fill(outputFluids[1].copy(), true);
			tanks[3].fill(outputFluids[2].copy(), true);
			tanks[4].fill(outputFluids[3].copy(), true);
			itemOutputTimer += 1;
			power -= 50;
			heat -= heatPerOp;
			needsUpdate = true;
		}

		if(itemOutputTimer >= totalItemTime) {
			if(inventory.getStackInSlot(11).isEmpty()) {
				inventory.setStackInSlot(11, outputItem.copy());
				itemOutputTimer = 0;
			} else if(!inventory.getStackInSlot(11).isEmpty() && inventory.getStackInSlot(11).getItem() == outputItem.getItem() && inventory.getStackInSlot(11).getCount() < inventory.getStackInSlot(11).getMaxStackSize()) {
				inventory.getStackInSlot(11).grow(1);
				itemOutputTimer = 0;
			}
		}
	}

	private long detectPower;
	private FluidTank[] detectTanks = new FluidTank[]{null, null, null, null, null};

		private void detectAndSendChanges() {
		boolean mark = false;
		if(detectPower != power){
			mark = true;
			detectPower = power;
		}
		for (int i = 0; i < tanks.length; i++) {
			if(!FFUtils.areTanksEqual(tanks[i], detectTanks[i])){
				mark = true;
				needsUpdate = true;
				detectTanks[i] = FFUtils.copyTank(tanks[i]);
			}
		}
		if(mark) {
			markDirty();
		}
	}

	protected boolean inputValidForTank(int tank, int slot){

		if(!inventory.getStackInSlot(slot).isEmpty()){
			FluidStack containerFluid = FluidUtil.getFluidContained(inventory.getStackInSlot(slot));
			if(containerFluid != null){
				if(RefineryRecipes.getRecipe(containerFluid.getFluid()) != null){
					setTankType(tank, containerFluid.getFluid());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(EnumFacing e){
		return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
	}

	@Override
	public boolean canExtractItem(int i, ItemStack stack, int amount) {
		return i==2 || i==4 || i==6 || i==8 || i==10 || i==11;
	}

	public long getPowerScaled(long i) {
		return (power * i) / maxPower;
	}

	public int getHeatScaled(int i) {
		return (heat * i) / maxHeat;
	}

	@Override
	public void setPower(long i) {
		power = i;
	}

	@Override
	public long getPower() {
		return power;
	}

	@Override
	public long getMaxPower() {
		return maxPower;
	}

	public void fillFluidInit(FluidTank tank) {
		FFUtils.fillFluid(this, tank, world, pos.add(1, 0, -2), 2000);
		FFUtils.fillFluid(this, tank, world, pos.add(1, 0, 2), 2000);
		FFUtils.fillFluid(this, tank, world, pos.add(-1, 0, -2), 2000);
		FFUtils.fillFluid(this, tank, world, pos.add(-1, 0, 2), 2000);

		FFUtils.fillFluid(this, tank, world, pos.add(-2, 0, 1), 2000);
		FFUtils.fillFluid(this, tank, world, pos.add(2, 0, 1), 2000);
		FFUtils.fillFluid(this, tank, world, pos.add(-2, 0, -1), 2000);
		FFUtils.fillFluid(this, tank, world, pos.add(2, 0, -1), 2000);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return TileEntity.INFINITE_EXTENT_AABB;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared()
	{
		return 65536.0D;
	}

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return new IFluidTankProperties[]{tanks[0].getTankProperties()[0], tanks[1].getTankProperties()[0], tanks[2].getTankProperties()[0], tanks[3].getTankProperties()[0], tanks[4].getTankProperties()[0]};
	}

		@Override
	public int fill(FluidStack resource, boolean doFill) {
		if(resource == null)
			return 0;
		if(tankTypes[0] != null && resource.getFluid() == tankTypes[0]) {
			int filled = tanks[0].fill(resource, doFill);
			if(filled > 0 && doFill) this.markDirty();
			return filled;
		}
		if(tanks[0].getFluidAmount() == 0 && RefineryRecipes.getRecipe(resource.getFluid()) != null) {
			tankTypes[0] = resource.getFluid();
			int filled = tanks[0].fill(resource, doFill);
			if(doFill) this.markDirty();
			return filled;
		}
		return 0;
	}

	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		if(resource == null)
			return null;
		for(int i = 1; i <= 4; i++) {
			if(resource.isFluidEqual(tanks[i].getFluid())) {
				FluidStack drained = tanks[i].drain(resource.amount, doDrain);
				if(drained != null && drained.amount > 0 && doDrain) this.markDirty();
				return drained;
			}
		}
		return null;
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		for(int i = 1; i <= 4; i++) {
			if(tanks[i].getFluid() != null) {
				FluidStack drained = tanks[i].drain(maxDrain, doDrain);
				if(drained != null && drained.amount > 0 && doDrain) this.markDirty();
				return drained;
			}
		}
		return null;
	}

	@Override
	public void recievePacket(NBTTagCompound[] tags) {
		if(tags.length != 5){
			return;
		} else {
			tanks[0].readFromNBT(tags[0]);
			tanks[1].readFromNBT(tags[1]);
			tanks[2].readFromNBT(tags[2]);
			tanks[3].readFromNBT(tags[3]);
			tanks[4].readFromNBT(tags[4]);
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
		} else {
			return super.getCapability(capability, facing);
		}
	}
}
