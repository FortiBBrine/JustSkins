package ru.FortiBrine.JustSkins.Listeners;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.exception.SkinRequestException;
import ru.FortiBrine.JustSkins.JustSkins;

public class Handler implements Listener {

	private JustSkins plugin;
	public Handler(JustSkins plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(plugin.messageFile);
		
		Player p = e.getPlayer();
		String name = p.getName();
		
		SkinsRestorerAPI api = SkinsRestorerAPI.getApi();
		PlayerWrapper player = new PlayerWrapper(p);
		
		if (!plugin.players.containsKey(name)) {

			try {
				api.setSkin(name, plugin.getConfig().getString("standart"));
				api.applySkin(player);
			} catch (SkinRequestException e1) {
				e1.printStackTrace();
			}
			for (String message : messageConfig.getStringList("reloadSkin")) {
				message = message.replace("&", "§");

				p.sendMessage(message);
			}

		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(plugin.messageFile);
		Player p = e.getPlayer();
		String name = p.getName();
		ItemStack item = e.getItem();
		
		if (plugin.checkHead(item) == false) return;
		
		if (plugin.cd.containsKey(p)) {
			long cd = plugin.cd.get(p);
			if (cd > System.currentTimeMillis()) {
				long seconds = (cd-System.currentTimeMillis())/1000;
				p.sendMessage(messageConfig.getString("cdLimit").replace("%time%", ""+seconds).replace("&", "§"));
				
				return;
			}
		}
		long cd = System.currentTimeMillis()+plugin.getConfig().getLong("cd")*1000;
		plugin.cd.put(p, cd);
		
		if (plugin.players.containsKey(name)) {
			String message = messageConfig.getString("playerHasBlocked");
			message = message.replace("&", "§");
			
			p.sendMessage(message);
			return;
		}
		String skin = plugin.nmsManager.getNBTTag(item, "skin");

		SkinsRestorerAPI api = SkinsRestorerAPI.getApi();
		PlayerWrapper player = new PlayerWrapper(p);
		
		try {
			api.setSkin(name, skin);
			api.applySkin(player);
		} catch (SkinRequestException e1) {
			p.sendMessage(messageConfig.getString("skinNotExists").replace("&", "§"));
			return;
		}
		
		plugin.getConfig().set("players."+name, skin);
		plugin.saveConfig();
		plugin.reloadConfig();
		plugin.loadBlocks();
		
		for (String message : messageConfig.getStringList("setSkin")) {
			message = message.replace("&", "§");

			p.sendMessage(message);
		}

		Inventory inv = p.getInventory();
		inv.removeItem(item);
	}
}
