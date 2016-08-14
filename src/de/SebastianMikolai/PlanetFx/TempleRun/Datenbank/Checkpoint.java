package de.SebastianMikolai.PlanetFx.TempleRun.Datenbank;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Checkpoint {
	
	public Player player;
	public Location loc;
   
	public Checkpoint(Player player, Location loc) {
		this.player = player;
		this.loc = loc;
	}
}
