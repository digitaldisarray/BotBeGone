package xyz.disarray.listeners;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import xyz.disarray.BotBeGone;
import xyz.disarray.PlayerObj;

public class PlayerListener implements Listener {

	BotBeGone plugin;
	private HashMap<UUID, Location> playerLocations = new HashMap<>(); // Used for the anti afk rn

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
//	@EventHandler
//	public void onPlayerBlockPlace(BlockPlaceEvent e) {
//		// TODO: Detect yaw and pitch of block break for exact/specific angles
//	}
//
//	@EventHandler
//	public void onPlayerBlockBreak(BlockBreakEvent e) {
//		// TODO: Detect yaw and pitch of block place for exact/specific angles
//	}
//
//	@EventHandler
//	public void onPlayerInteract(PlayerInteractEvent e) {
//		// TODO: Categorize, store, and scan actions for patterns
//	}
//
//	@EventHandler
//	public void onPlayerMove(PlayerMoveEvent e) {
//		// TODO: Detect movement patterns
//	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		// Remove any anti-spam methods & cleanse
		String message = normalizeMessage(e.getMessage());

		if (plugin.getMesssages().size() > 6) {
			for (String prev : plugin.getRecentMessages(6)) {

				// Check if the messages are the same
				if (e.getMessage().equals(prev)) {
					plugin.getPlayer(e.getPlayer().getUniqueId()).addInfraction();
					break;
				}

				// Check the word frequency (used for messages with spaces)
				if (wordFrequencyRatio(message, prev) >= 0.6) {
					// TODO: Make sure the message is longer than just a single word (has spaces)
					plugin.getPlayer(e.getPlayer().getUniqueId()).addInfraction();
					break;
				}

				// Check character frequency (used for messages with no spaces)
			}

			// Check if the player has more than 2 infractions. If so, kick them
			if (plugin.getPlayer(e.getPlayer().getUniqueId()).getInfractions() > 2) {
				e.setCancelled(true);
				Bukkit.getScheduler().runTask(plugin, new Runnable() {
					public void run() {
						e.getPlayer().kickPlayer("Kicked for being AFK"); // Don't let the bot devs know
					}
				});
			}

		}

		// Lastly, add it the the recent messages
		Bukkit.broadcastMessage("Adding message: " + message);
		plugin.newMessage(message);
	}

	String[] in = { "@", "8", "1", "4", "3", "5", "7" };
	String[] ou = { "a", "b", "i", "a", "e", "s", "t" };

	private String normalizeMessage(String input) {
		// Trim
		input = input.trim();

		// Convert to lower case
		input = input.toLowerCase();

		// Remove accents
		input = StringUtils.stripAccents(input);

		// Undo 1337 $p33k
		for (int i = 0; i < in.length; i++) {
			input = input.replaceAll(in[i], ou[i]);
		}

		// TODO: Add more removal features

		return input;
	}

	ArrayList<String> messageSplit1;
	ArrayList<String> messageSplit2;
	int matched;
	int unmatched;

	public double wordFrequencyRatio(String message1, String message2) {
		
		// Make sure the string contains spaces
		if(!message1.contains(" ") || !message2.contains(" ")) {
			return -0.1;
		}
		
		// Reset variables from any previous usages
		matched = 0;
		unmatched = 0;

		messageSplit1 = new ArrayList<String>(Arrays.asList(message1.split(" ")));
		messageSplit2 = new ArrayList<String>(Arrays.asList(message2.split(" ")));

		// First we need to make sure they have same length so we can compare better
		if (messageSplit1.size() < messageSplit2.size()) {
			for (int i = 0; i < messageSplit2.size() - messageSplit1.size(); i++) {
				messageSplit1.add(""); // Add an empty cell
			}
		} else if (messageSplit1.size() > messageSplit2.size()) {
			for (int i = 0; i < messageSplit2.size() - messageSplit1.size(); i++) {
				messageSplit1.add(""); // Add an empty cell
			}
		}

//			System.out.println("=== COMPARING: "
//					+ mArray.stream().map(Object::toString).collect(Collectors.joining(",")) + " and "
//					+ prevMsgA.stream().map(Object::toString).collect(Collectors.joining(",")));

		for (String a : messageSplit1) {
			for (String b : messageSplit2) {
				if (a.equals(b)) {
					matched++;
				} else {
					unmatched++;
				}
			}
		}

		return (double) matched / (matched + unmatched);
	}

	// Bot detection
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
