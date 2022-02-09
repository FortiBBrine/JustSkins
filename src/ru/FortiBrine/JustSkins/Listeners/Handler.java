package ru.FortiBrine.JustSkins.Listeners;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import ru.FortiBrine.JustSkins.JustSkins;

@SuppressWarnings("unused")
public class Handler implements Listener {

	private JustSkins plugin;
	public Handler(JustSkins plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(plugin.messageFile);
		Player p = e.getPlayer();
		String name = p.getName();
		ItemStack item = e.getItem();
		
		if (plugin.checkHead(item) == false) return;
		if (plugin.getConfig().getStringList("blockedPlayers")==null) return;
		if (!plugin.getConfig().getStringList("blockedPlayers").contains(name)) {
			String message = messageConfig.getString("playerHasBlocked");
			message = message.replace("&", "§");
			
			p.sendMessage(message);
			return;
		}
		p.sendMessage("YES");
	}
}
