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

package fr.zabricraft.plugnreplica;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.utils.ServerStatus;
import fr.zabricraft.plugnreplica.events.BlockBreak;
import fr.zabricraft.plugnreplica.events.BlockPlace;
import fr.zabricraft.plugnreplica.events.EntityDamage;
import fr.zabricraft.plugnreplica.events.GameStart;
import fr.zabricraft.plugnreplica.events.PlayerJoin;
import fr.zabricraft.plugnreplica.events.PlayerQuit;
import fr.zabricraft.plugnreplica.events.PlayerRespawn;
import fr.zabricraft.plugnreplica.utils.Picture;
import fr.zabricraft.plugnreplica.utils.ReplicaGenerator;
import fr.zabricraft.plugnreplica.utils.ZabriPlayer;

public class PlugNReplica extends JavaPlugin {

	private static PlugNReplica instance;

	public static PlugNReplica getInstance() {
		return instance;
	}

	private ArrayList<ZabriPlayer> players = new ArrayList<ZabriPlayer>();
	private ArrayList<Picture> pictures = new ArrayList<Picture>();

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

	public Picture getRandomPicture() {
		Random r = new Random();
		return pictures.get(r.nextInt(pictures.size()));
	}

	public void onEnable() {
		instance = this;

		WorldCreator w = new WorldCreator("game");
		w.type(WorldType.FLAT);
		w.generator(new ReplicaGenerator());
		w.createWorld();
		Bukkit.getWorld("game").setDifficulty(Difficulty.PEACEFUL);
		Bukkit.getWorld("game").setSpawnLocation(-1000, 0, 0);
		Bukkit.getWorld("game").setGameRuleValue("doDaylightCycle", "false");
		Bukkit.getWorld("game").setTime(0);

		for (Player p : Bukkit.getOnlinePlayers()) {
			initPlayer(p);
		}

		loadPlots();

		pictures.clear();
		ConfigurationSection pf = getConfig().getConfigurationSection("pictures");
		if (pf != null) {
			for (String s : pf.getKeys(false)) {
				Picture p = new Picture(pf.getString(s + ".name"));
				String[] blocks = pf.getString(s + ".blocks").split(";");
				for (int x = 0; x < 8; x++) {
					for (int y = 0; y < 8; y++) {
						p.setBlock(Integer.parseInt(blocks[y * 8 + x]), x, y);
					}
				}
				pictures.add(p);
			}
		}
		if (pictures.size() < 1) {
			getLogger().severe("You have to add one picture or more to use this plugin !");
			getLogger().severe("Vous devez au moins ajoutez une image pour faire fonctionner le plugin !");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoin(), this);
		pm.registerEvents(new PlayerQuit(), this);
		pm.registerEvents(new PlayerRespawn(), this);
		pm.registerEvents(new EntityDamage(), this);
		pm.registerEvents(new BlockPlace(), this);
		pm.registerEvents(new BlockBreak(), this);
		pm.registerEvents(new GameStart(), this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				if (PlugNAPI.getInstance().getStatus().equals(ServerStatus.PLAYING)) {
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
			ZabriPlayer zp = PlugNReplica.getInstance().getPlayer(p.getUniqueId());
			if (zp.isPlaying()) {
				result.add(p.getUniqueId());
			}
		}
		return result;
	}

	public ItemStack makeClay(int color) {
		return new ItemStack(Material.STAINED_CLAY, 64, (byte) color);
	}

	public void loadPlots() {
		for (int i = 0; i < 20; i++) {
			for (int x = 5; x < 13; x++) {
				for (int z = 5; z < 13; z++) {
					new Location(Bukkit.getWorld("game"), x, 64, z + i * 32).getBlock().setType(Material.AIR);
				}
			}
			for (int y = 0; y < 8; y++) {
				for (int z = 5; z < 13; z++) {
					new Location(Bukkit.getWorld("game"), 14, 66 + y, z + i * 32).getBlock().setType(Material.AIR);
				}
			}
		}
	}

	public void breakPlot(int col) {
		col--;
		for (int x = 5; x < 13; x++) {
			for (int z = 5; z < 13; z++) {
				new Location(Bukkit.getWorld("game"), x, 64, z + col * 32).getBlock().setType(Material.AIR);
			}
		}
	}

	public void drawPlot(int col, Picture p) {
		col--;
		for (int y = 0; y < 8; y++) {
			for (int z = 5; z < 13; z++) {
				Block b = new Location(Bukkit.getWorld("game"), 14, 66 + (7 - y), z + col * 32).getBlock();
				b.setType(Material.STAINED_CLAY);
				b.setData((byte) p.getBlock(z - 5, y));
			}
		}
	}

	public boolean isCompletingPlot(int col) {
		col--;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location b = new Location(Bukkit.getWorld("game"), 14, 66 + (7 - y), (7 - x) + col * 32 + 5);
				Location b2 = new Location(Bukkit.getWorld("game"), 5 + (7 - y), 64, (7 - x) + col * 32 + 5);
				if (!b2.getBlock().getType().equals(Material.STAINED_CLAY)) {
					return false;
				}
				if (b.getBlock().getData() != b2.getBlock().getData()) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean containsColor(int col, int color) {
		col--;
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location b = new Location(Bukkit.getWorld("game"), 14, 66 + (7 - y), (7 - x) + col * 32 + 5);
				if (b.getBlock().getData() == (byte) color) {
					return true;
				}
			}
		}
		return false;
	}

	public void loadDraw() {
		Picture p = PlugNReplica.getInstance().getRandomPicture();
		loadPlots();
		draw(p, getPlayers().size());
		int plot = 1;
		PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
		pc.getTitleActions().write(0, TitleAction.TITLE);
		pc.getChatComponents().write(0, WrappedChatComponent.fromText("§6" + p.getName()));
		for (UUID uuid : getPlayers()) {
			Player player = Bukkit.getPlayer(uuid);
			ZabriPlayer zp = PlugNReplica.getInstance().getPlayer(uuid);
			Location l = new Location(Bukkit.getWorld("game"), 4, 65, (plot - 1) * 32 + 9);
			l.setYaw(-90);
			player.teleport(l);
			player.setGameMode(GameMode.SURVIVAL);
			zp.setPlaying(true);
			zp.setPlot(plot);
			zp.setFinish(false);
			player.getInventory().clear();
			player.getInventory().addItem(new ItemStack(Material.IRON_PICKAXE));
			for (int i = 0; i < 16; i++) {
				if (containsColor(plot, i)) {
					player.getInventory().addItem(makeClay(i));
				}
			}
			player.updateInventory();
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, pc);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			plot++;
		}
	}

	public void verifNext() {
		int number = getPlayers().size();
		int current = 0;
		UUID no = null;
		for (UUID uuid : getPlayers()) {
			ZabriPlayer zp = PlugNReplica.getInstance().getPlayer(uuid);
			if (zp.isFinish()) {
				current++;
			} else {
				no = uuid;
			}
		}
		if (number == 0) {
			PlugNAPI.getInstance().stop(null);
		} else if(number == 1){
			PlugNAPI.getInstance().stop(Bukkit.getPlayer(getPlayers().get(0)));
		}else if (current >= number - 1) {
			if (no != null) {
				Player nop = Bukkit.getPlayer(no);
				ZabriPlayer zp = PlugNReplica.getInstance().getPlayer(no);
				zp.setPlaying(false);
				zp.setFinish(false);
				zp.setPlot(0);
				Bukkit.broadcastMessage("§e" + nop.getName() + " §7a été éliminé !");
				nop.getInventory().clear();
				nop.updateInventory();
				nop.setGameMode(GameMode.SPECTATOR);
				PacketContainer pc = new PacketContainer(PacketType.Play.Server.TITLE);
				pc.getTitleActions().write(0, TitleAction.TITLE);
				pc.getChatComponents().write(0, WrappedChatComponent.fromText("§cVous avez été éliminé !"));
				try {
					ProtocolLibrary.getProtocolManager().sendServerPacket(nop, pc);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			if (number == 2) {
				PlugNAPI.getInstance().stop(Bukkit.getPlayer(getPlayers().get(0)));
			} else {
				loadDraw();
			}
		}
	}

	public void draw(Picture p, int limit) {
		for (int i = 1; i <= limit; i++) {
			drawPlot(i, p);
		}
	}

}
