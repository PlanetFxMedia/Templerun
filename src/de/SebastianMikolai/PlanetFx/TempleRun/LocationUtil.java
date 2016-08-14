package de.SebastianMikolai.PlanetFx.TempleRun;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtil {
	
	public static String LocationToString(Location l) {
		return String.valueOf(new StringBuilder(String.valueOf(l.getWorld().getName())).append(":").append(l.getX()).toString()) + ":" + String.valueOf(l.getY()) + ":" + String.valueOf(new StringBuilder(String.valueOf(l.getZ())).append(":").append(String.valueOf(l.getPitch())).append(":").append(String.valueOf(l.getYaw())).toString());
	}

	public static Location StringToLoc(String s) {
		Location l = null;
		try {
			World world = org.bukkit.Bukkit.getWorld(s.split(":")[0]);
			Double x = Double.valueOf(Double.parseDouble(s.split(":")[1]));
			Double y = Double.valueOf(Double.parseDouble(s.split(":")[2]));
			Double z = Double.valueOf(Double.parseDouble(s.split(":")[3]));
			float pitch = 0.0F;
			float yaw = 0.0F;
			if (s.split(":")[4] != null) {
				pitch = Float.parseFloat(s.split(":")[4]);
			}
			if (s.split(":")[5] != null) {
				yaw = Float.parseFloat(s.split(":")[5]);
			}
			l = new Location(world, x.doubleValue(), y.doubleValue(), z.doubleValue(), yaw, pitch);
		} catch (Exception ex) {
			ex.printStackTrace();
		}   
		return l;
	}
}