package ru.FortiBrine.JustSkins.versions;

import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_16_R3.NBTTagCompound;

public class NMSManager_v1_16_R3 implements NMSManager {
	
	public ItemStack setNBTTag(ItemStack item, String NBTTag, String skin) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = nmsItem.getTag()!=null?nmsItem.getTag():new NBTTagCompound();
		tag.setString(NBTTag, skin);
		nmsItem.setTag(tag);
		item = CraftItemStack.asBukkitCopy(nmsItem);
		return item;
	}
	
	public String getNBTTag(ItemStack item, String NBTTag) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		String result = null;
		NBTTagCompound tag = nmsItem.getTag()!=null?nmsItem.getTag():new NBTTagCompound();
		if (tag.hasKey(NBTTag)) result = tag.getString(NBTTag);
		
		return result;
	}
}
