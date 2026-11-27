package com.hbm.handler;

import com.hbm.inventory.AnvilRecipes;
import com.hbm.inventory.AnvilSmithingRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnvilUpgradeHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		if (world.isRemote) return;

		EntityPlayer player = event.getEntityPlayer();
		if (!player.isSneaking()) return;

		ItemStack held = player.getHeldItem(event.getHand());
		if (held.isEmpty() || 10 > held.getCount()) return;

		BlockPos pos = event.getPos();
		IBlockState oldState = world.getBlockState(pos);
		Item blockItem = Item.getItemFromBlock(oldState.getBlock());

		for (AnvilSmithingRecipe recipe : AnvilRecipes.getSmithing()) {
			boolean matchesAnvil = false;
			for (ItemStack leftStack : recipe.getLeft()) {
				if (leftStack.getItem() == blockItem) {
					matchesAnvil = true;
					break;
				}
			}
			if (!matchesAnvil) continue;

			boolean matchesMaterial = false;
			for (ItemStack rightStack : recipe.getRight()) {
				if (ItemStack.areItemsEqual(held, rightStack)) {
					matchesMaterial = true;
					break;
				}
			}

			if (matchesMaterial) {
				Block newBlock = Block.getBlockFromItem(recipe.getSimpleOutput().getItem());
				if (newBlock != null && newBlock != Blocks.AIR) {
					IBlockState newState = newBlock.getDefaultState();
					if (oldState.getPropertyKeys().contains(BlockHorizontal.FACING)
							&& newState.getPropertyKeys().contains(BlockHorizontal.FACING)) {
						newState = newState.withProperty(BlockHorizontal.FACING, oldState.getValue(BlockHorizontal.FACING));
					}

					world.setBlockState(pos, newState, 3);
					held.shrink(10);

					if (player instanceof EntityPlayerMP) {
						((EntityPlayerMP) player).inventoryContainer.detectAndSendChanges();
					}

					world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
					event.setCanceled(true);
					event.setCancellationResult(EnumActionResult.SUCCESS);
				}
				return;
			}
		}
	}
}
