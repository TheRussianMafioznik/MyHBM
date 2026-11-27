package com.hbm.handler;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WorldSpaceFPRender {

	public static boolean shouldCustomRender = false;

	public static void doHandRendering(RenderWorldLastEvent e) {
	}
}
