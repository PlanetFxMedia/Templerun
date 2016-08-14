package de.SebastianMikolai.PlanetFx.TempleRun.Datenbank;

import org.bukkit.entity.Player;

public class Game {
	
	public Player player;
	public String game;
	public Boolean checkpoint;
   
	public Game(Player player, String game, Boolean checkpoint) {
		this.player = player;
		this.game = game;
		this.checkpoint = checkpoint;
	}
}
