/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.module.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import tk.wurst_client.Client;
import tk.wurst_client.module.Category;
import tk.wurst_client.module.Module;
import tk.wurst_client.utils.EntityUtils;

public class TriggerBot extends Module
{
	public TriggerBot()
	{
		super(
			"TriggerBot",
			"Automatically attacks the entity you're looking at.",
			Category.COMBAT);
	}
	
	@Override
	public void onEnable()
	{
		if(Client.wurst.moduleManager.getModuleFromClass(Killaura.class)
			.getToggled())
			Client.wurst.moduleManager.getModuleFromClass(Killaura.class)
				.setToggled(false);
		if(Client.wurst.moduleManager.getModuleFromClass(KillauraLegit.class)
			.getToggled())
			Client.wurst.moduleManager.getModuleFromClass(KillauraLegit.class)
				.setToggled(false);
		if(Client.wurst.moduleManager.getModuleFromClass(MultiAura.class)
			.getToggled())
			Client.wurst.moduleManager.getModuleFromClass(MultiAura.class)
				.setToggled(false);
	}
	
	@Override
	public void onUpdate()
	{
		if(getToggled()
			&& Minecraft.getMinecraft().objectMouseOver != null
			&& Minecraft.getMinecraft().objectMouseOver.entityHit != null)
		{
			updateMS();
			if(hasTimePassedS(Killaura.realSpeed))
			{
				EntityLivingBase en =
					(EntityLivingBase)Minecraft.getMinecraft().objectMouseOver.entityHit;
				if(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(en) <= Killaura.realRange
					&& EntityUtils.isCorrectEntity(en, true))
				{
					Criticals.doCritical();
					Minecraft.getMinecraft().thePlayer.swingItem();
					Minecraft.getMinecraft().playerController.attackEntity(
						Minecraft.getMinecraft().thePlayer, en);
					updateLastMS();
				}
			}
		}
	}
}
