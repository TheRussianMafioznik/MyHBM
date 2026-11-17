package com.hbm.inventory.gui;

import java.io.IOException;
import java.util.List;

import com.hbm.forgefluid.FFUtils;
import com.hbm.inventory.PUREXRecipes;
import com.hbm.inventory.PUREXRecipes.PUREXRecipe;
import com.hbm.inventory.container.ContainerMachinePUREX;
import com.hbm.items.ModItems;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityMachinePUREX;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GUIMachinePUREX extends GuiInfoContainer {

    private static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/processing/gui_purex.png");
    private final TileEntityMachinePUREX purex;

    public GUIMachinePUREX(InventoryPlayer invPlayer, TileEntityMachinePUREX tile) {
        super(new ContainerMachinePUREX(invPlayer, tile));
        this.purex = tile;

        this.xSize = 176;
        this.ySize = 256;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);

        int outIdx = 3;
        for (int i = 0; i < 3; i++) {
            FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 8 + i * 18, guiTop + 18, 16, 52, purex.tanks[i], purex.types[i]);
        }
        FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 116, guiTop + 36, 16, 52, purex.tanks[outIdx], purex.types[outIdx]);

        this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 152, guiTop + 18, 16, 61, purex.power, purex.maxPower);

                if (guiLeft + 7 <= mouseX && guiLeft + 7 + 18 > mouseX && guiTop + 125 < mouseY && guiTop + 125 + 18 >= mouseY) {
            PUREXRecipe recipe = PUREXRecipes.getRecipeById(this.purex.selectedRecipe);
            if (recipe != null) {
                this.drawHoveringText(GUIScreenPUREXSelector.getRecipeTooltipLines(recipe), mouseX, mouseY);
            } else {
                this.drawHoveringText(TextFormatting.YELLOW + "Нажмите, чтобы выбрать рецепт (или авто-режим)", mouseX, mouseY);
            }
        }

        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);

        if (guiLeft + 7 <= x && guiLeft + 7 + 18 > x && guiTop + 125 < y && guiTop + 125 + 18 >= y) {
            GUIScreenPUREXSelector.openSelector(purex, purex.selectedRecipe, this);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j) {
        String name = this.purex.hasCustomInventoryName() ? this.purex.getInventoryName() : I18n.format(this.purex.getInventoryName());

        this.fontRenderer.drawString(name, 70 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawDefaultBackground();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int p = (int) (purex.power * 61 / purex.maxPower);
        drawTexturedModalRect(guiLeft + 152, guiTop + 79 - p, 176, 61 - p, 16, p);

        if (purex.progress > 0 && purex.maxProgress > 0) {
            int j = (int) Math.ceil(70.0 * purex.progress / purex.maxProgress);
            drawTexturedModalRect(guiLeft + 62, guiTop + 126, 176, 61, j, 16);
        }

        PUREXRecipe recipe = PUREXRecipes.getRecipeById(purex.selectedRecipe);

        if (purex.didProcess) {
            drawTexturedModalRect(guiLeft + 51, guiTop + 121, 195, 0, 3, 6);
            drawTexturedModalRect(guiLeft + 56, guiTop + 121, 195, 0, 3, 6);
        } else if (recipe != null) {
            drawTexturedModalRect(guiLeft + 51, guiTop + 121, 192, 0, 3, 6);
            if (purex.power >= recipe.powerPerTick) {
                drawTexturedModalRect(guiLeft + 56, guiTop + 121, 192, 0, 3, 6);
            }
        }

        // Иконка выбранного рецепта или синяя книжечка (template_folder)
        ItemStack icon = (recipe != null) ? recipe.getIcon() : new ItemStack(ModItems.template_folder);
        renderGuiItem(icon, 8, 126);

        // Полупрозрачные призраки входных предметов в слотах 4-6
        if (recipe != null && recipe.inputs != null) {
            for (int i = 0; i < recipe.inputs.length && i < 3; i++) {
                Slot slot = this.inventorySlots.inventorySlots.get(4 + i);
                if (!slot.getHasStack()) {
                    List<ItemStack> variants = PUREXRecipes.getVariants(recipe.inputs[i]);
                    if (!variants.isEmpty()) {
                        int idx = (int) ((System.currentTimeMillis() / 1000L) % variants.size());
                        renderGuiItem(variants.get(idx), slot.xPos, slot.yPos);
                    }
                }
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(1F, 1F, 1F, 0.5F);
            GlStateManager.enableBlend();
            this.zLevel = 300F;
            for (int i = 0; i < recipe.inputs.length && i < 3; i++) {
                Slot slot = this.inventorySlots.inventorySlots.get(4 + i);
                if (!slot.getHasStack()) {
                    drawTexturedModalRect(guiLeft + slot.xPos, guiTop + slot.yPos, slot.xPos, slot.yPos, 16, 16);
                }
            }
            this.zLevel = 0F;
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.disableBlend();
        }

        int outIdx = 3;
        for (int i = 0; i < 3; i++) {
            if (purex.tanks[i].getFluid() != null && purex.tanks[i].getFluidAmount() > 0) {
                FFUtils.drawLiquid(purex.tanks[i], guiLeft, guiTop, this.zLevel, 16, 52, 8 + i * 18, 70);
            }
        }
        if (purex.tanks[outIdx].getFluid() != null && purex.tanks[outIdx].getFluidAmount() > 0) {
            FFUtils.drawLiquid(purex.tanks[outIdx], guiLeft, guiTop, this.zLevel, 16, 52, 116, 88);
        }
    }

    private void renderGuiItem(ItemStack stack, int x, int y) {
        if (stack == null || stack.isEmpty()) return;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GlStateManager.enableRescaleNormal();

        itemRender.zLevel = 100.0F;
        itemRender.renderItemAndEffectIntoGUI(stack, guiLeft + x, guiTop + y);
        itemRender.zLevel = 0.0F;

        GlStateManager.enableAlpha();
        GlStateManager.disableLighting();
    }
}