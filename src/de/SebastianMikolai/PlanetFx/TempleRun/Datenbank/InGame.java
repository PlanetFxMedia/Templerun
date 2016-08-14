package de.SebastianMikolai.PlanetFx.TempleRun.Datenbank;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class InGame {
	
	private static InGame instance;
	private List<Game> InGamePlayers = new ArrayList<Game>();
	private List<Checkpoint> cps = new ArrayList<Checkpoint>();
	
	public static InGame getInstance() {
		if (instance == null) {
			instance = new InGame();
		}
		return instance;
	}
	
	public List<Player> getInGamePlayers() {
		List<Player> plist = new ArrayList<Player>();
		for (Game game : InGamePlayers) {
			plist.add(game.player);
		}
		return plist;
	}
	
	public void add(Player p, String game) {
		InGamePlayers.add(new Game(p, game, false));
	}
   
	public void remove(Player p) {
		for (Game game : InGamePlayers) {
			if (game.player == p) {
				InGamePlayers.remove(p);
				return;
			}
		}
	}
   
	public boolean isPlayerInList(Player p) {
		for (Game game : InGamePlayers) {
			if (game.player == p) {
				return true;
			}
		}
		return false;
	}
	
	public void setCheckpoint(Player p) {
		for (Game game : InGamePlayers) {
			if (game.player == p) {
				game.checkpoint = true;
				//cps.add(new Checkpoint(p, loc));
			}
		}
	}
	
	public boolean isCheckpoint(Player p) {
		for (Game game : InGamePlayers) {
			if (game.player == p) {
				return game.checkpoint;
			}
		}
		return false;
	}
	
	public Location getCheckpoint(Player p) {
		Location loc = null;
		for (Game game : InGamePlayers) {
			if (game.player == p) {
				for (Checkpoint cp : cps) {
					if (cp.player == p) {
						loc = cp.loc;
					}
				}
			}
		}
		return loc;
	}
}
