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

        int idxSteam = 0;
        int idxCO2 = 1;
        int idxWater = 2;

        // Линейные красные индикаторы для CO2, Пара и Воды
        int steamOffset = (int) (14.0 * zirnox.tanks[idxSteam].getFluidAmount() / Math.max(1, zirnox.tanks[idxSteam].getCapacity()));
        drawRect(guiLeft + 162 + steamOffset, guiTop + 113, guiLeft + 164 + steamOffset, guiTop + 118, 0xFF7F0000);

        int co2Offset = (int) (14.0 * zirnox.tanks[idxCO2].getFluidAmount() / Math.max(1, zirnox.tanks[idxCO2].getCapacity()));
        drawRect(guiLeft + 144 + co2Offset, guiTop + 113, guiLeft + 146 + co2Offset, guiTop + 118, 0xFF7F0000);

        int waterOffset = (int) (14.0 * zirnox.tanks[idxWater].getFluidAmount() / Math.max(1, zirnox.tanks[idxWater].getCapacity()));
        drawRect(guiLeft + 180 + waterOffset, guiTop + 113, guiLeft + 182 + waterOffset, guiTop + 118, 0xFF7F0000);

        // Стрелки температуры и давления на круглых циферблатах
        drawGaugeNeedle(guiLeft + 169, guiTop + 42, (double) zirnox.heat / 100000.0, 5);
        drawGaugeNeedle(guiLeft + 187, guiTop + 42, (double) zirnox.pressure / 100000.0, 5);

        // Зелёные лампочки на управляющих стержнях и кнопке питания при включённом реакторе
        if(zirnox.isOn) {
            for(int x = 0; x < 4; x++) {
                for(int y = 0; y < 4; y++) {
                    drawRect(guiLeft + 13 + 36 * x, guiTop + 21 + 36 * y, guiLeft + 19 + 36 * x, guiTop + 27 + 36 * y, 0xFF00D000);
                }
            }
            for(int x = 0; x < 3; x++) {
                for(int y = 0; y < 3; y++) {
                    drawRect(guiLeft + 31 + 36 * x, guiTop + 39 + 36 * y, guiLeft + 37 + 36 * x, guiTop + 45 + 36 * y, 0xFF00D000);
                }
            }
            // Верхний зелёный индикатор
            drawRect(guiLeft + 148, guiTop + 21, guiLeft + 154, guiTop + 27, 0xFF00D000);

            // Сама кнопка включения (становится зелёной вместо красной)
            drawRect(guiLeft + 145, guiTop + 36, guiLeft + 157, guiTop + 48, 0xFF00A800);
            drawRect(guiLeft + 148, guiTop + 39, guiLeft + 154, guiTop + 45, 0xFF003800);
            drawRect(guiLeft + 150, guiTop + 41, guiLeft + 152, guiTop + 43, 0xFF00E000);
        }
    }

    private void drawGaugeNeedle(int cx, int cy, double progress, int radius) {
        double clamped = Math.max(0.0, Math.min(1.0, progress));
        double angle = Math.toRadians(-135.0 + clamped * 270.0);
        int tipX = cx + (int) Math.round(Math.sin(angle) * radius);
        int tipY = cy - (int) Math.round(Math.cos(angle) * radius);
        drawRect(Math.min(cx, tipX), Math.min(cy, tipY), Math.max(cx, tipX) + 1, Math.max(cy, tipY) + 1, 0xFF7F0000);
    }
}