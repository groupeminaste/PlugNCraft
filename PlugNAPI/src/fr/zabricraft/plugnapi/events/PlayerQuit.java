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

package fr.zabricraft.plugnapi.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.PlugNAPI.Template;

public class PlayerQuit implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlugNAPI.getInstance().uninitPlayer(PlugNAPI.getInstance().getPlayer(e.getPlayer().getUniqueId()));
		Template template = PlugNAPI.getInstance().new Template(PlugNAPI.getInstance().getTemplate());
		e.setQuitMessage("§e" + e.getPlayer().getName() + " §7s'est déconnecté du §e" + template.getName()
				+ " de PlugNCraft §7!");
		PlugNAPI.getInstance().setPlayers(Bukkit.getOnlinePlayers().size() - 1);
	}

}
