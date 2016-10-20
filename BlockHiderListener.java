package com.gamsion.chris.blockhider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BlockHiderListener implements Listener {
	private Map<String, double[]> disguisedplayers = new HashMap<String, double[]>();
	private Map<String, Material> playerblocks = new HashMap<String, Material>();
	private List<String> cooldown = new ArrayList<String>();
	private Plugin plugin;
	private boolean realhide;

	public BlockHiderListener(Plugin plugin) {
		this.plugin = plugin;
		this.realhide = plugin.getConfig().getBoolean("ReallyHidePlayers");
		plugin.getLogger().info("ReallyHidePlayers is " + this.realhide);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack().hasItemMeta()
				&& e.getItemDrop().getItemStack().getItemMeta()
						.getDisplayName()
						.equals(ChatColor.GREEN + "Slimeball of Hiding")) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void playerDeath(PlayerDeathEvent e) {
		ItemStack i = new ItemStack(Material.SLIME_BALL);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Slimeball of Hiding");
		i.setItemMeta(im);
		if (e.getDrops().contains(i)) {
			e.getDrops().remove(i);
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getPlayer().getItemInHand().getType().equals(Material.SLIME_BALL)
				&& e.getPlayer().getItemInHand().hasItemMeta()
				&& e.getPlayer().getItemInHand().getItemMeta().getDisplayName()
						.equals(ChatColor.GREEN + "Slimeball of Hiding")) {
			if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
				playerblocks.put(e.getPlayer().getName(), e.getClickedBlock()
						.getType());
				e.getPlayer().sendMessage(
						ChatColor.GOLD + "You set your selected block to: "
								+ e.getClickedBlock().getType());
			} else if (cooldown.contains(e.getPlayer().getName())) {
				e.getPlayer().sendMessage("Cooldown...");
			} else if (e.getAction() == Action.RIGHT_CLICK_BLOCK
					|| e.getAction() == Action.RIGHT_CLICK_AIR) {
				if (!playerblocks.containsKey(e.getPlayer().getName())) {
					e.getPlayer().sendMessage(
							ChatColor.RED + "Must have a block selected.");
					return;
				}
				cooldown.add(e.getPlayer().getName());
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						new CooldownRunnable(e.getPlayer().getName()),
						plugin.getConfig().getLong("CooldownInTicks"));
				e.getPlayer().getLocation().getBlock()
						.setType(playerblocks.get(e.getPlayer().getName()));
				double[] ar = new double[3];
				ar[0] = e.getPlayer().getLocation().getX();
				ar[1] = e.getPlayer().getLocation().getY();
				ar[2] = e.getPlayer().getLocation().getZ();
				disguisedplayers.put(e.getPlayer().getName(), ar);

				hidePlayer(e.getPlayer());
				e.getPlayer()
						.teleport(e.getPlayer().getLocation().add(0, 1, 0));
				e.getPlayer()
						.sendMessage(
								ChatColor.DARK_RED
										+ "Don't move. If you move, you will no longer be hidden!");
				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
						new HideTimeRunnable(e.getPlayer()),
						plugin.getConfig().getLong("HideTimeInTicks"));
			}
			e.setCancelled(true);

		}

	}

	private class CooldownRunnable implements Runnable {
		private final String player;

		public CooldownRunnable(String player) {
			this.player = player;
		}

		public void run() {
			cooldown.remove(player);
		}

	}

	private class HideTimeRunnable implements Runnable {
		private final Player p;

		public HideTimeRunnable(Player p) {
			this.p = p;
		}

		public void run() {
			if(disguisedplayers.containsKey(p.getName())){
				Block b = p.getWorld().getBlockAt(
						new Location(p.getWorld(),
								disguisedplayers.get(p.getName())[0],
								disguisedplayers.get(p.getName())[1],
								disguisedplayers.get(p.getName())[2]));
				disguisedplayers.remove(p.getName());
				b.setType(Material.AIR);
				showPlayer(p);
			}
		}

	}

	private void hidePlayer(Player p) {
		if (realhide) {
			for (Player players : Bukkit.getOnlinePlayers()) {
				players.hidePlayer(p);
			}
		} else {
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY,
					Integer.MAX_VALUE, 1), true);
		}

	}

	private void showPlayer(Player p) {
		if (realhide) {

			for (Player players : Bukkit.getOnlinePlayers()) {
				players.showPlayer(p);
			}
		}
		p.removePotionEffect(PotionEffectType.INVISIBILITY);

	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {

		if (disguisedplayers.containsKey(e.getPlayer().getName())) {
			double[] ar = new double[3];
			ar[0] = e.getPlayer().getLocation().getX();
			ar[1] = e.getPlayer().getLocation().getY();
			ar[2] = e.getPlayer().getLocation().getZ();
			if (hasMovedLocation(ar,
					disguisedplayers.get(e.getPlayer().getName()))) {
				Block b = e
						.getPlayer()
						.getWorld()
						.getBlockAt(
								new Location(e.getPlayer().getWorld(),
										disguisedplayers.get(e.getPlayer()
												.getName())[0],
										disguisedplayers.get(e.getPlayer()
												.getName())[1],
										disguisedplayers.get(e.getPlayer()
												.getName())[2]));
				disguisedplayers.remove(e.getPlayer().getName());
				b.setType(Material.AIR);
				showPlayer(e.getPlayer());
			}
		}

	}

	private boolean hasMovedLocation(double[] ar1, double[] ar2) {
		if (ar1.length < 3 || ar2.length < 3)
			throw new ArrayTooShortException();
		for (int i = 0; i < 3; i++) {
			if ((ar1[i] > (ar2[i] + 1)) || (ar1[i] < (ar2[i] - 1)))
				return true;
		}
		return false;
	}

	public void setRealHide(boolean value) {
		this.realhide = value;
	}

	public boolean getRealHide() {
		return this.realhide;
	}

}
