package de.SebastianMikolai.PlanetFx.TempleRun.Datenbank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;

import de.SebastianMikolai.PlanetFx.TempleRun.LocationUtil;
import de.SebastianMikolai.PlanetFx.TempleRun.TempleRun;
import de.SebastianMikolai.PlanetFx.TempleRun.Parkour.Parkour;

public class MySQL {
	
	public static Connection c = null;
	public static Statement database;
 
	public static void Connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:mysql://" + TempleRun.plugin.getConfig().getString("database.host", "") + ":" + 
					TempleRun.plugin.getConfig().getInt("database.port", 3306) + "/" + TempleRun.plugin.getConfig().getString("database.db", "") + 
					"?user=" + TempleRun.plugin.getConfig().getString("database.user", "") + "&password=" + TempleRun.plugin.getConfig().getString("database.password", ""));
			database = c.createStatement();
			TempleRun.plugin.getLogger().info("Die Verbindung zur Datenbank wurde hergestellt!");
		} catch (Exception e) {
			TempleRun.plugin.getLogger().info("ERROR #01 Connect: Die Verbindung zur Datenbank konnte nicht hergestellt werden!");
			TempleRun.plugin.getPluginLoader().disablePlugin(TempleRun.plugin);
		}
	}
	
	public static void LadeTabellen() {
		try {
			Statement stmt = c.createStatement();
			ResultSet rss = stmt.executeQuery("SHOW TABLES LIKE 'PlanetFx_TempleRun'");
			if (rss.next()) {
				TempleRun.plugin.getLogger().info("Die Tabelle PlanetFx_TempleRun wurde geladen!");
			} else {
				int rs = stmt.executeUpdate("CREATE TABLE PlanetFx_TempleRun (id INTEGER PRIMARY KEY AUTO_INCREMENT, name TEXT, spawn TEXT)");
				TempleRun.plugin.getLogger().info("Die Tabelle PlanetFx_TempleRun wurde erstellt! (" + rs + ")");
			}	
		} catch (SQLException e) {
			e.printStackTrace();
			TempleRun.plugin.getLogger().info("ERROR #02 LadeTabellen: Die Tabellen konnte nicht geladen werden!");
			TempleRun.plugin.getPluginLoader().disablePlugin(TempleRun.plugin);
		}
	}
	
	public static void LoadParkours() {
		TempleRun.plugin.parkours.clear();
		try {
			Statement stmt = c.createStatement();
			ResultSet rss = stmt.executeQuery("SELECT * FROM PlanetFx_TempleRun");
			while (rss.next()) {
				TempleRun.plugin.registerParkour(Parkour.load(rss.getString("name"), rss.getString("spawn"), TempleRun.plugin));
			}
		} catch (SQLException e) {
			TempleRun.plugin.getLogger().info("ERROR #04 LoadParkours: Die Einträge konnten nicht geladen werden!");
			e.printStackTrace();
		}
	}
	
	public static void SaveParkours() {
		for (Parkour parkour : TempleRun.plugin.parkours) {
			parkour.kickall();
			try {
				Statement stmt = c.createStatement();
				String sql = "SELECT * FROM PlanetFx_TempleRun WHERE name=" + parkour.getName();
				ResultSet rss = stmt.executeQuery(sql);
				if (rss.next()) {
					UpdateParkour(parkour);
				} else {
					SaveParkour(parkour.getName(), parkour.getSpawn());
				}
			} catch (SQLException e) {
				TempleRun.plugin.getLogger().info("ERROR #06 UpdateParkour: Der Datensatz konnte nicht eingetragen werden!");
				e.printStackTrace();
			}
        }
	}
	
	public static void SaveParkour(String name, Location loc) {
		try {
			Statement stmt = c.createStatement();
			boolean bool = stmt.execute("INSERT INTO PlanetFx_TempleRun (name, spawn) VALUES ('" + name + "', '" + LocationUtil.LocationToString(loc) + "')");
			TempleRun.plugin.getLogger().info("Neuer Eintrag! (" + name + ", " + bool + ")");
		} catch (SQLException e) {
			TempleRun.plugin.getLogger().info("ERROR #03 SaveParkour: Der Datensatz konnte nicht eingetragen werden!");
			e.printStackTrace();
		}
	}
	
	public static void UpdateParkour(Parkour parkour) {
		try {
			Statement stmt = c.createStatement();
			String sql = "SELECT * FROM PlanetFx_TempleRun WHERE name=" + parkour.getName();
			ResultSet rss = stmt.executeQuery(sql);
			while (rss.next()) {
				Statement updatestmt = c.createStatement();
				String updatesql = "UPDATE PlanetFx_TempleRun SET name = '" + parkour.getName() + "', spawn = '" + LocationUtil.LocationToString(parkour.getSpawn()) + "' WHERE id=" + rss.getInt("id");
				int i = updatestmt.executeUpdate(updatesql);
				TempleRun.plugin.getLogger().info("Tabellenupdate! (" + parkour.getName() + ", " + i + ")");
			}
		} catch (SQLException e) {
			TempleRun.plugin.getLogger().info("ERROR #06 UpdateParkour: Der Datensatz konnte nicht eingetragen werden!");
			e.printStackTrace();
		}
	}
	
	public static void RemoveParkour(Parkour parkour) {
		try {
			Statement stmt = c.createStatement();
			ResultSet rss = stmt.executeQuery("SELECT * FROM PlanetFx_TempleRun");
			while (rss.next()) {
				if (rss.getString("name") == parkour.getName()) {
					Statement removestmt = c.createStatement();
					String sql ="DELETE FROM PlanetFx_TempleRun WHERE id=" + rss.getInt("id");
					ResultSet removerss = removestmt.executeQuery(sql);
					TempleRun.plugin.getLogger().info("Eintrag gelöscht! (" + parkour.getName() + ", " + removerss.isBeforeFirst() + ")");
				}	
			}	
		} catch (SQLException e) {
			TempleRun.plugin.getLogger().info("ERROR #03 SaveParkour: Der Datensatz konnte nicht eingetragen werden!");
			e.printStackTrace();
		}
	}
}