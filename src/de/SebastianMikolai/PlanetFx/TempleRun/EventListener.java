package de.SebastianMikolai.PlanetFx.TempleRun;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener {
	
	@EventHandler
	public void onBlockPlaced(SignChangeEvent event) {
		String[] lines = (String[])event.getLines().clone();
		if (lines[0].startsWith("[tr]")) {
			if (lines[1].length() > 0) {
				if (TempleRun.plugin.getParkourByName(lines[1]) != null) {
					event.setLine(0, ChatColor.DARK_RED + "TempleRun");
					event.setLine(1, ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Join" + ChatColor.DARK_GRAY + "]");
					event.setLine(2, lines[1]);
					event.setLine(3, "by PlanetFx");
					event.getPlayer().sendMessage(TempleRun.prefix + "Join Schild erstellt!");
				} else {
					event.getPlayer().sendMessage(TempleRun.prefix + "Dieser Parkour existiert nicht: " + lines[1]);
				}
			} else {
				event.getPlayer().sendMessage(TempleRun.prefix + "Du musst einen Parkour angeben!");
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if ((!player.hasMetadata("intr")) && (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			Block block = event.getClickedBlock();
			if ((block.getState() instanceof Sign)) {
				Sign sign = (Sign)block.getState();
				if (sign.getLine(0).equals(ChatColor.DARK_RED + "TempleRun") && sign.getLine(1).equals(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Join" + ChatColor.DARK_GRAY + "]")) {
					if (TempleRun.plugin.getParkourByName(sign.getLine(2)) != null) {
						TempleRun.plugin.getParkourByName(sign.getLine(2)).join(player);
						event.getPlayer().sendMessage(TempleRun.prefix + "Du hast das Spiel betreten!");
					} else {
						event.getPlayer().sendMessage(TempleRun.prefix + "Dieser Parkour existiert nicht!");
					}
				}
			}
		}
	}
}