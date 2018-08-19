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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.zabricraft.plugnapi.utils.GameStartEvent;
import fr.zabricraft.plugnreplica.PlugNReplica;
import fr.zabricraft.plugnreplica.utils.ZabriPlayer;

public class GameStart implements Listener {

	@EventHandler
	public void onGameStart(GameStartEvent e) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			ZabriPlayer zp = PlugNReplica.getInstance().getPlayer(p.getUniqueId());
			zp.setPlaying(true);
		}
		PlugNReplica.getInstance().loadDraw();
	}

}
