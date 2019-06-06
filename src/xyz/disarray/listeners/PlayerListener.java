package xyz.disarray.listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.disarray.BotBeGone;
import xyz.disarray.PlayerObj;

public class PlayerListener implements Listener {
	
	BotBeGone plugin;
	private HashMap<UUID, Location> playerLocations = new HashMap<>();
	
	public PlayerListener(BotBeGone plugin) {
		this.plugin = plugin;
		startThread();
	}
	
	// *** Util related listeners ***
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		// A new player to keep track of
		plugin.getPlayers().add(new PlayerObj(e.getPlayer().getUniqueId()));
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		// We no longer need to keep track of the player
		plugin.removePlayer(e.getPlayer().getUniqueId());
	}

	// *** Player action listeners ***
	@EventHandler
	public void onPlayerBlockPlace(BlockPlaceEvent e) {

	}

	@EventHandler
	public void onPlayerBlockBreak(BlockBreakEvent e) {

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		// Check for spam patterns
	}

	/*
	 * AFK Machine detection
	 */

	private void startThread() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			playerLocations.clear();
			// Save all players current locations
			for (Player p : Bukkit.getOnlinePlayers()) {
				playerLocations.put(p.getUniqueId(), p.getLocation());
			}
			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
				// Go through each saved location and see if the player is moving
				for (UUID uuid : playerLocations.keySet()) {
					if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
						Location savedLoc = playerLocations.get(uuid);
						Location loc = Bukkit.getPlayer(uuid).getLocation();
						// Check if the player is moving in both rotation and transform
						boolean inactive = false;
						// TODO: Make the agressive checking check only XZ instead of XYZ
						if (plugin.getConfig().getBoolean("AggressiveAFKDetection")) {
							// If aggressive is enabled we want to check if the player isn't moving in one
							// or both
							if (checkRotation(savedLoc, loc))
								inactive = true;
							if (checkXYZ(savedLoc, loc))
								inactive = true;
						} else {
							// Without aggressive enabled we only want one to be true,
							// if both are false then inactive will be false
							// This is achieved by converting true to 1 and false to 0 and summing them
							// Inactive is only true when only one of the booleans is true
							if ((checkRotation(savedLoc, loc) ? 1 : 0) + (checkXYZ(savedLoc, loc) ? 1 : 0) == 1) {
								inactive = true;
							}
						}
						
						// Player deemed afk
						Bukkit.getPlayer(uuid).kickPlayer("Kicked for being AFK"); // TOOD: Config file message
					}
				}
			}, 20 * 2);
		}, 20 * 5, 20 * 5);
	}

	/**
	 * Compares two location objects based on yaw and pitch.
	 * 
	 * @param oldLocation - A previous, old location.
	 * @param newLocation - A new, potentially updated location.
	 * @return - Returns true if the yaw and pitch are the same, otherwise returns
	 *         false.
	 */
	private boolean checkRotation(Location oldLocation, Location newLocation) {
		return oldLocation.getYaw() == newLocation.getYaw() && oldLocation.getPitch() == newLocation.getPitch();
	}

	/**
	 * Compares two location objects based on x, y, and z.
	 * 
	 * @param oldLocation - A previous, old location.
	 * @param newLocation - A new, potentially updated location.
	 * @return - Returns true if the locations are the same, otherwise returns
	 *         false.
	 */
	private boolean checkXYZ(Location oldLocation, Location newLocation) {
		return oldLocation.getX() == newLocation.getX() && oldLocation.getY() == newLocation.getY()
				&& oldLocation.getZ() == newLocation.getZ();
	}

	/**
	 * Compares two location objects based ONLY on x and z.
	 * 
	 * @param oldLocation - A previous, old location.
	 * @param newLocation - A new, potentially updated location.
	 * @return - Returns true if the locations are the same, otherwise returns
	 *         false.
	 */
	private boolean checkXZ(Location oldLocation, Location newLocation) {
		return oldLocation.getX() == newLocation.getX() && oldLocation.getZ() == newLocation.getZ();
	}
}
