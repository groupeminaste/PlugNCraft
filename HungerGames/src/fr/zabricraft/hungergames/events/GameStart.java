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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import fr.zabricraft.hungergames.HungerGames;
import fr.zabricraft.hungergames.utils.Kit;
import fr.zabricraft.hungergames.utils.ZabriPlayer;
import fr.zabricraft.plugnapi.utils.GameStartEvent;

public class GameStart implements Listener {

	@EventHandler
	public void onGameStart(GameStartEvent e) {
		Location l = Bukkit.getWorlds().get(0).getHighestBlockAt(0, 0).getLocation().add(0, 10, 0);
		for (Player p : Bukkit.getOnlinePlayers()) {
			ZabriPlayer zp = HungerGames.getInstance().getPlayer(p.getUniqueId());
			p.getInventory().clear();
			p.getInventory().addItem(new ItemStack(Material.COMPASS));
			Kit kit = zp.getKit();
			if (kit != null) {
				p.getInventory().addItem(kit.getItems());
			}
			p.updateInventory();
			p.teleport(l);
			zp.setPlaying(true);
		}
	}

}
