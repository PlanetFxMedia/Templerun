package de.SebastianMikolai.PlanetFx.TempleRun.Parkour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import de.SebastianMikolai.PlanetFx.TempleRun.LocationUtil;
import de.SebastianMikolai.PlanetFx.TempleRun.TempleRun;
import de.SebastianMikolai.PlanetFx.TempleRun.Datenbank.InGame;
import de.SebastianMikolai.PlanetFx.TempleRun.Tasks.RemoveMetadataTask;
import de.SebastianMikolai.PlanetFx.TempleRun.Tasks.TickTask;
import de.SebastianMikolai.PlanetFx.TempleRun.Tasks.TimerTask;

public class Parkour implements Listener {
	
	private final TempleRun instance;
	private List<Player> players = new ArrayList<Player>();
	private Map<Player, Integer> score = new HashMap<Player, Integer>();
	private Map<Player, List<Block>> goldTook = new HashMap<Player, List<Block>>();
	private Map<Player, Scoreboard> scoreboards = new HashMap<Player, Scoreboard>();
	private Map<Player, Objective> stats = new HashMap<Player, Objective>();
	private Map<Player, BukkitRunnable> timerTask = new HashMap<Player, BukkitRunnable>();
	private Map<Player, Integer> timer = new HashMap<Player, Integer>();
 	private Map<Player, GameMode> gamemode = new HashMap<Player, GameMode>();
 	private Map<Player, Integer> hunger = new HashMap<Player, Integer>();
 	private Map<Player, ItemStack[]> inventories = new HashMap<Player, ItemStack[]>();
 	private Map<Player, Integer> fails = new HashMap<Player, Integer>();
  	private Map<Player, Location> oldLoc = new HashMap<Player, Location>();
  	private final String NAME;
  	private Location spawn;
  	private Map<Player, Location> cp = new HashMap<Player, Location>();
  	private boolean isTicking = false;
  	private ScoreboardManager manager = Bukkit.getScoreboardManager();
  
  	public Parkour(String name, Location spawn, TempleRun instance) {
  		this.NAME = name;
  		this.spawn = spawn;
  		this.instance = instance;
  		Bukkit.getPluginManager().registerEvents(this, instance);
  	}
  
  	public void tick()  {
  		this.isTicking = true;
  		if (this.players.size() > 0) {
  			new TickTask(this).runTaskLater(this.instance, 20L);
  			if (this.instance.getConfig().getBoolean("gameSettings.kick-afk")) {
  				for (Player p : getPlayers()) {
  					if (this.oldLoc.containsKey(p)) {
  						if (!checkIfMoved(p.getLocation(), (Location)this.oldLoc.get(p))) {
  							if (p.hasMetadata("trrafk")) {
  								if (((MetadataValue)p.getMetadata("trrafk").get(0)).asInt() >= 10) {
  									kick(p, "Du darfst nicht stehen bleiben!");
  								} else {
  									p.setMetadata("trrafk", new FixedMetadataValue(this.instance, Integer.valueOf(((MetadataValue)p.getMetadata("trrafk").get(0)).asInt() + 1)));
  								}
  							} else {
  								p.setMetadata("trrafk", new FixedMetadataValue(this.instance, Integer.valueOf(1)));
  							}
  						} else if (p.hasMetadata("trrafk")) {
  							p.removeMetadata("trrafk", this.instance);
  						}
  						this.oldLoc.put(p, p.getLocation());
  					}
  				}
  			}
  		} else {
  			this.isTicking = false;
  		}
  	}
  
  	public List<Player> getPlayers() {
  		List<Player> result = new ArrayList<Player>();
  		for (Player p : this.players) {
  			result.add(p);
  		}
  		return result;
  	}
  
  	public void join(Player p) {
  		if (InGame.getInstance().isPlayerInList(p) == false) {
  			if (!this.isTicking) {
  				new TickTask(this).runTaskLater(this.instance, 20L);
  			}
  			resetMeta(p);
  			this.players.add(p);
  			this.hunger.put(p, Integer.valueOf(p.getFoodLevel()));
  			p.setFoodLevel(20);
  			this.score.put(p, Integer.valueOf(0));
  			this.fails.put(p, Integer.valueOf(0));
  			this.gamemode.put(p, p.getGameMode());
  			p.setGameMode(GameMode.ADVENTURE);
  			ItemStack[] inventory = p.getInventory().getContents();
  			ItemStack[] saveInventory = new ItemStack[inventory.length];
  			for (int i = 0; i < inventory.length; i++) {
  				if (inventory[i] != null) {
  					saveInventory[i] = inventory[i].clone();
  				}
  			}
  			this.inventories.put(p, saveInventory);
  			p.getInventory().clear();
  			p.setMetadata("intrr", new FixedMetadataValue(this.instance, this.NAME));
  			p.setMetadata("oldloctrr", new FixedMetadataValue(this.instance, LocationUtil.LocationToString(p.getLocation())));
  			p.teleport(this.spawn);
  			this.oldLoc.put(p, p.getLocation());
  			this.scoreboards.put(p, this.manager.getNewScoreboard());
  			p.setScoreboard((Scoreboard)this.scoreboards.get(p));
  			if (p.getActivePotionEffects().size() > 0) {
  				for (PotionEffect effect : p.getActivePotionEffects()) {
  					p.removePotionEffect(effect.getType());
  				}
  			}
  			float speed = this.instance.getConfig().getInt("gameSettings.default-speed");
  			p.setWalkSpeed(speed / 10.0F);
  			Objective obj = ((Scoreboard)this.scoreboards.get(p)).registerNewObjective("gold", "dummy");
  			obj.setDisplaySlot(DisplaySlot.SIDEBAR);
  			obj.setDisplayName(ChatColor.DARK_RED + "Stats");
  			startTimer(p);
  			this.stats.put(p, obj);
  			this.goldTook.put(p, new ArrayList<Block>());
  		}
  	}
  
  	public void kick(Player p, String reason) {
  		leave(p);
  		p.sendMessage(TempleRun.prefix + "Du wurdest gekickt: " + reason);
  	}
  
  	public void leave(Player p) {
  		if (this.players.contains(p)) {
  			p.setGameMode((GameMode)this.gamemode.get(p));
  			this.gamemode.remove(p);
  			this.oldLoc.remove(p);
  			p.setFoodLevel(((Integer)this.hunger.get(p)).intValue());
  			this.hunger.remove(p);
  			this.fails.remove(p);
  			InGame.getInstance().remove(p);
  			p.removeMetadata("trrafk", this.instance);
  			p.teleport(LocationUtil.StringToLoc(((MetadataValue)p.getMetadata("oldloctrr").get(0)).asString()));
  			p.removeMetadata("oldloctrr", this.instance);
  			if (p.getActivePotionEffects().size() > 0) {
  				for (PotionEffect effect : p.getActivePotionEffects()) {
  					p.removePotionEffect(effect.getType());
  				}
  			}
  			if (this.inventories.get(p) != null) {
  				p.getInventory().setContents((ItemStack[])this.inventories.get(p));
  			}
  			this.inventories.remove(p);
  			p.setWalkSpeed(0.2F);
  			this.players.remove(p);
  			this.score.remove(p);
  			this.stats.remove(p);
  			this.scoreboards.remove(p);
  			this.goldTook.remove(p);
  			stopTimer(p);
  			p.setScoreboard(this.manager.getMainScoreboard());
  			cp.remove(p);
  		}
  	}
  
  	public void reset(Player p) {
  		this.score.remove(p);
  		this.stats.remove(p);
    	this.scoreboards.remove(p);
    	this.goldTook.remove(p);
    	stopTimer(p);
    	this.score.put(p, Integer.valueOf(0));
    	p.teleport(this.spawn);
    	this.scoreboards.put(p, this.manager.getNewScoreboard());
    	p.setScoreboard((Scoreboard)this.scoreboards.get(p));
    	if (p.getActivePotionEffects().size() > 0) {
    		for (PotionEffect effect : p.getActivePotionEffects()) {
    			p.removePotionEffect(effect.getType());
    		}
    	}
    	Objective obj = ((Scoreboard)this.scoreboards.get(p)).registerNewObjective("gold", "dummy");
    	obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    	obj.setDisplayName(ChatColor.DARK_RED + "Stats");
    	this.stats.put(p, obj);
    	this.goldTook.put(p, new ArrayList<Block>());
    	startTimer(p);
  	}
  	
  	public void resetCP(Player p) {
    	p.teleport(this.cp.get(p));
    	if (p.getActivePotionEffects().size() > 0) {
    		for (PotionEffect effect : p.getActivePotionEffects()) {
    			p.removePotionEffect(effect.getType());
    		}
    	}
  	}
  	
  	public void kickall() {
  		List<Player> toKick = new ArrayList<Player>();
  		for (Player p : this.players) {
  			toKick.add(p);
  		}
  		for (Player p : toKick) {
  			kick(p, "Parkour abgestüzt");
  		}
  	}
  
  	private void startTimer(Player p) {
  		this.timerTask.put(p, new TimerTask(p, this, 0, 1));
  		this.timer.put(p, Integer.valueOf(0));
  		((BukkitRunnable)this.timerTask.get(p)).runTaskLater(this.instance, 20L);
  	}
  
  	public void updateTimer(Player p, int time) {
  		if (this.players.contains(p)) {
  			this.timerTask.remove(p);
  			this.timerTask.put(p, new TimerTask(p, this, time, 1));
  			((BukkitRunnable)this.timerTask.get(p)).runTaskLater(this.instance, 20L);
  			this.timer.put(p, Integer.valueOf(time));
  			Objective objective = (Objective)this.stats.get(p);
  			@SuppressWarnings("deprecation")
			Score scoreBoard = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN + "" + ChatColor.BOLD + "TIME"));
  			scoreBoard.setScore(time);
  		}
  	}
  
  	private void stopTimer(Player p) {
  		if (this.timerTask.get(p) != null) {
  			((BukkitRunnable)this.timerTask.get(p)).cancel();
  			this.timer.remove(p);
  			this.timerTask.remove(p);
  		}
  	}
  
  	public static Parkour load(String name, String spawn, TempleRun instance) {
  		Parkour result = new Parkour(name, LocationUtil.StringToLoc(spawn), instance);
  		return result;
  	}
  
  	public void increaseScore(Player p) {
  		increaseScore(p, 1);
 	}
  
  	public void increaseScore(Player p, int amount) {
  		if ((amount > 0) && (this.players.contains(p))) {
  			this.score.put(p, Integer.valueOf(((Integer)this.score.get(p)).intValue() + amount));
  			Objective objective = (Objective)this.stats.get(p);
  			@SuppressWarnings("deprecation")
			Score scoreBoard = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW +  "" + ChatColor.BOLD + "GOLD"));
  			scoreBoard.setScore(((Integer)this.score.get(p)).intValue());
  		}
  	}
  
  	public String getName() {
  		return this.NAME;
  	}
  
  	public Location getSpawn() {
  		return this.spawn.clone();
  	}
  
  	public void resetMeta(Player p) {
  		p.removeMetadata("trremeraldcooldown", this.instance);
  		p.removeMetadata("trrredstonecooldown", this.instance);
  		p.removeMetadata("trrcoalcooldown", this.instance);
  		p.removeMetadata("trrironcooldown", this.instance);
  	}
  
  	private boolean checkIfMoved(Location loc, Location loc1) {
  		double x = loc.getX();
  		double y = loc.getY();
  		double z = loc.getZ();
  		double x1 = loc1.getX();
  		double y1 = loc1.getY();
  		double z1 = loc1.getZ();
  		if ((x != x1) || (y != y1) || (z != z1)) {
  			return true;
  		}
  		return false;
  	}
  	
	@SuppressWarnings("deprecation")
	@EventHandler
  	public void onPlayerMove(PlayerMoveEvent event) {
  		Player player = event.getPlayer();
  		if (this.players.contains(player)) {
  			Block block = player.getLocation().clone().add(0.0D, -1.0D, 0.0D).getBlock();
  			if (block != null) {
  				if (block.getType().equals(Material.GOLD_BLOCK)) {
  					if (!((List<Block>)this.goldTook.get(player)).contains(block)) {
  						block.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 2.0F);
  						((List<Block>)this.goldTook.get(player)).add(block);
  						increaseScore(player);
  					}
  				} else if (block.getType().equals(Material.DIAMOND_BLOCK)) {
  					for (Player p : player.getWorld().getPlayers()) {
  						p.sendMessage(player.getName() + " hat den Parkour " + ChatColor.DARK_RED + getName() + ChatColor.RESET + " in " + ChatColor.GREEN + this.timer.get(player) + " Sekunden" + ChatColor.RESET + " beendet und hat " + ChatColor.YELLOW + this.score.get(player) + " Gold" + ChatColor.RESET + " geholt!");
  					}
  					leave(player);
  				} else if (block.getType().equals(Material.LAPIS_BLOCK)) {
  					cp.put(player, player.getLocation());
  					player.sendTitle("", ChatColor.BLUE + "Checkpoint!");
  					InGame.getInstance().setCheckpoint(player);
  				} else if (block.getType().equals(Material.EMERALD_BLOCK)) {
  					if (!player.hasMetadata("trremeraldcooldown")) {
  						player.setMetadata("trremeraldcooldown", new FixedMetadataValue(this.instance, this.NAME));
  						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2), true);
  						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.5F);
  					}
  					new RemoveMetadataTask(player, "trremeraldcooldown", this.instance).runTaskLater(this.instance, 20L);
  				} else if (block.getType().equals(Material.COAL_BLOCK)) {
  					if (!player.hasMetadata("trrcoalcooldown")) {
  						player.setMetadata("trrcoalcooldown", new FixedMetadataValue(this.instance, this.NAME));
  						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0), true);
  						player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0F, 0.75F);
  					}
  					new RemoveMetadataTask(player, "trrcoalcooldown", this.instance).runTaskLater(this.instance, 20L);
  				} else if (block.getType().equals(Material.REDSTONE_BLOCK)) {
  					if (!player.hasMetadata("trrredstonecooldown")) {
  						player.setMetadata("trrredstonecooldown", new FixedMetadataValue(this.instance, this.NAME));
  						Location loc = new Location(player.getWorld(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
  						loc.setYaw(player.getLocation().getYaw() + 180.0F);
  						player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.5F);
  						player.teleport(loc);
  					}
  					new RemoveMetadataTask(player, "trrredstonecooldown", this.instance).runTaskLater(this.instance, 40L);
  				} else if (block.getType().equals(Material.IRON_BLOCK)) {
  					if (!player.hasMetadata("trrironcooldown")) {
  						player.setVelocity(player.getVelocity().clone().setY(1.5D));
  						player.setMetadata("trrironcooldown", new FixedMetadataValue(this.instance, this.NAME));
  					}
  					new RemoveMetadataTask(player, "trrironcooldown", this.instance).runTaskLater(this.instance, 20L);
  				} else if ((player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) || (player.getLocation().getBlock().getType().equals(Material.WATER)) || (player.getLocation().getBlock().getType().equals(Material.LAVA)) || (player.getLocation().getBlock().getType().equals(Material.STATIONARY_LAVA))) {
  					if (cp.get(player) != null) {
  						player.sendMessage(TempleRun.prefix + "Du bist reingefallen O.o");
  						this.fails.put(player, Integer.valueOf(((Integer)this.fails.get(player)).intValue() + 1));
  						int fails = ((Integer)this.fails.get(player)).intValue();
  						resetCP(player);
  						resetMeta(player);
  						Objective objective = (Objective)this.stats.get(player);
  						Score scoreBoard = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "FAILS"));
  						scoreBoard.setScore(fails);
  					} else if (cp.get(player) == null){
  						player.sendMessage(TempleRun.prefix + "Du bist reingefallen O.o");
  						this.fails.put(player, Integer.valueOf(((Integer)this.fails.get(player)).intValue() + 1));
  						int fails = ((Integer)this.fails.get(player)).intValue();
  						reset(player);
  						resetMeta(player);
  						Objective objective = (Objective)this.stats.get(player);
  						Score scoreBoard = objective.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "FAILS"));
  						scoreBoard.setScore(fails);
  					}
  				}
  			}
  		}
  	}
  
  	@EventHandler
  	public void playerInteract(PlayerInteractEvent event) {
  		if (this.players.contains(event.getPlayer())) {
  			event.setCancelled(true);
  		}
  	}
  
  	@EventHandler
  	public void playerInteract(EntityDamageEvent event) {
  		if (this.players.contains(event.getEntity())) {
  			event.setCancelled(true);
  		}
  	}
  
  	@EventHandler
  	public void playerPlaceBlock(BlockPlaceEvent event) {
  		if (this.players.contains(event.getPlayer())) {
  			event.setCancelled(true);
  		}
  	}
  
  	@EventHandler
  	public void playerBreakBlock(BlockBreakEvent event) {
  		if (this.players.contains(event.getPlayer())) {
  			event.setCancelled(true);
  		}
  	}
  
  	@EventHandler
  	public void playerDropItem(PlayerDropItemEvent event) {
  		if (this.players.contains(event.getPlayer())) {
  			event.setCancelled(true);
  		}
  	}
  
  	@EventHandler
  	public void PlayerGetHungry(FoodLevelChangeEvent event) {
  		if (this.players.contains(event.getEntity())) {
  			event.setCancelled(true);
  		}
  	}
  
  	@EventHandler
  	public void onPlayerQuit(PlayerQuitEvent event) {
  		if (this.players.contains(event.getPlayer())) {
  			leave(event.getPlayer());
  		}
  	}
}