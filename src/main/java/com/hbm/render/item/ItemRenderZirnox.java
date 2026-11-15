package com.hbm.render.item;

import org.lwjgl.opengl.GL11;

import com.hbm.main.ResourceManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class ItemRenderZirnox extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        GL11.glPushMatrix();
        GlStateManager.enableLighting();
        GlStateManager.disableCull();

        GL11.glTranslated(0.5D, 0.0D, 0.5D);
        GL11.glScaled(0.22D, 0.22D, 0.22D);

        Minecraft.getMinecraft().getTextureManager().bindTexture(ResourceManager.zirnox_tex);
        ResourceManager.zirnox.renderAll();

        GlStateManager.enableCull();
        GL11.glPopMatrix();
    }
}
