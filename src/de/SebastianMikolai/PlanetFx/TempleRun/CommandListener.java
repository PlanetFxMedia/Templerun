package de.SebastianMikolai.PlanetFx.TempleRun;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

import de.SebastianMikolai.PlanetFx.TempleRun.Datenbank.InGame;
import de.SebastianMikolai.PlanetFx.TempleRun.Datenbank.MySQL;
import de.SebastianMikolai.PlanetFx.TempleRun.Parkour.Parkour;

public class CommandListener implements CommandExecutor {

	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if ((cs instanceof Player)) {
			Player p = (Player) cs;
			if (args.length != 0) {
				if (args[0].equalsIgnoreCase("help") && cs.hasPermission("pfxtr.help")) {
					cs.sendMessage("§6= = = = §a§lPlanet-Fx TempleRun Help §6= = = =");
					cs.sendMessage("§6Hilfe: §f/pfxtr help");
					cs.sendMessage("");
					cs.sendMessage("§6Parkour setzen: §f/pfxtr create [name]");
					cs.sendMessage("§6Parkour löschen: §f/pfxtr remove [name]");
					cs.sendMessage("§6Parkour spielen: §f/pfxtr join [name]");
					cs.sendMessage("§6Parkour verlassen: §f/pfxtr leave");
					cs.sendMessage("§6Alle Parkours auflisten : §f/pfxtr list");
				} else if (args[0].equalsIgnoreCase("create") && cs.hasPermission("pfxtr.create")) {
					if (args.length == 2) {
						Location loc = p.getLocation();
						if (TempleRun.plugin.getParkourByName(args[1]) == null) {
							TempleRun.plugin.createParkour(args[1], loc);
							cs.sendMessage("Neuen Parkour gesetzt: " + args[1]);
						} else {
							cs.sendMessage("Ein Parkour mit diesem Namen existiert bereits!");
						}
					} else {
						cs.sendMessage("Du musst einen Parkour angeben!");
					}
				} else if (args[0].equalsIgnoreCase("remove") && cs.hasPermission("pfxtr.remove")) {
					if (args.length == 2) {
						if (TempleRun.plugin.getParkourByName(args[1]) != null) {
							Parkour parkour = TempleRun.plugin.getParkourByName(args[1]);
							TempleRun.plugin.parkours.remove(parkour);
							MySQL.RemoveParkour(parkour);
							cs.sendMessage("Parkour gelöscht!");
						} else {
							cs.sendMessage("Dieser Parkour existiert nicht!");
						}
					} else {
						cs.sendMessage("Du musst einen Parkour angeben!");
					}
				} else if (args[0].equalsIgnoreCase("join") && cs.hasPermission("pfxtr.join")) {
					if (args.length == 2) {
						if (InGame.getInstance().isPlayerInList(p) == false) {
							if (TempleRun.plugin.getParkourByName(args[1]) != null) {
								TempleRun.plugin.getParkourByName(args[1]).join(p);
								cs.sendMessage("Du hast das Spiel betreten!");
							} else {
							cs.sendMessage("Dieser Parkour existiert nicht!");
							}
						} else {
							cs.sendMessage("Du bist bereits in einem Spiel!");
						}
					} else {
						cs.sendMessage("Du musst einen Parkour angeben!");
					}
				} else if (args[0].equalsIgnoreCase("leave") && cs.hasPermission("pfxtr.leave")) {
					if (InGame.getInstance().isPlayerInList(p) == true) {
						TempleRun.plugin.getParkourByName(((MetadataValue)p.getMetadata("intr").get(0)).asString()).leave(p);
						cs.sendMessage("Du hast das Spiel verlassen!");
					} else {
						cs.sendMessage("Du bist in keinem Spiel!");
					}
				} else if (args[0].equalsIgnoreCase("list") && cs.hasPermission("pfxtr.list")) {
					if (TempleRun.plugin.parkours.size() == 0) {
						cs.sendMessage("Es gibt keine Parkours!");
					} else {
						cs.sendMessage("Das sind alle Parkours:");
						int i = 1;
						for (Parkour parkour : TempleRun.plugin.parkours) {
							cs.sendMessage(" - (" + i + ") " + parkour.getName());
							i++;
						}
						cs.sendMessage("gesammt: " + (i - 1));
					}
				} else {
					cs.sendMessage("Der Befehl ist falsch! -> /pfxtr");
				}
			} else {
				cs.sendMessage("§6= = = = §a§lPlanet-Fx TempleRun §6= = = =");
				cs.sendMessage("§6Versuche: §f/pfxtr help");
			}
		} else {
			cs.sendMessage("Für diesen Command musst du ein Spieler sein!");
		}
		return true;
	}
}