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

package fr.zabricraft.plugnmanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

import fr.zabricraft.plugnmanager.discord.DiscordListener;
import fr.zabricraft.plugnmanager.discord.DiscordStepper;
import fr.zabricraft.plugnmanager.minecraft.MinecraftServer;
import fr.zabricraft.plugnmanager.minecraft.ServerStatus;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

public class PlugNManager {

	private static PlugNManager instance;

	public static PlugNManager getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		new PlugNManager();
	}

	private boolean running;
	private Connection connection;
	private IDiscordClient discord;
	private ArrayList<MinecraftServer> minecraft;
	private ArrayList<DiscordStepper> stepper;

	public ArrayList<MinecraftServer> getMinecraft() {
		return minecraft;
	}

	public ArrayList<DiscordStepper> getDiscordStepper() {
		return stepper;
	}

	public PlugNManager() {
		System.out.println("Starting PlugNManager server...");
		long start = System.currentTimeMillis();
		instance = this;
		running = true;
		if (initDatabase()) {
			initConfigurations();
			long done = System.currentTimeMillis();
			System.out.println("Done ! (" + (done - start) + "ms)");
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (isRunning()) {
						if (!serverStarted("hub1")) {
							startServer("hub", 1);
						}
						try {
							Statement state = getConnection().createStatement();
							ResultSet result = state.executeQuery("SELECT * FROM start");
							while (result.next()) {
								startServer(result.getString("game"), result.getInt("map"));
							}
							result.close();
							state.close();
							Statement state3 = getConnection().createStatement();
							state3.executeUpdate("TRUNCATE start");
							state3.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							Thread.sleep(5000L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			new Thread(new Runnable() {
				@Override
				public void run() {
					Scanner sc = new Scanner(System.in);
					while (isRunning()) {
						String s = sc.nextLine();
						String[] args = s.trim().split(" ");
						if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
							stop();
						} else if (args.length == 3 && args[0].equalsIgnoreCase("start")) {
							startServer(args[1], Integer.parseInt(args[2]));
						} else if (args.length > 2 && args[0].equalsIgnoreCase("exec")) {
							MinecraftServer server = getMinecraftServer(args[1]);
							if (server != null) {
								String cmd = "";
								for (int i = 2; i < args.length; i++) {
									cmd += args[i] + " ";
								}
								server.sendCommand(cmd.trim());
							}
						}
					}
					sc.close();
				}
			}).start();
		}
	}

	public void clearDirectory(File directory) {
		if (directory.isDirectory()) {
			for (File f : directory.listFiles()) {
				if (f.isDirectory()) {
					clearDirectory(f);
				}
				f.delete();
			}
		}
	}

	public void cloneDirectory(File from, File to) {
		if (!to.exists()) {
			to.mkdirs();
		}
		if (from.isDirectory()) {
			for (File f : from.listFiles()) {
				try {
					File to2 = new File(to, f.getName());
					if (f.isDirectory()) {
						cloneDirectory(f, to2);
					} else {
						Files.copy(f.toPath(), to2.toPath());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void initConfigurations() {
		try {
			System.out.println("Loading configurations...");
			File servers = new File("servers");
			if (!servers.exists()) {
				servers.mkdirs();
			}
			clearDirectory(servers);
			File templates = new File("templates");
			if (!templates.exists()) {
				templates.mkdirs();
			}
			File libs = new File("libs");
			if (!libs.exists()) {
				libs.mkdirs();
			}
			this.minecraft = new ArrayList<MinecraftServer>();
			this.stepper = new ArrayList<DiscordStepper>();
			getDiscord().getDispatcher().registerListener(new DiscordListener());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean initDatabase() {
		if (getConnection() != null) {
			try {
				Statement state = getConnection().createStatement();
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `players` (`uuid` varchar(255) NOT NULL, `name` varchar(255) NOT NULL, `grade` varchar(255) NOT NULL, `first_login` datetime NOT NULL, PRIMARY KEY (`uuid`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `servers` (`id` varchar(255) NOT NULL, `type` varchar(255) NOT NULL, `port` int(11) NOT NULL, `status` varchar(255) NOT NULL, `map` int(11) NOT NULL, `players` int(11) NOT NULL, PRIMARY KEY (`id`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `games` (`id` varchar(255) NOT NULL, `name` varchar(255) NOT NULL, `description` text NOT NULL, `min_players` int(11) NOT NULL, `max_players` int(11) NOT NULL, `slot` int(11) NOT NULL, `icon` varchar(255) NOT NULL, PRIMARY KEY (`id`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `maps` (`id` int(11) NOT NULL AUTO_INCREMENT, `game` varchar(255) NOT NULL, `name` varchar(255) NOT NULL, `description` text NOT NULL, `map_hub` varchar(255) NOT NULL, `map_game` varchar(255) NOT NULL, `icon` varchar(255) NOT NULL, PRIMARY KEY (`id`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `start` (`id` int(11) NOT NULL AUTO_INCREMENT, `game` varchar(255) NOT NULL, `map` int(11) NOT NULL, PRIMARY KEY (`id`))");
				state.executeUpdate(
						"CREATE TABLE IF NOT EXISTS `ai_answers` (`id` int(11) NOT NULL AUTO_INCREMENT, `keywords` text NOT NULL, `answer` text NOT NULL, PRIMARY KEY (`id`))");
				state.executeUpdate("UPDATE servers SET status = 'OFFLINE'");
				state.close();
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void stop() {
		System.out.println("Stopping PlugNManager server...");
		running = false;
		stopServers();
		getDiscord().logout();
		System.out.println("Bye !");
	}

	public MinecraftServer getMinecraftServer(String name) {
		for (MinecraftServer s : minecraft) {
			if (s.getName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}

	public void startServer(String template, int map) {
		try {
			PreparedStatement state3 = getConnection().prepareStatement(
					"SELECT * FROM servers WHERE type = ? AND map = ? AND (status = ? OR status = ? OR status = ?)");
			state3.setString(1, template);
			state3.setInt(2, map);
			state3.setString(3, ServerStatus.WAITING.toString());
			state3.setString(4, ServerStatus.COUNTDOWN.toString());
			state3.setString(5, ServerStatus.STARTING.toString());
			ResultSet result3 = state3.executeQuery();
			if (!result3.next()) {
				if (minecraft.size() < 12) {
					boolean c = true;
					for (int i = 1; c; i++) {
						if (!serverStarted(template + i)) {
							PreparedStatement state = PlugNManager.getInstance().getConnection()
									.prepareStatement("SELECT 1 FROM servers WHERE id = ?");
							state.setString(1, template + i);
							ResultSet result = state.executeQuery();
							if (result.next()) {
								PreparedStatement state2 = PlugNManager.getInstance().getConnection()
										.prepareStatement("UPDATE servers SET map = ?, status = ? WHERE id = ?");
								state2.setInt(1, map);
								state2.setString(2, ServerStatus.STARTING.toString());
								state2.setString(3, template + i);
								state2.executeUpdate();
								state2.close();
							} else {
								PreparedStatement state2 = PlugNManager.getInstance().getConnection().prepareStatement(
										"INSERT INTO servers (id, type, port, status, map, players) VALUES(?, ?, ?, ?, ?, ?)");
								state2.setString(1, template + i);
								state2.setString(2, template);
								state2.setInt(3, getFreePort());
								state2.setString(4, ServerStatus.STARTING.toString());
								state2.setInt(5, map);
								state2.setInt(6, 0);
								state2.executeUpdate();
								state2.close();
							}
							result.close();
							state.close();
							minecraft.add(new MinecraftServer(template + i));
							c = false;
						}
					}
				}
			}
			result3.close();
			state3.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopServers() {
		for (MinecraftServer mc : minecraft) {
			mc.sendCommand("stop");
		}
	}

	public void uninitServer(MinecraftServer server) {
		if (minecraft.contains(server)) {
			minecraft.remove(server);
			File folder = new File(new File("servers"), server.getName());
			clearDirectory(folder);
			folder.delete();
		}
	}

	private boolean serverStarted(String name) {
		for (MinecraftServer mc : minecraft) {
			if (mc.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private int getFreePort() {
		int i;
		for (i = 26000; !isFreePort(i); i++) {
		}
		return i;
	}

	private boolean isFreePort(int i) {
		boolean free = true;
		try {
			PreparedStatement state = getConnection().prepareStatement("SELECT port FROM servers WHERE port = ?");
			state.setInt(1, i);
			ResultSet result = state.executeQuery();
			free = !result.next();
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return free;
	}

	public DiscordStepper getStepper(long user, long channel) {
		for (DiscordStepper s : stepper) {
			if (s.isFor(user, channel)) {
				return s;
			}
		}
		return null;
	}

	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver");
				
				// Edit here the credentials for your MySQL database
				
				connection = DriverManager.getConnection("jdbc:mysql://192.168.0.5:3306/plugncraft", "root", "");
			}
		} catch (SQLException | ClassNotFoundException e) {
			System.out.println("Un probleme est survenue lors de la connexion au serveur MySQL, arret du serveur...");
			stop();
			return null;
		}
		return connection;
	}

	public IDiscordClient getDiscord() {
		try {
			if (discord == null) {
				ClientBuilder clientBuilder = new ClientBuilder();

				// Fill the string with the token of your discord bot - see https://discordapp.com/developers/applications/
				
				clientBuilder.withToken("");
				discord = clientBuilder.login();
			}
		} catch (Exception e) {
			System.out.println("Un probleme est survenue lors de la connexion au service Discord, arret du serveur...");
			stop();
			return null;
		}
		return discord;
	}

	public String getCraftSearchAIAnswer(String request) {
		String answer = "";
		request = request.toLowerCase();
		if (request.startsWith("dis plugncraft")) {
			String[] removes = { "dis plugncraft", "\\?", "!", "¿", "¡", ",", ".", "-" };
			for (String remove : removes) {
				if (request.contains(remove)) {
					request = request.replaceAll(remove, " ");
				}
			}
			request = request.trim();
			if (request.isEmpty()) {
				request = "show help";
			}
			try {
				request = request.replaceAll(" ", "%");
				String query = "SELECT * FROM ai_answers WHERE (keywords LIKE ? ";
				ArrayList<String> param = new ArrayList<String>();
				param.add("%" + request + "%");
				String keys[] = request.split("%");
				for (String key : keys) {
					query += "OR keywords LIKE ? ";
					param.add("%" + key + "%");
				}
				query += ") ORDER BY ";
				for (String key : keys) {
					query += "(CASE WHEN keywords LIKE ? THEN " + (key.equals("broadcast") ? 99999 : 5 + key.length())
							+ " ELSE 0 END) + ";
					param.add("%" + key + "%");
				}
				query += "0 DESC LIMIT 1";
				PreparedStatement state = getConnection().prepareStatement(query);
				for (int i = 0; i < param.size(); i++) {
					state.setString(i + 1, param.get(i));
				}
				ResultSet result = state.executeQuery();
				if (result.next()) {
					answer = result.getString("answer");
				} else {
					answer = "Je ne trouve rien correspondant à votre requête... Essayez `Dis PlugNCraft, affiche l'aide`";
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
				answer = "Oups! Une erreur est survenue.";
			}
			return answer;
		}
		return answer;
	}

}
