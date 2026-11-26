package com.hbm.render.entity.mob;

import org.lwjgl.opengl.GL11;

import com.hbm.entity.mob.sodtekhnologiyah.EntityBallsOTronBase;
import com.hbm.entity.mob.sodtekhnologiyah.EntityBallsOTronHead;
import com.hbm.entity.mob.sodtekhnologiyah.EntityBallsOTronSegment;
import com.hbm.lib.RefStrings;
import com.hbm.main.ResourceManager;
import com.hbm.render.amlfrom1710.AdvancedModelLoader;
import com.hbm.render.amlfrom1710.IModelCustom;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderBalls extends Render<EntityBallsOTronBase> {

	public static final IRenderFactory<EntityBallsOTronBase> FACTORY = man -> new RenderBalls(man);

	protected RenderBalls(RenderManager renderManager) {
		super(renderManager);
		this.shadowOpaque = 0;
	}

	public static final IModelCustom capsule = AdvancedModelLoader.loadModel(new ResourceLocation(RefStrings.MODID, "models/mobs/capsule.obj"));

	@Override
	public void doRender(EntityBallsOTronBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 90.0F, 0.0F, 0.0F, 1.0F);

		GL11.glShadeModel(GL11.GL_SMOOTH);
		this.bindEntityTexture(entity);
		capsule.renderAll();

		boolean shielded = (entity instanceof EntityBallsOTronHead && ((EntityBallsOTronHead) entity).isArmored())
				|| (entity instanceof EntityBallsOTronSegment && ((EntityBallsOTronSegment) entity).isShielded());

		if (shielded) {
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			GlStateManager.disableLighting();
			GlStateManager.color(0.75F, 0.25F, 1.0F, 0.45F);
			GlStateManager.scale(1.08F, 1.08F, 1.08F);
			capsule.renderAll();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}

		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBallsOTronBase entity) {
		return ResourceManager.turbofan_blades_tex;
	}
}
