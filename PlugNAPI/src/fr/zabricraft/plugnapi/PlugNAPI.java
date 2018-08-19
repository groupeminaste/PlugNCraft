/*
 *  Copyright (C) 2018 FALLET Nathan
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 */

package fr.zabricraft.plugnapi;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fr.zabricraft.craftsearch.CraftSearch;
import fr.zabricraft.plugnapi.events.BlockBreak;
import fr.zabricraft.plugnapi.events.BlockPlace;
import fr.zabricraft.plugnapi.events.CreatureSpawn;
import fr.zabricraft.plugnapi.events.EntityDamage;
import fr.zabricraft.plugnapi.events.EntityExplode;
import fr.zabricraft.plugnapi.events.PlayerChat;
import fr.zabricraft.plugnapi.events.PlayerDropItem;
import fr.zabricraft.plugnapi.events.PlayerJoin;
import fr.zabricraft.plugnapi.events.PlayerLogin;
import fr.zabricraft.plugnapi.events.PlayerQuit;
import fr.zabricraft.plugnapi.events.WeatherChange;
import fr.zabricraft.plugnapi.utils.GameStartEvent;
import fr.zabricraft.plugnapi.utils.ServerStatus;
import fr.zabricraft.plugnapi.utils.ZabriPlayer;

public class PlugNAPI extends JavaPlugin {

	private static PlugNAPI instance;

	public static PlugNAPI getInstance() {
		return instance;
	}

	private ArrayList<ZabriPlayer> players;
	private Connection connection;
	private int countdown;

	public String getIp() {
		return "78.46.91.153";
	}

	public ZabriPlayer getPlayer(UUID uuid) {
		for (ZabriPlayer current : players) {
			if (current.getUUID().equals(uuid)) {
				return current;
			}
		}
		return null;
	}

	public void initPlayer(Player p) {
		players.add(new ZabriPlayer(p));
	}

	public void uninitPlayer(ZabriPlayer p) {
		if (players.contains(p)) {
			players.remove(p);
		}
	}

	public void onEnable() {
		instance = this;
		players = new ArrayList<ZabriPlayer>();
		setStatus(ServerStatus.WAITING);

		saveDefaultConfig();
		reloadConfig();

		for (Player p : Bukkit.getOnlinePlayers()) {
			initPlayer(p);
		}
		setPlayers(Bukkit.getOnlinePlayers().size());

		Bukkit.getPluginManager().registerEvents(new BlockBreak(), this);
		Bukkit.getPluginManager().registerEvents(new BlockPlace(), this);
		Bukkit.getPluginManager().registerEvents(new CreatureSpawn(), this);
		Bukkit.getPluginManager().registerEvents(new EntityDamage(), this);
		Bukkit.getPluginManager().registerEvents(new EntityExplode(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerChat(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDropItem(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLogin(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuit(), this);
		Bukkit.getPluginManager().registerEvents(new WeatherChange(), this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				Template template = new Template(getTemplate());
				if (!getStatus().equals(ServerStatus.PLAYING)) {
					if (Bukkit.getOnlinePlayers().size() >= template.getMinPlayers()
							&& getStatus().equals(ServerStatus.WAITING)) {
						setStatus(ServerStatus.COUNTDOWN);
						countdown = 61;
					}
					if (Bukkit.getOnlinePlayers().size() < template.getMinPlayers()
							&& getStatus().equals(ServerStatus.COUNTDOWN)) {
						setStatus(ServerStatus.WAITING);
						countdown = 0;
					}
					if (getStatus().equals(ServerStatus.COUNTDOWN)) {
						countdown--;
						if (countdown == 0) {
							setStatus(ServerStatus.PLAYING);
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.getInventory().clear();
								p.updateInventory();
								p.setGameMode(GameMode.SURVIVAL);
							}
							GameStartEvent start = new GameStartEvent();
							Bukkit.getPluginManager().callEvent(start);
						} else if (countdown == 60 || countdown == 30 || countdown == 20 || countdown == 10
								|| countdown <= 5) {
							PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
							pc.getTitleActions().write(0, TitleAction.TITLE);
							pc.getChatComponents().write(0, WrappedChatComponent
									.fromText("§a" + getStatus().getShortText().replaceAll("%d", countdown + "")));
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.sendMessage("§e" + getStatus().getText().replaceAll("%d", countdown + ""));
								try {
									ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				ArrayList<String> lines = new ArrayList<String>();
				lines.add("§d");
				lines.add("§d§lServeur :");
				lines.add("§f" + template.getName());
				lines.add("§b");
				lines.add("§b§lJoueurs :");
				lines.add("§f" + Bukkit.getOnlinePlayers().size() + "/" + template.getMaxPlayers());
				if (!template.getId().equals("hub")) {
					lines.add("§a");
					lines.add("§a§lStatut :");
					lines.add("§f" + getStatus().getShortText().replaceAll("%d", countdown + ""));
				}
				lines.add("§e");
				lines.add("§e§l-- ND QUE PLUGN");
				lines.add("§e§lACHETERA POUR PNC --");
				for (Player p : Bukkit.getOnlinePlayers()) {
					ZabriPlayer zp = getPlayer(p.getUniqueId());
					zp.getScoreboard().update(p, lines);
				}
			}
		}, 0, 20);
	}

	public void onDisable() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			CraftSearch.getInstance().connectSwitcher(p, getIp() + ":26000");
		}
		setStatus(ServerStatus.OFFLINE);
		setPlayers(0);
	}

	public void stop(Player winner) {
		if (getStatus().equals(ServerStatus.PLAYING)) {
			setStatus(ServerStatus.FINISHED);

			if (winner != null) {
				Bukkit.broadcastMessage("§e" + winner.getName() + " §7remporte la partie !");
				PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
				pc.getTitleActions().write(0, TitleAction.TITLE);
				pc.getChatComponents().write(0, WrappedChatComponent.fromText("§aVous avez gagné la partie !"));
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(winner, pc);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.getInventory().clear();
				p.updateInventory();
				p.setGameMode(GameMode.SPECTATOR);
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				@Override
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers()) {
						CraftSearch.getInstance().connectSwitcher(p, getIp() + ":26000");
					}
					Bukkit.getScheduler().scheduleSyncRepeatingTask(PlugNAPI.getInstance(), new Runnable() {
						@Override
						public void run() {
							if (Bukkit.getOnlinePlayers().size() == 0) {
								Bukkit.shutdown();
							}
						}
					}, 0, 20);
				}
			}, 100);
		}
	}

	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				FileConfiguration conf = getConfig();
				connection = DriverManager.getConnection(
						"jdbc:mysql://" + conf.getString("database.host") + ":" + conf.getInt("database.port") + "/"
								+ conf.getString("database.database"),
						conf.getString("database.user"), conf.getString("database.password"));
			}
		} catch (SQLException | ClassNotFoundException e) {
			getLogger().severe("Un probleme est survenue lors de la connexion au serveur MySQL, arret du serveur...");
			Bukkit.shutdown();
			return null;
		}
		return connection;
	}

	public ServerStatus getStatus() {
		ServerStatus status = ServerStatus.WAITING;
		try {
			PreparedStatement state = getConnection().prepareStatement("SELECT status FROM servers WHERE id = ?");
			state.setString(1, getServer().getServerName());
			ResultSet result = state.executeQuery();
			if (result.next()) {
				status = ServerStatus.valueOf(result.getString("status"));
			}
			result.close();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public void setStatus(ServerStatus status) {
		if (!status.equals(getStatus())) {
			Template template = new Template(getTemplate());
			CraftSearch.getInstance().setData("PlugNCraft - " + template.getName(),
					template.getDescription() + (template.getId().equals("hub") ? ""
							: "\n\n" + status.getText().replaceAll("%d", countdown + "")));
			try {
				PreparedStatement state = getConnection()
						.prepareStatement("UPDATE servers SET status = ? WHERE id = ?");
				state.setString(1, status.toString());
				state.setString(2, getServer().getServerName());
				state.executeUpdate();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setPlayers(int players) {
		try {
			PreparedStatement state = getConnection().prepareStatement("UPDATE servers SET players = ? WHERE id = ?");
			state.setInt(1, players);
			state.setString(2, getServer().getServerName());
			state.executeUpdate();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTemplate() {
		String template = "";
		try {
			PreparedStatement state = getConnection().prepareStatement("SELECT type FROM servers WHERE id = ?");
			state.setString(1, getServer().getServerName());
			ResultSet result = state.executeQuery();
			if (result.next()) {
				template = result.getString("type");
			}
			result.close();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return template;
	}

	public class Template {

		private String id;

		public Template(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			String name = "";
			try {
				PreparedStatement state = getConnection().prepareStatement("SELECT name FROM games WHERE id = ?");
				state.setString(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					name = result.getString("name");
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return name;
		}

		public String getDescription() {
			String description = "";
			try {
				PreparedStatement state = getConnection()
						.prepareStatement("SELECT description FROM games WHERE id = ?");
				state.setString(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					description = result.getString("description");
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return description;
		}

		public int getMinPlayers() {
			int min_players = 0;
			try {
				PreparedStatement state = getConnection()
						.prepareStatement("SELECT min_players FROM games WHERE id = ?");
				state.setString(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					min_players = result.getInt("min_players");
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return min_players;
		}

		public int getMaxPlayers() {
			int max_players = 0;
			try {
				PreparedStatement state = getConnection()
						.prepareStatement("SELECT max_players FROM games WHERE id = ?");
				state.setString(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					max_players = result.getInt("max_players");
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return max_players;
		}

		public Material getIcon() {
			Material icon = Material.WOOL;
			try {
				PreparedStatement state = getConnection().prepareStatement("SELECT icon FROM games WHERE id = ?");
				state.setString(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					icon = Material.valueOf(result.getString("icon"));
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return icon;
		}

	}

	public class Map {

		private int id;

		public Map(int id) {
			this.id = id;
		}

		public String getMapHub() {
			String map_hub = "";
			try {
				PreparedStatement state = getConnection().prepareStatement("SELECT map_hub FROM maps WHERE id = ?");
				state.setInt(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					map_hub = result.getString("map_hub");
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map_hub;
		}

		public String getMapGame() {
			String map_game = "";
			try {
				PreparedStatement state = getConnection().prepareStatement("SELECT map_game FROM maps WHERE id = ?");
				state.setInt(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					map_game = result.getString("map_game");
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return map_game;
		}

		public Material getIcon() {
			Material icon = Material.WOOL;
			try {
				PreparedStatement state = getConnection().prepareStatement("SELECT icon FROM maps WHERE id = ?");
				state.setInt(1, id);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					icon = Material.valueOf(result.getString("icon"));
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return icon;
		}

	}

}
