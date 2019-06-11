package xyz.disarray;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.disarray.listeners.PlayerListener;

public class BotBeGone extends JavaPlugin {

	private ArrayList<PlayerObj> players = new ArrayList<>();
	private ArrayList<String> chat = new ArrayList<>();
	private ArrayList<String> getter = new ArrayList<>();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		
		// I added auto update so new spam bypasses can be patched rapidly
		PluginUpdater updater = new PluginUpdater(this, "http://www.disarray.xyz/botbegone.html");
		updater.disableOut();
		
		if(updater.needsUpdate())
			updater.update();
	}

	@Override
	public void onDisable() {
		players.clear();
	}

	public ArrayList<PlayerObj> getPlayers() {
		return players;
	}

	public void removePlayer(UUID id) {
		int i = 0;
		for (PlayerObj p : players) {
			if (p.getID().equals(id)) {
				i = players.indexOf(p);
			}
		}
		players.remove(i);
	}

	public PlayerObj getPlayer(UUID id) {
		if (indexOfPlayer(id) == -1) {
			players.add(new PlayerObj(id));
		}

		return players.get(indexOfPlayer(id));
	}

	public int indexOfPlayer(UUID id) {
		int i = -1;
		for (PlayerObj p : players) {
			if (p.getID().equals(id)) {
				i = players.indexOf(p);
			}
		}
		return i;
	}

	public ArrayList<String> getRecentMessages(int ammount) {
		getter.clear();
		// length and yada yada
		String returnals = "";
		for (int i = chat.size() - 1; i > chat.size() - 1 - ammount; i--) {
			getter.add(chat.get(i));
			returnals += chat.get(i) + ", ";
		}
//		Bukkit.broadcastMessage(ammount + "Returning: ");
		return getter;
	}
	
	public void newMessage(String message) {
		chat.add(message);
	}
	
	public ArrayList<String> getMesssages() {
		return chat;
	}
}
