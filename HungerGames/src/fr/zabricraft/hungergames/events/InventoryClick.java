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

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.zabricraft.hungergames.HungerGames;
import fr.zabricraft.hungergames.utils.Kit;
import fr.zabricraft.hungergames.utils.ZabriPlayer;

public class InventoryClick implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			ZabriPlayer zp = HungerGames.getInstance().getPlayer(p.getUniqueId());
			if (zp != null) {
				if (e.getInventory().getName().equals("§r§rKits")) {
					e.setCancelled(true);
					if (e.getSlot() == 31) {
						p.closeInventory();
					} else if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
						for (Kit kit : Kit.values()) {
							if (kit.getSlot() == e.getSlot()) {
								zp.setKit(kit);
								p.sendMessage("§7Vous avez sélectionné le §eKit " + kit.getName() + "§7.");
							}
						}
					}
				}
			}
		}
	}

}
