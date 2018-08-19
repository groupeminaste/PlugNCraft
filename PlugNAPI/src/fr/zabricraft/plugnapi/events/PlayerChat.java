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

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.zabricraft.plugnapi.PlugNAPI;
import fr.zabricraft.plugnapi.utils.Grade;
import fr.zabricraft.plugnapi.utils.ZabriPlayer;

public class PlayerChat implements Listener {

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		ZabriPlayer sender = PlugNAPI.getInstance().getPlayer(e.getPlayer().getUniqueId());
		if (sender != null) {
			Grade grade = sender.getGrade();
			if (grade.isStaff()) {
				e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
				e.setFormat("§b[§3" + grade.getName() + "§b] §b%s §3>> §r%s");
			} else {
				e.setFormat("§e[§6" + grade.getName() + "§e] §e%s §6>> §r%s");
			}
		}
	}

}
