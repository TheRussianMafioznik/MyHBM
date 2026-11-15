package com.hbm.inventory.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import com.hbm.forgefluid.FFUtils;
import com.hbm.inventory.container.ContainerReactorZirnox;
import com.hbm.lib.HBMSoundHandler;
import com.hbm.lib.RefStrings;
import com.hbm.packet.NBTControlPacket;
import com.hbm.packet.PacketDispatcher;
import com.hbm.tileentity.machine.TileEntityReactorZirnox;
import com.hbm.util.I18nUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTank;

public class GUIReactorZirnox extends GuiInfoContainer {

    private static final ResourceLocation texture = new ResourceLocation(RefStrings.MODID + ":textures/gui/reactors/gui_zirnox.png");
    private final TileEntityReactorZirnox zirnox;

    public GUIReactorZirnox(InventoryPlayer invPlayer, TileEntityReactorZirnox tile) {
        super(new ContainerReactorZirnox(invPlayer, tile));
        zirnox = tile;

        this.xSize = 203;
        this.ySize = 256;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float f) {
        super.drawScreen(mouseX, mouseY, f);
        super.renderHoveredToolTip(mouseX, mouseY);

        int idxSteam = 0;
        int idxCO2 = 1;
        int idxWater = 2;

        FluidTank tankSteam = zirnox.tanks[idxSteam];
        FluidTank tankCO2 = zirnox.tanks[idxCO2];
        FluidTank tankWater = zirnox.tanks[idxWater];

        Fluid typeSteam = zirnox.types[idxSteam];
        Fluid typeCO2 = zirnox.types[idxCO2];
        Fluid typeWater = zirnox.types[idxWater];

        FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 160, guiTop + 108, 18, 12, tankSteam, typeSteam);
        FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 142, guiTop + 108, 18, 12, tankCO2, typeCO2);
        FFUtils.renderTankInfo(this, mouseX, mouseY, guiLeft + 178, guiTop + 108, 18, 12, tankWater, typeWater);

        this.drawCustomInfo(this, mouseX, mouseY, guiLeft + 160, guiTop + 33, 18, 17, new String[] { "Temperature:", "   " + Math.round((zirnox.heat) * 0.00001 * 780 + 20) + "°C" });
        this.drawCustomInfo(this, mouseX, mouseY, guiLeft + 178, guiTop + 33, 18, 17, new String[] { "Pressure:", "   " + Math.round((zirnox.pressure) * 0.00001 * 30) + " bar" });

        String[] coolantText = I18nUtil.resolveKeyArray("desc.gui.zirnox.coolant");
        this.drawCustomInfoStat(mouseX, mouseY, guiLeft - 16, guiTop + 36, 16, 16, guiLeft - 8, guiTop + 36 + 16, coolantText);

        String[] pressureText = I18nUtil.resolveKeyArray("desc.gui.zirnox.pressure");
        this.drawCustomInfoStat(mouseX, mouseY, guiLeft - 16, guiTop + 36 + 16, 16, 16, guiLeft - 8, guiTop + 36 + 16 + 16, pressureText);

        if(tankCO2.getFluidAmount() <= 0) {
            String[] warning1 = I18nUtil.resolveKeyArray("desc.gui.zirnox.warning1");
            this.drawCustomInfoStat(mouseX, mouseY, guiLeft - 16, guiTop + 36 + 32, 16, 16, guiLeft - 8, guiTop + 36 + 32 + 16, warning1);
        }

        if(tankWater.getFluidAmount() < 4000) {
            String[] warning2 = I18nUtil.resolveKeyArray("desc.gui.zirnox.warning2");
            this.drawCustomInfoStat(mouseX, mouseY, guiLeft - 16, guiTop + 36 + 32 + 16, 16, 16, guiLeft - 8, guiTop + 36 + 32 + 16 + 16, warning2);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int i) throws IOException {
        super.mouseClicked(x, y, i);

        NBTTagCompound control = new NBTTagCompound();

        if(guiLeft + 144 <= x && guiLeft + 144 + 14 > x && guiTop + 35 < y && guiTop + 35 + 14 >= y) {
            control.setBoolean("control", true);
            PacketDispatcher.wrapper.sendToServer(new NBTControlPacket(control, zirnox.getPos().getX(), zirnox.getPos().getY(), zirnox.getPos().getZ()));
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(HBMSoundHandler.rbmk_az5_cover, 0.5F));
        }

        if(guiLeft + 151 <= x && guiLeft + 151 + 36 > x && guiTop + 51 < y && guiTop + 51 + 36 >= y) {
            control.setBoolean("vent", true);
            PacketDispatcher.wrapper.sendToServer(new NBTControlPacket(control, zirnox.getPos().getX(), zirnox.getPos().getY(), zirnox.getPos().getZ()));
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(HBMSoundHandler.rbmk_az5_cover, 0.5F));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int i, int j) {
        String name = this.zirnox.hasCustomInventoryName() ? this.zirnox.getInventoryName() : I18n.format(this.zirnox.getInventoryName());

        this.fontRenderer.drawString(name, this.xSize / 2 - this.fontRenderer.getStringWidth(name) / 2, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        super.drawDefaultBackground();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int idxCO2 = 1;
        int idxWater = 2;

        int steamScale = zirnox.getGaugeScaled(14, 0);
        drawRect(guiLeft + 162, guiTop + 113, guiLeft + 162 + steamScale, guiTop + 115, 0xFF7F0000);

        int co2Scale = zirnox.getGaugeScaled(14, 1);
        drawRect(guiLeft + 144, guiTop + 113, guiLeft + 144 + co2Scale, guiTop + 115, 0xFF7F0000);

        int waterScale = zirnox.getGaugeScaled(14, 2);
        drawRect(guiLeft + 180, guiTop + 113, guiLeft + 180 + waterScale, guiTop + 115, 0xFF7F0000);

        int heatScale = zirnox.getGaugeScaled(12, 3);
        drawRect(guiLeft + 168, guiTop + 47 - heatScale, guiLeft + 170, guiTop + 47, 0xFF7F0000);

        int pressureScale = zirnox.getGaugeScaled(12, 4);
        drawRect(guiLeft + 186, guiTop + 47 - pressureScale, guiLeft + 188, guiTop + 47, 0xFF7F0000);

        if(zirnox.isOn) {
            for(int x = 0; x < 4; x++)
                for(int y = 0; y < 4; y++)
                    drawTexturedModalRect(guiLeft + 7 + 36 * x, guiTop + 15 + 36 * y, 238, 238, 18, 18);
            for(int x = 0; x < 3; x++)
                for(int y = 0; y < 3; y++)
                    drawTexturedModalRect(guiLeft + 25 + 36 * x, guiTop + 33 + 36 * y, 238, 238, 18, 18);
            drawTexturedModalRect(guiLeft + 142, guiTop + 15, 220, 238, 18, 18);
        }

        this.drawInfoPanel(guiLeft - 16, guiTop + 36, 16, 16, 2);
        this.drawInfoPanel(guiLeft - 16, guiTop + 36 + 16, 16, 16, 3);

        if(zirnox.tanks[idxCO2].getFluidAmount() <= 0)
            this.drawInfoPanel(guiLeft - 16, guiTop + 36 + 32, 16, 16, 6);

        if(zirnox.tanks[idxWater].getFluidAmount() <= 4000)
            this.drawInfoPanel(guiLeft - 16, guiTop + 36 + 32 + 16, 16, 16, 6);
    }
}