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

package fr.zabricraft.plugnhub.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.utils.Items;
import fr.zabricraft.plugnapi.utils.ServerStatus;

public class InventoryManager {

	public static void openMenu(Player player) {
		Inventory i = Bukkit.createInventory(null, 36, "§r§rNos jeux");
		try {
			Statement state = PlugNAPI.getInstance().getConnection().createStatement();
			ResultSet result = state.executeQuery("SELECT * FROM games");
			while (result.next()) {
				int slot = result.getInt("slot");
				if (slot >= 0) {
					ItemStack si = makeGameItem(result);
					if (si != null) {
						i.setItem(slot, si);
					} else {
						i.setItem(slot, Items.setName(new ItemStack(Material.WOOL), "§cError with this game !"));
					}
				}
			}
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		i.setItem(31, Items.setName(new ItemStack(Material.WOOL), "§aFermer"));
		player.openInventory(i);
	}

	public static void openGames(Player player, String id) {
		try {
			PreparedStatement state = PlugNAPI.getInstance().getConnection()
					.prepareStatement("SELECT * FROM games WHERE id = ?");
			state.setString(1, id);
			ResultSet result = state.executeQuery();
			if (result.next()) {
				Inventory i = Bukkit.createInventory(null, 36, "§r§rParties de " + result.getString("name"));
				PreparedStatement state2 = PlugNAPI.getInstance().getConnection()
						.prepareStatement("SELECT * FROM servers WHERE type = ?");
				state2.setString(1, result.getString("id"));
				ResultSet result2 = state2.executeQuery();
				int j = 0;
				while (result2.next()) {
					ItemStack si = makeServItem(result, result2);
					if (si != null) {
						i.setItem(convertSlot(j), si);
					} else {
						i.setItem(convertSlot(j),
								Items.setName(new ItemStack(Material.WOOL), "§cError with this server !"));
					}
					j++;
				}
				result2.close();
				state2.close();
				i.setItem(30, Items.setLore(Items.setName(new ItemStack(Material.WOOL), "§aCréer une partie"), "",
						"§9ID: " + result.getString("id")));
				i.setItem(32, Items.setName(new ItemStack(Material.WOOL), "§aFermer"));
				player.openInventory(i);
			}
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void openStart(Player player, String id) {
		try {
			PreparedStatement state = PlugNAPI.getInstance().getConnection()
					.prepareStatement("SELECT * FROM games WHERE id = ?");
			state.setString(1, id);
			ResultSet result = state.executeQuery();
			if (result.next()) {
				Inventory i = Bukkit.createInventory(null, 36, "§r§rDémarrer un serveur " + result.getString("name"));
				PreparedStatement state2 = PlugNAPI.getInstance().getConnection()
						.prepareStatement("SELECT * FROM maps WHERE game = ?");
				state2.setString(1, result.getString("id"));
				ResultSet result2 = state2.executeQuery();
				int j = 0;
				while (result2.next()) {
					ItemStack si = makeStartItem(result, result2);
					if (si != null) {
						i.setItem(convertSlot(j), si);
					} else {
						i.setItem(convertSlot(j),
								Items.setName(new ItemStack(Material.WOOL), "§cError with this map !"));
					}
					j++;
				}
				result2.close();
				state2.close();
				i.setItem(31, Items.setName(new ItemStack(Material.WOOL), "§aFermer"));
				player.openInventory(i);
			}
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static int convertSlot(int i) {
		return (((i / 7) + 1) * 9) + (i % 7) + 1;
	}

	public static ItemStack makeGameItem(ResultSet game) {
		try {
			ItemStack result = Items.setName(new ItemStack(Material.valueOf(game.getString("icon"))), "§6" + game.getString("name"));
			ArrayList<String> lore = new ArrayList<String>();
			String current = "";
			for (char c : game.getString("description").toCharArray()) {
				if ((current.length() > 40 && c == ' ') || c == '\n') {
					lore.add("§a" + current);
					current = "";
				} else {
					current += c;
				}
			}
			if (!current.isEmpty()) {
				lore.add("§a" + current);
			}
			PreparedStatement state = PlugNAPI.getInstance().getConnection()
					.prepareStatement("SELECT SUM(players) as total FROM servers WHERE id = ?");
			state.setString(1, game.getString("id"));
			ResultSet players = state.executeQuery();
			if (players.next()) {
				lore.add("");
				lore.add("§b" + players.getInt("total") + " joueur" + (players.getInt("total") > 1 ? "s" : "")
						+ " en jeu");
			}
			players.close();
			state.close();
			lore.add("");
			lore.add("§9ID: " + game.getString("id"));
			result = Items.setLore(result, lore);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ItemStack makeServItem(ResultSet game, ResultSet serv) {
		try {
			ServerStatus status = ServerStatus.valueOf(serv.getString("status"));
			int color = status.equals(ServerStatus.OFFLINE) ? 15 : 5;
			ItemStack result = Items.setName(Items.createItem(Material.WOOL, color), "§6" + game.getString("name"));
			ArrayList<String> lore = new ArrayList<String>();
			if (!status.equals(ServerStatus.OFFLINE)) {
				PreparedStatement state = PlugNAPI.getInstance().getConnection()
						.prepareStatement("SELECT * FROM maps WHERE id = ?");
				state.setInt(1, serv.getInt("map"));
				ResultSet map = state.executeQuery();
				if (map.next()) {
					lore.add("§dMap : " + map.getString("name"));
					lore.add("");
					String current = "";
					for (char c : map.getString("description").toCharArray()) {
						if ((current.length() > 40 && c == ' ') || c == '\n') {
							lore.add("§a" + current);
							current = "";
						} else {
							current += c;
						}
					}
					if (!current.isEmpty()) {
						lore.add("§a" + current);
					}
				}
				map.close();
				state.close();
			}
			lore.add("");
			lore.add("§7" + status.getText().replaceAll("%s", "quelques"));
			lore.add("");
			lore.add("§b" + serv.getInt("players") + " joueur" + (serv.getInt("players") > 1 ? "s" : "") + " en jeu");
			lore.add("");
			lore.add("§9ID: " + serv.getString("id"));
			result = Items.setLore(result, lore);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ItemStack makeStartItem(ResultSet game, ResultSet map) {
		try {
			ItemStack result = Items.setName(new ItemStack(Material.valueOf(map.getString("icon"))), "§6" + map.getString("name"));
			ArrayList<String> lore = new ArrayList<String>();
			String current = "";
			for (char c : map.getString("description").toCharArray()) {
				if ((current.length() > 40 && c == ' ') || c == '\n') {
					lore.add("§a" + current);
					current = "";
				} else {
					current += c;
				}
			}
			if (!current.isEmpty()) {
				lore.add("§a" + current);
			}
			lore.add("");
			lore.add("§9ID: " + map.getInt("id"));
			result = Items.setLore(result, lore);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
