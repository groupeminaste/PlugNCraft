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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import fr.zabricraft.hungergames.HungerGames;
import fr.zabricraft.hungergames.utils.ZabriPlayer;

public class PlayerDeath implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		ZabriPlayer zp = HungerGames.getInstance().getPlayer(e.getEntity().getUniqueId());
		if (zp != null) {
			if (e.getEntity().getKiller() != null) {
				e.setDeathMessage("§e" + e.getEntity().getName() + " §7a été tué par §e"
						+ e.getEntity().getKiller().getName() + "§7.");
			} else {
				e.setDeathMessage("§e" + e.getEntity().getName() + " §7est mort.");
			}
			zp.setPlaying(false);
		}
	}

}
