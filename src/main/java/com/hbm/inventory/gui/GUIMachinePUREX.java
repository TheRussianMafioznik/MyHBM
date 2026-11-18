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

                PUREXRecipe activeSel = PUREXRecipes.getRecipeById(this.purex.selectedRecipe);
        for (int i = 0; i < 3; i++) {
            net.minecraftforge.fluids.Fluid expected = (activeSel != null && i < activeSel.inputFluids.length) ? activeSel.inputFluids[i].getFluid() : purex.types[i];
            FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 8 + i * 18, guiTop + 18, 16, 52, purex.tanks[i], expected);
        }
        int outIdx = 3;
        net.minecraftforge.fluids.Fluid expectedOut = (activeSel != null && activeSel.outputFluid != null) ? activeSel.outputFluid.getFluid() : purex.types[outIdx];
        FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 116, guiTop + 36, 16, 52, purex.tanks[outIdx], expectedOut);

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

                // Полупрозрачные призраки входных (4-6) и выходных (7-12) предметов
        if (recipe != null) {
            if (recipe.inputs != null) {
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
            }
            if (recipe.outputs != null) {
                for (int i = 0; i < recipe.outputs.length && i < 6; i++) {
                    Slot slot = this.inventorySlots.inventorySlots.get(7 + i);
                    if (!slot.getHasStack()) {
                        renderGuiItem(recipe.outputs[i], slot.xPos, slot.yPos);
                    }
                }
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.color(1F, 1F, 1F, 0.5F);
            GlStateManager.enableBlend();
            this.zLevel = 300F;
            if (recipe.inputs != null) {
                for (int i = 0; i < recipe.inputs.length && i < 3; i++) {
                    Slot slot = this.inventorySlots.inventorySlots.get(4 + i);
                    if (!slot.getHasStack()) {
                        drawTexturedModalRect(guiLeft + slot.xPos, guiTop + slot.yPos, slot.xPos, slot.yPos, 16, 16);
                    }
                }
            }
            if (recipe.outputs != null) {
                for (int i = 0; i < recipe.outputs.length && i < 6; i++) {
                    Slot slot = this.inventorySlots.inventorySlots.get(7 + i);
                    if (!slot.getHasStack()) {
                        drawTexturedModalRect(guiLeft + slot.xPos, guiTop + slot.yPos, slot.xPos, slot.yPos, 16, 16);
                    }
                }
            }
            this.zLevel = 0F;
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.disableBlend();
        }

                        int outIdx = 3;
        for (int i = 0; i < 3; i++) {
            renderTankFluid(purex.tanks[i], guiLeft + 8 + i * 18, guiTop + 18, 16, 52);
        }
        renderTankFluid(purex.tanks[outIdx], guiLeft + 116, guiTop + 36, 16, 52);
    }

    private void renderTankFluid(net.minecraftforge.fluids.FluidTank tank, int x, int y, int width, int height) {
        if (tank == null || tank.getFluid() == null || tank.getFluidAmount() <= 0) return;
        net.minecraftforge.fluids.FluidStack fs = tank.getFluid();
        net.minecraftforge.fluids.Fluid fluid = fs.getFluid();
        if (fluid == null || fluid.getStill(fs) == null) return;

        net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill(fs).toString());
        Minecraft.getMinecraft().getTextureManager().bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);

        int color = fluid.getColor(fs);
        float r = ((color >> 16) & 0xFF) / 255.0F;
        float g = ((color >> 8) & 0xFF) / 255.0F;
        float b = (color & 0xFF) / 255.0F;
        GlStateManager.color(r, g, b, 1.0F);

        int filledHeight = (int) Math.ceil((double) fs.amount * height / Math.max(1, tank.getCapacity()));
        int drawY = y + height - filledHeight;

        for (int dy = 0; dy < filledHeight; dy += 16) {
            int sliceH = Math.min(16, filledHeight - dy);
            drawTexturedModalRect(x, drawY + dy, sprite, width, sliceH);
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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