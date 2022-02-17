package ru.FortiBrine.JustSkins.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.exception.SkinRequestException;
import ru.FortiBrine.JustSkins.JustSkins;

public class CommandUnwear implements CommandExecutor {

	private JustSkins plugin;
	public CommandUnwear(JustSkins plugin) {
		this.plugin = plugin;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(plugin.messageFile);
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Вы не игрок");
			return true;
		}
		Player p = (Player) sender;
		String name = p.getName();
		
		if (!plugin.players.containsKey(name)) {
			p.sendMessage(messageConfig.getString("notWear", "").replace("&", "§"));
			return true;
		}
		for (String message : messageConfig.getStringList("unwear")) {
			message = message.replace("&", "§");

			p.sendMessage(message);
		}
		
		PlayerWrapper player = new PlayerWrapper(p);
		SkinsRestorerAPI api = SkinsRestorerAPI.getApi();
		
		try {
			api.setSkin(name, plugin.getConfig().getString("standart"));
			api.applySkin(player);
		} catch (SkinRequestException e1) {
			e1.printStackTrace();
		}
		
		String skin = plugin.players.get(p.getName());
		
		if (plugin.items.containsKey(skin)) {
			ItemStack item = plugin.items.get(skin);
			p.getInventory().addItem(item);
		}

		
		plugin.getConfig().set("players."+p.getName(), null);
		plugin.saveConfig();
		plugin.reloadConfig();
		plugin.loadBlocks();
		
		return true;
	}
}
