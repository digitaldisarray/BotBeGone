package xyz.disarray;

import java.util.UUID;

import org.bukkit.Location;

public class PlayerObj {
	private UUID id;
	private Location location;
	
	private int infractions = 0;
	
	public PlayerObj(UUID id) {
		this.id = id;
	}
	
	public UUID getID() {
		return id;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public int getInfractions() {
		return infractions;
	}
	
	public void addInfraction() {
		infractions++;
	}
}
