package com.lessnop.customevents.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationData {

	private String world;

	private float x, y, z, yaw, pitch;

	public LocationData(String world, float x, float y, float z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public String getWorld() {
		return world;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Location toLocation() {
		World world = Bukkit.getWorld(this.world);
		if (world == null) return null;
		return new Location(world, x, y, z, yaw, pitch);
	}

	public static LocationData fromLocation(Location location) {
		return new LocationData(location.getWorld().getName(),
				(float) location.getX(), (float) location.getY(), (float) location.getZ(),
				location.getYaw(), location.getPitch());
	}

}
