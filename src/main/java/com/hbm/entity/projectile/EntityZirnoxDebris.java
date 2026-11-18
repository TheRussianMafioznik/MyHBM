package com.hbm.entity.projectile;

import java.util.List;

import com.hbm.items.ModItems;
import com.hbm.util.ContaminationUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.hbm.blocks.ModBlocks;

public class EntityZirnoxDebris extends Entity {

	public static final DataParameter<Integer> TYPE_ID = EntityDataManager.createKey(EntityZirnoxDebris.class, DataSerializers.VARINT);

	public float rot;
	public float lastRot;
	private boolean hasSizeSet = false;

	public EntityZirnoxDebris(World world) {
		super(world);
	}

	public EntityZirnoxDebris(World world, double x, double y, double z, DebrisType type) {
		super(world);
		this.setPosition(x, y, z);
		this.setType(type);
	}

	@Override
	protected void entityInit() {
		this.getDataManager().register(TYPE_ID, 0);
		this.rot = this.lastRot = this.rand.nextFloat() * 360;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
		if (!world.isRemote && !isDead) {
						switch (this.getType()) {
			case BLANK:
			case SHRAPNEL:
			case EXCHANGER:
				if (player.inventory.addItemStackToInventory(new ItemStack(ModItems.debris_metal)))
					this.setDead();
				break;
			case ELEMENT:
				if (player.inventory.addItemStackToInventory(new ItemStack(ModItems.debris_fuel)))
					this.setDead();
				break;
			case GRAPHITE:
				if (player.inventory.addItemStackToInventory(new ItemStack(ModItems.debris_graphite)))
					this.setDead();
				break;
			case CONCRETE:
				if (player.inventory.addItemStackToInventory(new ItemStack(ModBlocks.concrete_smooth)))
					this.setDead();
				break;
			}

			player.inventoryContainer.detectAndSendChanges();
		}

		return false;
	}

	@Override
	public void onUpdate() {
		if (!hasSizeSet) {
			switch (this.getType()) {
			case BLANK:
				this.setSize(0.5F, 0.5F);
				break;
			case ELEMENT:
				this.setSize(0.75F, 0.5F);
				break;
			case SHRAPNEL:
				this.setSize(0.5F, 0.5F);
				break;
			case GRAPHITE:
				this.setSize(0.25F, 0.25F);
				break;
			case CONCRETE:
				this.setSize(0.75F, 0.5F);
				break;
			case EXCHANGER:
				this.setSize(1F, 0.5F);
				break;
			}
			hasSizeSet = true;
		}

		if (!world.isRemote) {
			if (this.getType() == DebrisType.ELEMENT) {
				ContaminationUtil.radiate(world, this.posX, this.posY, this.posZ, 16, 150);
			}

			if (this.ticksExisted > getLifetime() + this.getEntityId() % 50) {
				this.setDead();
			}
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.motionY -= 0.04D;
		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

		this.lastRot = this.rot;

		if (this.onGround) {
			this.motionX *= 0.85D;
			this.motionZ *= 0.85D;
			this.motionY *= -0.5D;
		} else {
			this.rot += 10F;
			if (rot >= 360F) {
				this.rot -= 360F;
				this.lastRot -= 360F;
			}
		}
	}

	private int getLifetime() {
		switch (this.getType()) {
		case BLANK:
			return 3 * 60 * 20;
		case ELEMENT:
			return 10 * 60 * 20;
		case SHRAPNEL:
			return 15 * 60 * 20;
		case GRAPHITE:
			return 15 * 60 * 20;
		case CONCRETE:
			return 60 * 20;
		case EXCHANGER:
			return 60 * 20;
		default:
			return 0;
		}
	}

	public void setType(DebrisType type) {
		this.getDataManager().set(TYPE_ID, type.ordinal());
	}

	public DebrisType getType() {
		return DebrisType.VALUES[Math.abs(this.getDataManager().get(TYPE_ID)) % DebrisType.VALUES.length];
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.getDataManager().set(TYPE_ID, nbt.getInteger("debtype"));
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("debtype", this.getDataManager().get(TYPE_ID));
	}

	@Override
	public void move(MoverType type, double moX, double moY, double moZ) {
		this.world.profiler.startSection("move");

		if (this.isInWeb) {
			this.isInWeb = false;
		}

		double initMoX = moX;
		double initMoY = moY;
		double initMoZ = moZ;

		List<AxisAlignedBB> list = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(moX, moY, moZ));

		for (int i = 0; i < list.size(); ++i) {
			moY = list.get(i).calculateYOffset(this.getEntityBoundingBox(), moY);
		}

		this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, moY, 0.0D));

		if (initMoY != moY) {
			moZ = 0.0D;
			moY = 0.0D;
			moX = 0.0D;
		}

		boolean isGoingDown = this.onGround || initMoY != moY && initMoY < 0.0D;
		int j;

		for (j = 0; j < list.size(); ++j) {
			moX = list.get(j).calculateXOffset(this.getEntityBoundingBox(), moX);
		}

		this.setEntityBoundingBox(this.getEntityBoundingBox().offset(moX, 0.0D, 0.0D));

		for (j = 0; j < list.size(); ++j) {
			moZ = list.get(j).calculateZOffset(this.getEntityBoundingBox(), moZ);
		}

		this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, moZ));

		if (this.stepHeight > 0.0F && isGoingDown && (initMoX != moX || initMoZ != moZ)) {
			double d12 = moX;
			double d10 = moY;
			double d11 = moZ;
			moX = initMoX;
			moY = this.stepHeight;
			moZ = initMoZ;
			AxisAlignedBB axisalignedbb1 = this.getEntityBoundingBox();
			list = this.world.getCollisionBoxes(this, this.getEntityBoundingBox().expand(initMoX, moY, initMoZ));

			for (int k = 0; k < list.size(); ++k) {
				moY = list.get(k).calculateYOffset(this.getEntityBoundingBox(), moY);
			}

			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, moY, 0.0D));

			for (int k = 0; k < list.size(); ++k) {
				moX = list.get(k).calculateXOffset(this.getEntityBoundingBox(), moX);
			}

			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(moX, 0.0D, 0.0D));

			for (int k = 0; k < list.size(); ++k) {
				moZ = list.get(k).calculateZOffset(this.getEntityBoundingBox(), moZ);
			}

			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, moZ));

			moY = -this.stepHeight;

			for (int k = 0; k < list.size(); ++k) {
				moY = list.get(k).calculateYOffset(this.getEntityBoundingBox(), moY);
			}

			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, moY, 0.0D));

			if (d12 * d12 + d11 * d11 >= moX * moX + moZ * moZ) {
				moX = d12;
				moY = d10;
				moZ = d11;
				this.setEntityBoundingBox(axisalignedbb1);
			}
		}

		this.world.profiler.endSection();
		this.world.profiler.startSection("rest");
		this.posX = (this.getEntityBoundingBox().minX + this.getEntityBoundingBox().maxX) / 2.0D;
		this.posY = this.getEntityBoundingBox().minY + this.getYOffset();
		this.posZ = (this.getEntityBoundingBox().minZ + this.getEntityBoundingBox().maxZ) / 2.0D;
		this.collidedHorizontally = initMoX != moX || initMoZ != moZ;
		this.collidedVertically = initMoY != moY;
		this.onGround = initMoY != moY && initMoY < 0.0D;
		this.collided = this.collidedHorizontally || this.collidedVertically;
		int j6 = MathHelper.floor(this.posX);
		int i1 = MathHelper.floor(this.posY - 0.20000000298023224D);
		int k6 = MathHelper.floor(this.posZ);
		BlockPos blockpos = new BlockPos(j6, i1, k6);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		this.updateFallState(moY, this.onGround, iblockstate, blockpos);

		if (initMoX != moX) {
			this.motionX *= -0.75D;
		}

		if (initMoY != moY) {
			this.motionY = 0.0D;
		}

		if (initMoZ != moZ) {
			this.motionZ *= -0.75D;
		}

		try {
			this.doBlockCollisions();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
			this.addEntityCrashInfo(crashreportcategory);
			throw new ReportedException(crashreport);
		}

		this.world.profiler.endSection();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double dist) {
		int range = 128;
		return dist < range * range;
	}

	public static enum DebrisType {
		BLANK,
		ELEMENT,
		SHRAPNEL,
		GRAPHITE,
		CONCRETE,
		EXCHANGER;

		public static final DebrisType[] VALUES = values();
	}
}