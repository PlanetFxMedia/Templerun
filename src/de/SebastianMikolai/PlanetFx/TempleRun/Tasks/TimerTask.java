package de.SebastianMikolai.PlanetFx.TempleRun.Tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import de.SebastianMikolai.PlanetFx.TempleRun.Parkour.Parkour;

public class TimerTask extends BukkitRunnable {
	
	int time;
	int add;
	Player p;
	Parkour pk;
	
	public TimerTask(Player p, Parkour pk, int secondes, int toadd) {
		this.p = p;
		this.pk = pk;
		this.time = secondes;
		this.add = toadd;
	}

	public void run() {
		this.pk.updateTimer(this.p, this.time + this.add);
	}
}