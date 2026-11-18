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
import net.minecraft.client.renderer.GlStateManager;
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
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int idxSteam = 0;
        int idxCO2 = 1;
        int idxWater = 2;

        // Линейные индикаторы CO2, Пара и Воды (аккуратные указатели строго внутри шкалы)
        drawSmoothLinearGauge(guiLeft + 144, guiTop + 115, this.zLevel, (double) zirnox.tanks[idxCO2].getFluidAmount() / Math.max(1, zirnox.tanks[idxCO2].getCapacity()), 2, 2, 0.5, 14, 0x7F0000);
        drawSmoothLinearGauge(guiLeft + 162, guiTop + 115, this.zLevel, (double) zirnox.tanks[idxSteam].getFluidAmount() / Math.max(1, zirnox.tanks[idxSteam].getCapacity()), 2, 2, 0.5, 14, 0x7F0000);
        drawSmoothLinearGauge(guiLeft + 180, guiTop + 115, this.zLevel, (double) zirnox.tanks[idxWater].getFluidAmount() / Math.max(1, zirnox.tanks[idxWater].getCapacity()), 2, 2, 0.5, 14, 0x7F0000);

        // Круглые стрелки температуры и давления на циферблатах (от левого нижнего края шкалы к правому)
        drawSmoothGauge(guiLeft + 169, guiTop + 42, this.zLevel, (double) zirnox.heat / 100000.0, 5, 2, 1, 0x7F0000);
        drawSmoothGauge(guiLeft + 187, guiTop + 42, this.zLevel, (double) zirnox.pressure / 100000.0, 5, 2, 1, 0x7F0000);

        // Сброс цвета OpenGL перед отрисовкой текстур
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // Зелёные диоды при работающем реакторе
        if (zirnox.isOn) {
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 4; y++) {
                    drawGreenLed(guiLeft + 13 + 36 * x, guiTop + 21 + 36 * y);
                }
            }
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    drawGreenLed(guiLeft + 31 + 36 * x, guiTop + 39 + 36 * y);
                }
            }
            drawGreenLed(guiLeft + 148, guiTop + 21);
        }

        // Родные боковые вкладки из GuiInfoContainer (gui_utility.png) со сброшенным белым цветом
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawInfoPanel(guiLeft - 16, guiTop + 36, 16, 16, 2);
        this.drawInfoPanel(guiLeft - 16, guiTop + 36 + 16, 16, 16, 3);

        if (zirnox.tanks[idxCO2].getFluidAmount() <= 0) {
            this.drawInfoPanel(guiLeft - 16, guiTop + 36 + 32, 16, 16, 6);
        }
        if (zirnox.tanks[idxWater].getFluidAmount() < 4000) {
            this.drawInfoPanel(guiLeft - 16, guiTop + 36 + 32 + 16, 16, 16, 7);
        }
    }

    private void drawGreenLed(int x, int y) {
        drawRect(x + 2, y, x + 4, y + 6, 0xFF28B428);
        drawRect(x, y + 2, x + 6, y + 4, 0xFF28B428);
        drawRect(x + 1, y + 1, x + 5, y + 5, 0xFF3CE63C);
        drawRect(x + 1, y + 1, x + 3, y + 3, 0xFFA0FFA0);
    }

    private void drawSmoothGauge(int x, int y, double z, double progress, double tipLength, double backLength, double backSide, int color) {
        GlStateManager.disableTexture2D();
        progress = Math.max(0.0, Math.min(1.0, progress));
        // 0.0 -> -135 градусов (левый низ шкалы), 0.5 -> 0 (верх), 1.0 -> +135 градусов (правый низ)
        double angle = Math.toRadians(-135.0 + progress * 270.0);

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        double tipX = tipLength * sin;
        double tipY = -tipLength * cos;
        double leftX = -backSide * cos - backLength * sin;
        double leftY = -backSide * sin + backLength * cos;
        double rightX = backSide * cos - backLength * sin;
        double rightY = backSide * sin + backLength * cos;

        double mult = 1.5;
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glVertex3d(x + tipX * mult, y + tipY * mult, z);
        GL11.glVertex3d(x + leftX * mult, y + leftY * mult, z);
        GL11.glVertex3d(x + rightX * mult, y + rightY * mult, z);

        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GL11.glColor4f(r, g, b, 1.0F);
        GL11.glVertex3d(x + tipX, y + tipY, z);
        GL11.glVertex3d(x + leftX, y + leftY, z);
        GL11.glVertex3d(x + rightX, y + rightY, z);
        GL11.glEnd();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
    }

    private void drawSmoothLinearGauge(int x, int y, double z, double progress, double tipLength, double backLength, double backSide, double scale, int color) {
        GlStateManager.disableTexture2D();
        scale = Math.max(scale, 1.0);
        double deltaX = Math.max(0.0, Math.min(1.0, progress)) * scale;

        double[] px = { 0, -backSide, -backSide, backSide, backSide };
        double[] py = { -tipLength, 0, backLength, backLength, 0 };

        double mult = 1.5;
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
        for (int i = 0; i < 5; i++) {
            double vy = (i == 2 || i == 3) ? py[i] : py[i] * mult;
            GL11.glVertex3d(x + deltaX + px[i] * mult, y + vy, z);
        }
        GL11.glEnd();

        float r = ((color >> 16) & 255) / 255.0F;
        float g = ((color >> 8) & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        GL11.glBegin(GL11.GL_POLYGON);
        GL11.glColor4f(r, g, b, 1.0F);
        for (int i = 0; i < 5; i++) {
            GL11.glVertex3d(x + deltaX + px[i], y + py[i], z);
        }
        GL11.glEnd();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
    }
}