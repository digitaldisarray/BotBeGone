package xyz.disarray;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.disarray.listeners.PlayerListener;

public class BotBeGone extends JavaPlugin {

	static ArrayList<PlayerObj> players = new ArrayList<>();

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
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
	
	public void getPlayer(UUID id) {
		if(indexOfPlayer(id) == -1) {
			players.add(new PlayerObj(id));
		}
		
		players.get(indexOfPlayer(id));
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
}
