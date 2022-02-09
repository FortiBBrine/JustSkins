package ru.FortiBrine.JustSkins.versions;

import org.bukkit.inventory.ItemStack;

public interface NMSManager {
	
	public ItemStack setNBTTag(ItemStack item, String NBTTag, String skin);
	public String getNBTTag(ItemStack item, String NBTTag);
}
