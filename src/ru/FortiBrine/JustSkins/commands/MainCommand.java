package ru.FortiBrine.JustSkins.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import ru.FortiBrine.JustSkins.JustSkins;

public class MainCommand implements CommandExecutor {
	
	private List<String> subCommands = new ArrayList<String>();
	private JustSkins plugin;
	public MainCommand(JustSkins plugin) {
		this.plugin = plugin;
		this.subCommands.add("get");
		this.subCommands.add("give");
		this.subCommands.add("check");
		this.subCommands.add("reload");
	}
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		YamlConfiguration messageConfig = YamlConfiguration.loadConfiguration(plugin.messageFile);
		if (!sender.hasPermission(cmd.getPermission())) {
			String send = messageConfig.getString("permission-message", "");
			send = send.replace("&", "§");
			sender.sendMessage(send);
			return true;
		}
		if (args.length < 1) {
			for (String s : messageConfig.getStringList("usage")) {
				s = s.replace("&", "§");
				sender.sendMessage(s);
			}
			return true;
		}
		if (!this.subCommands.contains(args[0].toLowerCase())) {
			for (String s : messageConfig.getStringList("usage")) {
				s = s.replace("&", "§");
				sender.sendMessage(s);
			}
			return true;
		}
		if (args[0].equals("reload")) {
			plugin.reloadConfig();
			plugin.loadBlocks();
			String send = messageConfig.getString("reloadSuccess", "");
			send = send.replace("&", "§");
			sender.sendMessage(send);
			return true;
		}
		if (args[0].equals("give") && args.length >= 3) {
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);
			if (!p.isOnline()) {
				sender.sendMessage(messageConfig.getString("playerNotOnline", "").replace("&", "§"));
				return true;
			}
			Player player = Bukkit.getPlayer(args[1]);
			List<String> items = plugin.getConfig().getStringList("skins");
			if (!items.contains(args[2])) {
				if (plugin.getConfig().getBoolean("addAndGive")) {
					items.add(args[2]);
					plugin.getConfig().set("skins", items);
					plugin.saveConfig();
					plugin.reloadConfig();
					plugin.loadBlocks();
				} else {
					sender.sendMessage(messageConfig.getString("notFind", "").replace("&", "§"));
					return true;
				}
			}
			player.getInventory().addItem(plugin.items.get(args[2]));
			sender.sendMessage(messageConfig.getString("giveItem", "").replace("&", "§"));
			
			return true;
		} else if (args[0].equals("give")) {
			for (String s : messageConfig.getStringList("usage")) {
				s = s.replace("&", "§");
				sender.sendMessage(s);
			}
			return true;
		}
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args[0].equals("check")) {
				ItemStack item = p.getInventory().getItemInMainHand();
				if (item.getType()==Material.AIR) {
					String send = messageConfig.getString("nonItemInHand", "");
					send = send.replace("&", "§");
					p.sendMessage(send);
					return true;
				}
				boolean check = plugin.checkHead(item);
				if (check) {
					p.sendMessage(messageConfig.getString("checkAccept", "").replace("&", "§"));
				} else {
					p.sendMessage(messageConfig.getString("checkDecline", "").replace("&", "§"));
				}
				
				return true;
			}
			if (args[0].equals("get") && args.length>=2) {
				List<String> items = plugin.getConfig().getStringList("skins");
				if (!items.contains(args[1])) {
					if (plugin.getConfig().getBoolean("addAndGive")) {
						items.add(args[1]);
						plugin.getConfig().set("skins", items);
						plugin.saveConfig();
						plugin.reloadConfig();
						plugin.loadBlocks();
					} else {
						sender.sendMessage(messageConfig.getString("notFind", "").replace("&", "§"));
						return true;
					}
				}
				p.getInventory().addItem(plugin.items.get(args[1]));
				String send = messageConfig.getString("successGivedItem", "");
				send = send.replace("&", "§");
				p.sendMessage(send);
				return true;
			}
			for (String s : messageConfig.getStringList("usage")) {
				s = s.replace("&", "§");
				sender.sendMessage(s);
			}
		} else {
			String send = messageConfig.getString("nonConsoleCommand", "");
			send = send.replace("&", "§");
			sender.sendMessage(send);
			return true;
		}
		return true;
	}
}
