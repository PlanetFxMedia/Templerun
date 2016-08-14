package de.SebastianMikolai.PlanetFx.TempleRun;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.SebastianMikolai.PlanetFx.TempleRun.Datenbank.MySQL;
import de.SebastianMikolai.PlanetFx.TempleRun.Parkour.Parkour;

public class TempleRun extends JavaPlugin {
	
	public static TempleRun plugin;
	public List<Parkour> parkours = new ArrayList<Parkour>();
	public static String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "TempleRun" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET; 
	
	public void onEnable() {
		plugin = this;
		saveDefaultConfig();
		getCommand("pfxtr").setExecutor(new CommandListener());
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new EventListener(), this);
		MySQL.Connect();
		MySQL.LadeTabellen();
		MySQL.LoadParkours();
	}
	
    public void registerParkour(Parkour parkour) {
		if (!parkours.contains(parkour)) {
			parkours.add(parkour);
		}
	}
	
	public void createParkour(String name, Location loc) {
		parkours.add(new Parkour(name, loc, this));
		MySQL.SaveParkour(name, loc);
	} 
	
	public Parkour getParkourByName(String name) {
		for (Parkour p : parkours) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return null;
	}
}