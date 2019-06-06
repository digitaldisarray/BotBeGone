package xyz.disarray;

import java.util.UUID;

import org.bukkit.Location;

public class PlayerObj {
	private UUID id;
	private Location oldLocation, newLocation;
	private long lastActionTime;
	
	public PlayerObj(UUID id) {
		this.id = id;
	}
	
	public UUID getID() {
		return id;
	}
}
