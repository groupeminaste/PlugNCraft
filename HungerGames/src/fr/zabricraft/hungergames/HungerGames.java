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

package fr.zabricraft.hungergames;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fr.zabricraft.hungergames.events.BlockBreak;
import fr.zabricraft.hungergames.events.EntityDamage;
import fr.zabricraft.hungergames.events.GameStart;
import fr.zabricraft.hungergames.events.InventoryClick;
import fr.zabricraft.hungergames.events.PlayerDeath;
import fr.zabricraft.hungergames.events.PlayerInteract;
import fr.zabricraft.hungergames.events.PlayerJoin;
import fr.zabricraft.hungergames.events.PlayerQuit;
import fr.zabricraft.hungergames.events.PlayerRespawn;
import fr.zabricraft.hungergames.utils.ZabriPlayer;
import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.utils.ServerStatus;

public class HungerGames extends JavaPlugin {

	private static HungerGames instance;

	public static HungerGames getInstance() {
		return instance;
	}

	private ArrayList<ZabriPlayer> players = new ArrayList<ZabriPlayer>();
	private int countdown;

	public ZabriPlayer getPlayer(UUID uuid) {
		for (ZabriPlayer current : players) {
			if (current.getUuid().equals(uuid)) {
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

	public int getCountdown() {
		return countdown;
	}

	public void onEnable() {
		instance = this;
		countdown = 61;

		Location spawn = Bukkit.getWorlds().get(0).getHighestBlockAt(0, 0).getLocation();
		Bukkit.getWorlds().get(0).setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

		for (Player p : Bukkit.getOnlinePlayers()) {
			initPlayer(p);
		}

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InventoryClick(), this);
		pm.registerEvents(new PlayerInteract(), this);
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		pm.registerEvents(new PlayerDeath(), this);
		pm.registerEvents(new PlayerRespawn(), this);
		pm.registerEvents(new EntityDamage(), this);
		pm.registerEvents(new GameStart(), this);
		pm.registerEvents(new BlockBreak(), this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if (PlugNAPI.getInstance().getStatus().equals(ServerStatus.PLAYING)) {
					if (countdown > 0) {
						countdown--;
						if (countdown == 0) {
							PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
							pc.getTitleActions().write(0, TitleAction.TITLE);
							pc.getChatComponents().write(0, WrappedChatComponent.fromText("§aPvP activé"));
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.sendMessage("§eLe PvP est activé, bonne chance !");
								try {
									ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						} else if (countdown == 60 || countdown == 30 || countdown == 20 || countdown == 10
								|| countdown <= 5) {
							PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
							pc.getTitleActions().write(0, TitleAction.TITLE);
							pc.getChatComponents().write(0,
									WrappedChatComponent.fromText("§aPvP activé dans " + countdown + "s"));
							for (Player p : Bukkit.getOnlinePlayers()) {
								p.sendMessage("§eLe PvP sera activé dans " + countdown + " secondes !");
								try {
									ProtocolLibrary.getProtocolManager().sendServerPacket(p, pc);
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}
							}
						}
					}
					verifNext();
				}
			}
		}, 0, 20);
	}

	public void onDisable() {
		players.clear();
	}

	public ArrayList<UUID> getPlayers() {
		ArrayList<UUID> result = new ArrayList<UUID>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			ZabriPlayer zp = HungerGames.getInstance().getPlayer(p.getUniqueId());
			if (zp.isPlaying()) {
				result.add(p.getUniqueId());
			}
		}
		return result;
	}

	public void verifNext() {
		int number = getPlayers().size();
		if (number == 0) {
			PlugNAPI.getInstance().stop(null);
		} else if (number == 1) {
			PlugNAPI.getInstance().stop(Bukkit.getPlayer(getPlayers().get(0)));
		}
	}

}
