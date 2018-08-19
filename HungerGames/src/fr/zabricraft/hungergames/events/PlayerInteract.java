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

package fr.zabricraft.hungergames.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.zabricraft.hungergames.HungerGames;
import fr.zabricraft.hungergames.utils.InventoryManager;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				|| e.getAction().equals(Action.RIGHT_CLICK_AIR) && e.getPlayer().getItemInHand() != null) {
			if (e.getPlayer().getItemInHand().getType().equals(Material.CHEST)
					&& e.getPlayer().getItemInHand().hasItemMeta()
					&& e.getPlayer().getItemInHand().getItemMeta().hasDisplayName()
					&& e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§rKits")) {
				InventoryManager.openKits(e.getPlayer());
			} else if (e.getPlayer().getItemInHand().getType().equals(Material.COMPASS)) {
				String name = "";
				double blocks = -1;
				for (UUID uuid : HungerGames.getInstance().getPlayers()) {
					Player p = Bukkit.getPlayer(uuid);
					if (!p.equals(e.getPlayer())) {
						if (p.getLocation().distance(e.getPlayer().getLocation()) < blocks || blocks == -1) {
							name = p.getName();
							blocks = p.getLocation().distance(e.getPlayer().getLocation());
						}
					}
				}
				e.getPlayer().sendMessage("§7Le joueur le plus proche est §e" + name + "§7, qui se trouve a §e" + ((int) blocks) + " blocks §7de vous.");
			}
		}
	}

}
