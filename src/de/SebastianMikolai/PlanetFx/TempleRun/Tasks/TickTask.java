package de.SebastianMikolai.PlanetFx.TempleRun.Tasks;

import org.bukkit.scheduler.BukkitRunnable;

import de.SebastianMikolai.PlanetFx.TempleRun.Parkour.Parkour;

public class TickTask extends BukkitRunnable {
	
	private final Parkour parkour;
	
	public TickTask(Parkour parkour) {
		this.parkour = parkour;
	}
	
	public void run() {
		this.parkour.tick();
	}
}