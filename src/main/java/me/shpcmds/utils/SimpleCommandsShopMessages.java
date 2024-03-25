package me.shpcmds.utils;

import me.shpcmds.brain.Spigot;

public class SimpleCommandsShopMessages {

	public static String getMessage(String key) {
		return Spigot.getInstance().getConfig().getString("messages."+key);
	}
	
}
