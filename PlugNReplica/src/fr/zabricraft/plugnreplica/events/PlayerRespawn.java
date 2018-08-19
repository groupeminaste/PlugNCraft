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

package fr.zabricraft.plugnreplica.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.utils.ServerStatus;
import fr.zabricraft.plugnreplica.PlugNReplica;
import fr.zabricraft.plugnreplica.utils.ZabriPlayer;

public class PlayerRespawn implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		ZabriPlayer zp = PlugNReplica.getInstance().getPlayer(e.getPlayer().getUniqueId());
		if (zp.isPlaying() && PlugNAPI.getInstance().getStatus().equals(ServerStatus.PLAYING)) {
			Location l = new Location(Bukkit.getWorld("game"), 4, 65, (zp.getPlot() - 1) * 32 + 9);
			l.setYaw(-90);
			e.setRespawnLocation(l);
		}
	}

}
