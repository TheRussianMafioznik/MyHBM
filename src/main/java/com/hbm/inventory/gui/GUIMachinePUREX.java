package com.hbm.inventory.gui;

import com.hbm.forgefluid.FFUtils;
import com.hbm.inventory.container.ContainerMachinePUREX;
import com.hbm.lib.RefStrings;
import com.hbm.tileentity.machine.TileEntityMachinePUREX;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

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

        for (int i = 0; i < 3; i++) {
            FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 8 + i * 18, guiTop + 18, 16, 52, purex.tanks[i], purex.types[i]);
        }

        this.drawElectricityInfo(this, mouseX, mouseY, guiLeft + 152, guiTop + 18, 16, 61, purex.power, purex.maxPower);
        super.renderHoveredToolTip(mouseX, mouseY);
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

        if (purex.didProcess) {
            drawTexturedModalRect(guiLeft + 51, guiTop + 121, 195, 0, 3, 6);
            drawTexturedModalRect(guiLeft + 56, guiTop + 121, 195, 0, 3, 6);
        }

        for (int i = 0; i < 3; i++) {
            if (purex.tanks[i].getFluid() != null && purex.tanks[i].getFluidAmount() > 0) {
                FFUtils.drawLiquid(purex.tanks[i], guiLeft, guiTop, this.zLevel, 16, 52, 8 + i * 18, 70);
            }
        }
    }
}
