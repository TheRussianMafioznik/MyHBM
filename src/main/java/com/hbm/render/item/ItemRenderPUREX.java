package com.hbm.render.item;

import org.lwjgl.opengl.GL11;

import com.hbm.main.ResourceManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class ItemRenderPUREX extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        GL11.glPushMatrix();
        GlStateManager.enableLighting();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        GL11.glTranslated(0.5D, 0.0D, 0.5D);
        GL11.glScaled(0.22D, 0.22D, 0.22D);

        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.purex_tex);
        ResourceManager.purex.renderPart("Base");
        ResourceManager.purex.renderPart("Frame");
        ResourceManager.purex.renderPart("Fan");
        ResourceManager.purex.renderPart("Pump");

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GL11.glPopMatrix();
    }
}
