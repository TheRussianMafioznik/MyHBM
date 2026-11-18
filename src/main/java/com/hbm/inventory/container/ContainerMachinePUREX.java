package com.hbm.inventory.container;

import com.hbm.inventory.SlotMachineOutput;
import com.hbm.items.machine.ItemMachineUpgrade;
import api.hbm.energy.IBatteryItem;
import com.hbm.tileentity.machine.TileEntityMachinePUREX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerMachinePUREX extends Container {

    private final TileEntityMachinePUREX purex;

    public ContainerMachinePUREX(InventoryPlayer invPlayer, TileEntityMachinePUREX te) {
        this.purex = te;

        // Battery (0)
        this.addSlotToContainer(new SlotItemHandler(te.inventory, 0, 152, 81));
        // Schematic / Reserved (1)
        this.addSlotToContainer(new SlotItemHandler(te.inventory, 1, 35, 126));
        // Upgrades (2-3)
        this.addSlotToContainer(new SlotItemHandler(te.inventory, 2, 152, 108));
        this.addSlotToContainer(new SlotItemHandler(te.inventory, 3, 152, 126));

        // Solid Input (4-6)
        for (int i = 0; i < 3; i++) {
            this.addSlotToContainer(new SlotItemHandler(te.inventory, 4 + i, 8 + i * 18, 90));
        }

                // Выходные слоты (7-12): 2 столбца (x = 80, 98) и 3 ряда (y = 36, 54, 72)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                this.addSlotToContainer(new SlotItemHandler(te.inventory, 7 + i * 2 + j, 80 + j * 18, 36 + i * 18));
            }
        }

        // Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 174 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 232));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();

            if (index < 13) {
                if (!this.mergeItemStack(stack, 13, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (copy.getItem() instanceof IBatteryItem) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
                } else if (copy.getItem() instanceof ItemMachineUpgrade) {
                    if (!this.mergeItemStack(stack, 2, 4, false)) return ItemStack.EMPTY;
                } else {
                    if (!this.mergeItemStack(stack, 4, 7, false)) return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return copy;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return purex.isUseableByPlayer(player);
    }
}