/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.module.modules;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;

import org.darkstorm.minecraft.gui.theme.wurst.WurstTheme;
import org.darkstorm.minecraft.gui.util.RenderUtil;

import tk.wurst_client.Client;
import tk.wurst_client.module.Category;
import tk.wurst_client.module.Module;
import tk.wurst_client.utils.EntityUtils;
import tk.wurst_client.utils.RenderUtils;

public class BowAimbot extends Module
{
	public BowAimbot()
	{
		super(
			"BowAimbot",
			"Automatically aims your bow at the closest entity.\n"
				+ "Tip: This works with FastBow.",
			Category.COMBAT);
	}
	
	private Entity target;
	private float velocity;
	
	@Override
	public void onRender()
	{
		if(!getToggled() || target == null)
			return;
		RenderUtils.entityESPBox(target, 3);
	}
	
	@Override
	public void onRenderGUI()
	{
		if(!getToggled() || target == null || velocity < 0.1)
			return;
		glEnable(GL_BLEND);
		glDisable(GL_CULL_FACE);
		glDisable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		RenderUtil.setColor(new Color(8, 8, 8, 128));
		ScaledResolution sr =
			new ScaledResolution(Minecraft.getMinecraft(),
				Minecraft.getMinecraft().displayWidth,
				Minecraft.getMinecraft().displayHeight);
		int width = sr.getScaledWidth();
		int height = sr.getScaledHeight();
		String targetLocked = "Target locked";
		glBegin(GL_QUADS);
		{
			glVertex2d(width / 2 + 1, height / 2 + 1);
			glVertex2d(
				width
					/ 2
					+ ((WurstTheme)Client.wurst.guiManager.getTheme())
						.getFontRenderer().getStringWidth(targetLocked) + 4,
				height / 2 + 1);
			glVertex2d(
				width
					/ 2
					+ ((WurstTheme)Client.wurst.guiManager.getTheme())
						.getFontRenderer().getStringWidth(targetLocked) + 4,
				height
					/ 2
					+ ((WurstTheme)Client.wurst.guiManager.getTheme())
						.getFontRenderer().FONT_HEIGHT);
			glVertex2d(
				width / 2 + 1,
				height
					/ 2
					+ ((WurstTheme)Client.wurst.guiManager.getTheme())
						.getFontRenderer().FONT_HEIGHT);
		}
		glEnd();
		glEnable(GL_TEXTURE_2D);
		((WurstTheme)Client.wurst.guiManager.getTheme()).getFontRenderer()
			.drawStringWithShadow(targetLocked, width / 2 + 2, height / 2,
				RenderUtil.toRGBA(Color.WHITE));
		glEnable(GL_CULL_FACE);
		glDisable(GL_BLEND);
	}
	
	@Override
	public void onUpdate()
	{
		if(!getToggled())
			return;
		target = null;
		if(Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem() != null
			&& Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem()
				.getItem() instanceof ItemBow
			&& Minecraft.getMinecraft().gameSettings.keyBindUseItem.pressed)
		{
			target = EntityUtils.getClosestEntity(true);
			aimAtTarget();
		}
	}
	
	private void aimAtTarget()
	{
		if(target == null)
			return;
		int bowCharge =
			Minecraft.getMinecraft().thePlayer.getItemInUseDuration();
		velocity = bowCharge / 20;
		velocity = (velocity * velocity + velocity * 2) / 3;
		if(Client.wurst.moduleManager.getModuleFromClass(FastBow.class)
			.getToggled())
			velocity = 1;
		if(velocity < 0.1)
		{
			if(target instanceof EntityLivingBase)
				EntityUtils.faceEntityClient((EntityLivingBase)target);
			return;
		}
		if(velocity > 1)
			velocity = 1;
		double posX = target.posX - Minecraft.getMinecraft().thePlayer.posX;
		double posY =
			target.posY + target.getEyeHeight() - 0.15
				- Minecraft.getMinecraft().thePlayer.posY
				- Minecraft.getMinecraft().thePlayer.getEyeHeight();
		double posZ = target.posZ - Minecraft.getMinecraft().thePlayer.posZ;
		float yaw = (float)(Math.atan2(posZ, posX) * 180 / Math.PI) - 90;
		double y2 = Math.sqrt(posX * posX + posZ * posZ);
		float g = 0.006F;
		float tmp =
			(float)(velocity * velocity * velocity * velocity - g
				* (g * (y2 * y2) + 2 * posY * (velocity * velocity)));
		float pitch =
			(float)-Math.toDegrees(Math.atan((velocity * velocity - Math
				.sqrt(tmp)) / (g * y2)));
		Minecraft.getMinecraft().thePlayer.rotationYaw = yaw;
		Minecraft.getMinecraft().thePlayer.rotationPitch = pitch;
	}
}
