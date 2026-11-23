package com.hbm.blocks.network.energy;

import javax.annotation.Nullable;

import com.hbm.tileentity.network.energy.TileEntityConnector;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

// Note: needs a dummy baked model to override break particles
public class ConnectorRedWire extends PylonBase {

    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    private static final double f = 1d / 16d;
    private static final double min = 5 * f;
    private static final double max = 11 * f;

    private static final AxisAlignedBB AABB_UP = new AxisAlignedBB(min, 0.0D, min, max, max, max);
    private static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(min, min, min, max, 1.0D, max);
    private static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(min, min, 0.0D, max, max, max);
    private static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(min, min, min, max, max, 1.0D);
    private static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, min, min, max, max, max);
    private static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(min, min, min, 1.0D, max, max);

    public ConnectorRedWire(Material mat, String reg) {
        super(mat, reg);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityConnector();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta & 7);
        return this.getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        switch (state.getValue(FACING)) {
            case NORTH:
                return AABB_NORTH;
            case SOUTH:
                return AABB_SOUTH;
            case WEST:
                return AABB_WEST;
            case EAST:
                return AABB_EAST;
            case DOWN:
                return AABB_DOWN;
            case UP:
            default:
                return AABB_UP;
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return this.getBoundingBox(state, worldIn, pos);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
