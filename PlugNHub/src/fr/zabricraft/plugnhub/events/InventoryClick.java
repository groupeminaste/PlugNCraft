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

package fr.zabricraft.plugnhub.events;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.zabricraft.craftsearch.CraftSearch;
import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.utils.ServerStatus;
import fr.zabricraft.plugnapi.utils.ZabriPlayer;
import fr.zabricraft.plugnhub.utils.InventoryManager;

public class InventoryClick implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		e.setCancelled(true);
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			ZabriPlayer zp = PlugNAPI.getInstance().getPlayer(p.getUniqueId());
			if (zp != null) {
				if (e.getInventory().getName().equals("§r§rNos jeux")) {
					if (e.getSlot() == 31) {
						p.closeInventory();
					} else if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()
							&& e.getCurrentItem().getItemMeta().hasLore()) {
						List<String> lore = e.getCurrentItem().getItemMeta().getLore();
						String id = lore.get(lore.size() - 1).replaceFirst("§9ID: ", "");
						InventoryManager.openGames(p, id);
					}
				} else if (e.getInventory().getName().startsWith("§r§rParties de")) {
					if (e.getSlot() == 30) {
						List<String> lore = e.getCurrentItem().getItemMeta().getLore();
						String id = lore.get(lore.size() - 1).replaceFirst("§9ID: ", "");
						InventoryManager.openStart(p, id);
					} else if (e.getSlot() == 32) {
						p.closeInventory();
					} else if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()
							&& e.getCurrentItem().getItemMeta().hasLore()) {
						List<String> lore = e.getCurrentItem().getItemMeta().getLore();
						String id = lore.get(lore.size() - 1).replaceFirst("§9ID: ", "");
						try {
							PreparedStatement state = PlugNAPI.getInstance().getConnection()
									.prepareStatement("SELECT * FROM servers WHERE id = ?");
							state.setString(1, id);
							ResultSet result = state.executeQuery();
							if (result.next()) {
								ServerStatus status = ServerStatus.valueOf(result.getString("status"));
								if (status.equals(ServerStatus.OFFLINE)) {
									p.sendMessage("§cCe serveur est hors ligne !");
								} else if (status.equals(ServerStatus.PLAYING)) {
									p.sendMessage("§cCette partie est déjà en cours !");
								} else if (status.equals(ServerStatus.FINISHED)) {
									p.sendMessage("§cCette partie est déjà terminée !");
								} else if (status.equals(ServerStatus.WAITING)
										|| status.equals(ServerStatus.COUNTDOWN)) {
									CraftSearch.getInstance().connectSwitcher(p, "plugncraft" + (result.getInt("port")-25999));
								} else {
									p.sendMessage("§cLe statut de la partie est inconnu !");
								}
							} else {
								p.sendMessage("§cImpossible de trouver ce serveur !");
							}
							result.close();
							state.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				} else if (e.getInventory().getName().startsWith("§r§rDémarrer un serveur ")) {
					if (e.getSlot() == 31) {
						p.closeInventory();
					} else if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()
							&& e.getCurrentItem().getItemMeta().hasLore()) {
						List<String> lore = e.getCurrentItem().getItemMeta().getLore();
						int id = Integer.parseInt(lore.get(lore.size() - 1).replaceFirst("§9ID: ", ""));
						try {
							PreparedStatement state = PlugNAPI.getInstance().getConnection()
									.prepareStatement("SELECT * FROM maps WHERE id = ?");
							state.setInt(1, id);
							ResultSet result = state.executeQuery();
							if (result.next()) {
								String game = result.getString("game");
								PreparedStatement state2 = PlugNAPI.getInstance().getConnection().prepareStatement(
										"SELECT * FROM servers WHERE type = ? AND map = ? AND (status = ? OR status = ? OR status = ?)");
								state2.setString(1, game);
								state2.setInt(2, id);
								state2.setString(3, ServerStatus.WAITING.toString());
								state2.setString(4, ServerStatus.COUNTDOWN.toString());
								state2.setString(5, ServerStatus.STARTING.toString());
								ResultSet result2 = state2.executeQuery();
								if (!result2.next()) {
									PreparedStatement state3 = PlugNAPI.getInstance().getConnection()
											.prepareStatement("SELECT * FROM start WHERE game = ? AND map = ?");
									state3.setString(1, game);
									state3.setInt(2, id);
									ResultSet result3 = state3.executeQuery();
									if (!result3.next()) {
										PreparedStatement state4 = PlugNAPI.getInstance().getConnection()
												.prepareStatement("INSERT INTO start (game, map) VALUES(?, ?)");
										state4.setString(1, game);
										state4.setInt(2, id);
										state4.executeUpdate();
										state4.close();
										p.sendMessage("§aDémarrage en cours d'un serveur avec la map §e"
												+ result.getString("name") + "§a...");
									} else {
										p.sendMessage("§cUn serveur avec cette map est déjà en train de démarrer !");
									}
									result3.close();
									state3.close();
								} else {
									p.sendMessage("§cUn serveur avec cette map est déjà prêt à vous accueillir !");
								}
								result2.close();
								state2.close();
							}
							result.close();
							state.close();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}
	}

}
