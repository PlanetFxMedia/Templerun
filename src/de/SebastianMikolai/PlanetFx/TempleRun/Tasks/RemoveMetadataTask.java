package de.SebastianMikolai.PlanetFx.TempleRun.Tasks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveMetadataTask extends BukkitRunnable {
	
	private Player player;
	private String metadata;
	private Plugin instance;
	
	public RemoveMetadataTask(Player p, String meta, Plugin instance) {
		this.player = p;
		this.metadata = meta;
		this.instance = instance;
	}   
	
	public void run() {
		if (this.player.isOnline()) {
			this.player.removeMetadata(this.metadata, this.instance);
		}
	}
}