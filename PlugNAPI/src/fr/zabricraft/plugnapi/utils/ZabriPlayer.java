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

package fr.zabricraft.plugnapi.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

import fr.zabricraft.plugnapi.PlugNAPI;

public class ZabriPlayer {

	private UUID uuid;
	private PlayerScoreboard sb;

	public ZabriPlayer(Player p) {
		this.uuid = p.getUniqueId();
		sb = new PlayerScoreboard("PlugNCraft");
		try {
			PreparedStatement state = PlugNAPI.getInstance().getConnection()
					.prepareStatement("SELECT * FROM players WHERE uuid = ?");
			state.setString(1, uuid.toString());
			ResultSet result = state.executeQuery();
			if (result.next()) {
				PreparedStatement state2 = PlugNAPI.getInstance().getConnection()
						.prepareStatement("UPDATE players SET name = ? WHERE uuid = ?");
				state2.setString(1, p.getName());
				state2.setString(2, uuid.toString());
				state2.executeUpdate();
				state2.close();
			} else {
				PreparedStatement state2 = PlugNAPI.getInstance().getConnection().prepareStatement(
						"INSERT INTO players (uuid, name, grade, first_login) VALUES(?, ?, ?, NOW())");
				state2.setString(1, uuid.toString());
				state2.setString(2, p.getName());
				state2.setString(3, Grade.JOUEUR.toString());
				state2.executeUpdate();
				state2.close();
			}
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public UUID getUUID() {
		return uuid;
	}

	public Grade getGrade() {
		Grade s = Grade.JOUEUR;
		try {
			PreparedStatement state = PlugNAPI.getInstance().getConnection()
					.prepareStatement("SELECT grade FROM players WHERE uuid = ?");
			state.setString(1, uuid.toString());
			ResultSet result = state.executeQuery();
			if (result.next()) {
				s = Grade.get(result.getString("grade"));
			}
			result.close();
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return s;
	}

	public PlayerScoreboard getScoreboard() {
		return sb;
	}

}
