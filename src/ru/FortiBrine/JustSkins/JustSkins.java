package ru.FortiBrine.JustSkins;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ru.FortiBrine.JustSkins.Listeners.Handler;
import ru.FortiBrine.JustSkins.commands.CommandUnwear;
import ru.FortiBrine.JustSkins.commands.MainCommand;
import ru.FortiBrine.JustSkins.versions.NMSManager;
import ru.FortiBrine.JustSkins.versions.NMSManager_v1_12_R1;
import ru.FortiBrine.JustSkins.versions.NMSManager_v1_13_R2;
import ru.FortiBrine.JustSkins.versions.NMSManager_v1_14_R1;
import ru.FortiBrine.JustSkins.versions.NMSManager_v1_15_R1;
import ru.FortiBrine.JustSkins.versions.NMSManager_v1_16_R3;

public class JustSkins extends JavaPlugin {
	
	public File templateFile = new File(getDataFolder()+File.separator+"template.yml");
	public YamlConfiguration templateConfig;
	public File messageFile = new File(getDataFolder()+File.separator+"message.yml");
	
	public NMSManager nmsManager;
	public String sversion;
	
	public Map<Player, Long> cd = new HashMap<Player, Long>();
	public Map<String, String> players;
	public Map<String, ItemStack> items = new HashMap<String, ItemStack>();
	
	@Override
	public void onEnable() {
		
		if (!setupManager()) {
			getLogger().severe("Failed to check version! Running non-compatible version!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		PluginManager pluginManager = Bukkit.getPluginManager();
		if (pluginManager.getPlugin("SkinsRestorer")==null) {
			getLogger().severe("Failed to find SkinsRestorer!");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		File config = new File(getDataFolder()+File.separator+"config.yml");
		if (!config.exists()) {
			getConfig().options().copyDefaults(true);
			saveDefaultConfig();
		}
		if (!templateFile.exists()) saveResource("template.yml", false);
		if (!messageFile.exists()) saveResource("message.yml", false);
		
		loadBlocks();
		loadCommands();
		pluginManager.registerEvents(new Handler(this), this);
	}
	
	@SuppressWarnings("deprecation")
	public void loadBlocks() {
		items = new HashMap<String, ItemStack>();
		templateConfig = YamlConfiguration.loadConfiguration(templateFile);
		players = new HashMap<String, String>();
		for (String key : this.getConfig().getConfigurationSection("players").getKeys(false)) {
			players.put(key, this.getConfig().getString("players."+key));
		}
		
		for (String nickname : getConfig().getStringList("skins")) {
			String material = "PLAYER_HEAD";
			if (sversion.equals("v1_12_R1")) material = "SKULL_ITEM";
			
			ItemStack item = new ItemStack(Material.matchMaterial(material));
			
			SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
			skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(nickname))
			
			item.setItemMeta(skullMeta);
			ItemMeta meta = item.getItemMeta();
			
			String title = templateConfig.getString("item.title");
			List<String> lore = templateConfig.getStringList("item.lore");
			if (title==null) title="";
			if (lore==null) lore = new ArrayList<String>();
			
			for (int i = 0; i < lore.size(); i++) {
				lore.set(i, lore.get(i).replace("%skin%", nickname).replace("&", "§"));
			}
			title = title.replace("%skin%", nickname).replace("&", "§");
			
			meta.setDisplayName(title);
			meta.setLore(lore);
			item.setItemMeta(meta);
			item = nmsManager.setNBTTag(item, "skin", nickname);
			items.put(nickname, item);
		}
	}
	
	public void loadCommands() {
		getCommand("justskins").setExecutor(new MainCommand(this));
		getCommand("unwear").setExecutor(new CommandUnwear(this));
	}
	
	public boolean checkHead(ItemStack item) {
		if (nmsManager.getNBTTag(item, "skin") != null) return true;
		return false;
	}
	
	private boolean setupManager() {
		
		sversion = "N/A";
		
		try {
			sversion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		if (sversion.equals("v1_16_R3")) {
			nmsManager = new NMSManager_v1_16_R3();
		}
		if (sversion.equals("v1_15_R1")) {
			nmsManager = new NMSManager_v1_15_R1();
		}
		if (sversion.equals("v1_14_R1")) {
			nmsManager = new NMSManager_v1_14_R1();
		}
		if (sversion.equals("v1_13_R2")) {
			nmsManager = new NMSManager_v1_13_R2();
		}
		if (sversion.equals("v1_12_R1")) {
			nmsManager = new NMSManager_v1_12_R1();
		}
		return nmsManager != null;
		
	}
}
