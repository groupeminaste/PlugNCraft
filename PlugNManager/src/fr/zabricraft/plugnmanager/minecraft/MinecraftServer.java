/*
 *  Copyright (C) 2017 FALLET Nathan
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

package fr.zabricraft.plugnmanager.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import fr.zabricraft.plugnmanager.PlugNManager;

public class MinecraftServer {

	private String name;
	private Process process;
	private String logs;

	public MinecraftServer(String name) {
		this.name = name;
		this.logs = "";
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					log("Starting minecraft server...");

					Template template = new Template(getTemplate());
					Map map = new Map(getMap());

					File launcher = new File("spigot.jar");
					if (!launcher.exists()) {
						log("File spigot.jar not found, can not start server!");
						PlugNManager.getInstance().uninitServer(link());
						return;
					}

					log("Creating folder...");
					File main = new File(new File("servers"), name);
					if (!main.exists()) {
						main.mkdirs();
					}
					PlugNManager.getInstance().clearDirectory(main);

					log("Creating configuration files...");
					File eula = new File(main, "eula.txt");
					eula.createNewFile();
					FileWriter eula2 = new FileWriter(eula);
					eula2.write("eula=true");
					eula2.close();

					File properties = new File(main, "server.properties");
					properties.createNewFile();
					FileWriter properties2 = new FileWriter(properties);
					properties2.write("server-name=" + name + "\n");
					properties2.write("server-port=" + getPort() + "\n");
					properties2.write("max-players=" + template.getMaxPlayers() + "\n");
					properties2.write("level-name=hub\n");
					properties2.write("online-mode=false\n");
					properties2.write("announce-player-achievements=false\n");
					properties2.close();

					File spigot_conf = new File(main, "spigot.yml");
					spigot_conf.createNewFile();
					FileWriter spigot_conf2 = new FileWriter(spigot_conf);
					spigot_conf2.write("settings:\n");
					spigot_conf2.write("  bungeecord: true\n");
					spigot_conf2.write("  restart-on-crash: false\n");
					spigot_conf2.close();

					File plugins = new File(main, "plugins");
					plugins.mkdirs();

					log("Copying libraries...");
					File libs = new File("libs");

					for (File pl : libs.listFiles()) {
						File to = new File(plugins, pl.getName());
						if (pl.isDirectory()) {
							PlugNManager.getInstance().cloneDirectory(pl, to);
						} else {
							log("Copying lib " + pl.getName() + " to " + to.getAbsolutePath() + "...");
							Files.copy(pl.toPath(), to.toPath());
						}
					}

					log("Copying plugins...");
					for (File pl : new File(new File(new File("templates"), template.getId()), "plugins").listFiles()) {
						File to = new File(plugins, pl.getName());
						if (pl.isDirectory()) {
							PlugNManager.getInstance().cloneDirectory(pl, to);
						} else {
							log("Copying plugin " + pl.getName() + " to " + to.getAbsolutePath() + "...");
							Files.copy(pl.toPath(), to.toPath());
						}
					}

					log("Copying maps...");
					File map_hub = new File(new File(new File(new File("templates"), template.getId()), "maps"),
							map.getMapHub());
					if (map_hub.exists() && map_hub.isDirectory()) {
						File hub = new File(main, "hub");
						PlugNManager.getInstance().cloneDirectory(map_hub, hub);
						log("Copying map " + map_hub.getAbsolutePath() + " for hub...");
					}
					File map_game = new File(new File(new File(new File("templates"), template.getId()), "maps"),
							map.getMapGame());
					if (map_game.exists() && map_game.isDirectory()) {
						File game = new File(main, "game");
						PlugNManager.getInstance().cloneDirectory(map_game, game);
						log("Copying map " + map_game.getAbsolutePath() + " for game...");
					}

					log("Launching server...");
					process = Runtime.getRuntime().exec("java -Xmx2G -jar " + launcher.getCanonicalPath(), null, main);
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BufferedReader out = new BufferedReader(
										new InputStreamReader(process.getInputStream()));
								String line = "";
								while ((line = out.readLine()) != null) {
									log(line);
								}
							} catch (Exception e) {
							}
						}
					}).start();
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								BufferedReader out = new BufferedReader(
										new InputStreamReader(process.getErrorStream()));
								String line = "";
								while ((line = out.readLine()) != null) {
									log(line);
								}
							} catch (Exception e) {
							}
						}
					}).start();
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								process.waitFor();
								PlugNManager.getInstance().uninitServer(link());
							} catch (Exception e) {
							}
						}
					}).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public String getName() {
		return name;
	}

	public String getLogs() {
		return logs;
	}

	public void log(String line) {
		if (!line.equals(">")) {
			logs += line + "\n";
			System.out.println("[" + name + "] " + line);
		}
	}

	public void sendCommand(String cmd) {
		PrintStream in = new PrintStream(process.getOutputStream());
		in.println(cmd);
		in.flush();
	}

	private MinecraftServer link() {
		return this;
	}

	public String getTemplate() {
		String template = "";
		try {
			PreparedStatement state = PlugNManager.getInstance().getConnection()
					.prepareStatement("SELECT type FROM servers WHERE id = ?");
			state.setString(1, name);
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

	public int getMap() {
		int map = 0;
		try {
			PreparedStatement state = PlugNManager.getInstance().getConnection()
					.prepareStatement("SELECT map FROM servers WHERE id = ?");
			state.setString(1, name);
			ResultSet result = state.executeQuery();
			if (result.next()) {
				map = result.getInt("map");
			}
			result.close();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public int getPort() {
		int port = 0;
		try {
			PreparedStatement state = PlugNManager.getInstance().getConnection()
					.prepareStatement("SELECT port FROM servers WHERE id = ?");
			state.setString(1, name);
			ResultSet result = state.executeQuery();
			if (result.next()) {
				port = result.getInt("port");
			}
			result.close();
			state.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return port;
	}

	public class Template {

		private String id;

		public Template(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public int getMinPlayers() {
			int min_players = 0;
			try {
				PreparedStatement state = PlugNManager.getInstance().getConnection()
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
				PreparedStatement state = PlugNManager.getInstance().getConnection()
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

	}

	public class Map {

		private int id;

		public Map(int id) {
			this.id = id;
		}

		public String getMapHub() {
			String map_hub = "";
			try {
				PreparedStatement state = PlugNManager.getInstance().getConnection()
						.prepareStatement("SELECT map_hub FROM maps WHERE id = ?");
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
				PreparedStatement state = PlugNManager.getInstance().getConnection()
						.prepareStatement("SELECT map_game FROM maps WHERE id = ?");
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

	}

}
