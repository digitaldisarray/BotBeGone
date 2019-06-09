package xyz.disarray.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

		// Get the message
		String message = e.getMessage();

		// Variables to measure the ratio
		ArrayList<String> mArray = new ArrayList<>(Arrays.asList(message.split(" ")));
		int unmatched = 0;
		int matched = 0;

		ArrayList<String> prevMsgA;

		// Cleanse the message
		message = message.toLowerCase();

		// Remove any anti-spam methods
		message = antiAntiSpam(message);

		if (plugin.getMesssages().size() > 6) {
			for (String prev : plugin.getRecentMessages(6)) {
				if (e.getMessage().equals(prev)) {
					plugin.getPlayer(e.getPlayer().getUniqueId()).addInfraction();

//					Bukkit.broadcastMessage("Matched with previous message");

					if (plugin.getPlayer(e.getPlayer().getUniqueId()).getInfractions() > 2) {
						e.setCancelled(true);
						Bukkit.getScheduler().runTask(plugin, new Runnable() {
							public void run() {
								e.getPlayer().kickPlayer("Kicked for being AFK"); // Don't let the bot devs know
							}
						});
					}
					break;
				} else {
					// Try to check the ratio of words to see how similar they are

					prevMsgA = new ArrayList<>(Arrays.asList(prev.split(" ")));

					// First we need to make sure they have same length so we can compare better
					if (prevMsgA.size() < mArray.size()) {
						for (int i = 0; i < mArray.size() - prevMsgA.size(); i++) {
							prevMsgA.add(""); // Add an empty cell
						}
					} else if (prevMsgA.size() > mArray.size()) {
						for (int i = 0; i < prevMsgA.size() - mArray.size(); i++) {
							mArray.add(""); // Add an empty cell
						}
					}

//					System.out.println("=== COMPARING: "
//							+ mArray.stream().map(Object::toString).collect(Collectors.joining(",")) + " and "
//							+ prevMsgA.stream().map(Object::toString).collect(Collectors.joining(",")));

					for (String a : prevMsgA) {
						for (String b : mArray) {
							if (a.equals(b)) {
								matched++;
							} else {
								unmatched++;
							}
						}
					}

					// Ratio is above 60%
					if ((double) matched / (matched + unmatched) > 0.6) {
						plugin.getPlayer(e.getPlayer().getUniqueId()).addInfraction();

//						Bukkit.broadcastMessage("high ratio");

						if (plugin.getPlayer(e.getPlayer().getUniqueId()).getInfractions() > 2) {
							e.setCancelled(true);
							Bukkit.getScheduler().runTask(plugin, new Runnable() {
								public void run() {
									e.getPlayer().kickPlayer("Spammer"); // Don't let the bot devs know
								}
							});
						}
						break;
					}

					// Otherwise, try the next message in history (so reset vars)
					matched = 0;
					unmatched = 0;
				}
			}
		}

		// Lastly, add it the the recent messages
		Bukkit.broadcastMessage("Adding message: " + message);
		plugin.newMessage(message);
	}

	String[] in = { "@", "8", "1", "$", "4", "3", "7", "à", "á", "â", "ã", "ä", "ç", "è", "é", "ê", "ë", "ì", "í", "î",
			"ï", "ñ", "ò", "ó", "ô", "õ", "ö", "ù" };
	String[] ou = { "a", "b", "i", "s", "a", "e", "t", "a", "a", "a", "a", "a", "c", "e", "e", "e", "e", "i", "i", "l",
			"l", "l", "l", "o", "o", "o", "o", "u" };

	private String antiAntiSpam(String input) {
		for (int i = 0; i < in.length; i++) {
			input = input.replaceAll(in[i], ou[i]);
		}
		return input;
	}

	/*
	 * AFK detection
	 */

	private void startThread() {
//		// Runs every 5 seconds
//		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
//			playerLocations.clear();
//
//			// Save all players current locations
//			for (Player p : Bukkit.getOnlinePlayers()) {
//				playerLocations.put(p.getUniqueId(), p.getLocation());
//			}
//
//			// Runs every 2 seconds
//			Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
//
//				// Go through each saved player location
//				for (UUID uuid : playerLocations.keySet()) {
//					if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
//						Location savedLoc = playerLocations.get(uuid);
//						Location loc = Bukkit.getPlayer(uuid).getLocation();
//
//						// Check if the player is moving in both rotation and transform
//						boolean inactive = false;
//
//						if (plugin.getConfig().getBoolean("AggressiveAFKDetection")) {
//							// Player has to move and look around
//							if (checkRotation(savedLoc, loc) && checkXZ(savedLoc, loc))
//								inactive = true;
//						} else {
//							// Check if either the player has moved their position in the xyz direction or
//							// pitch and yaw
//							if (checkRotation(savedLoc, loc) || checkXYZ(savedLoc, loc)) {
//								inactive = true;
//							}
//						}
//
//						// Player is afk
//						if (inactive)
//							Bukkit.getPlayer(uuid).kickPlayer("Kicked for being AFK"); // TODO: Config file message
//					}
//				}
//			}, 20 * 2);
//		}, 20 * 5, 20 * 5);
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
