/*
 *  Copyright (C) 2017 FALLET Nathan
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

package fr.zabricraft.plugnmanager.discord;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import fr.zabricraft.plugnmanager.PlugNManager;

public class StartServerStepper extends DiscordStepper {
	
	private String game;
	private int map;

	public StartServerStepper(long user, long channel) {
		super(user, channel);
		game = "";
		map = 0;
	}

	public String getMessage() {
		if (step == 0) {
			try {
				String message = "Choisissez un jeu pour le serveur à démarrer :\n";
				PreparedStatement state = PlugNManager.getInstance().getConnection()
						.prepareStatement("SELECT * FROM games WHERE id != ?");
				state.setString(1, "hub");
				ResultSet result = state.executeQuery();
				while (result.next()) {
					message += "\nTapez `" + result.getString("id") + "` pour démarrer un serveur "
							+ result.getString("name");
				}
				result.close();
				state.close();
				return message;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (step == 1) {
			try {
				String message = "Choisissez une carte pour le serveur à démarrer :\n";
				PreparedStatement state = PlugNManager.getInstance().getConnection()
						.prepareStatement("SELECT * FROM maps WHERE game = ?");
				state.setString(1, game);
				ResultSet result = state.executeQuery();
				while (result.next()) {
					message += "\nTapez `" + result.getInt("id") + "` pour choisir la carte "
							+ result.getString("name");
				}
				result.close();
				state.close();
				return message;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			return "Très bien, je démarre le serveur !";
		}
		end();
		return "Oups, une erreur est survenue !";
	}

	public void giveData(String data) {
		if (step == 0) {
			try {
				PreparedStatement state = PlugNManager.getInstance().getConnection()
						.prepareStatement("SELECT * FROM games WHERE id = ?");
				state.setString(1, data);
				ResultSet result = state.executeQuery();
				if (result.next()) {
					game = data;
					step++;
				}
				result.close();
				state.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (step == 1) {
			try {
				PreparedStatement state = PlugNManager.getInstance().getConnection()
						.prepareStatement("SELECT * FROM maps WHERE game = ? AND id = ?");
				state.setString(1, game);
				state.setInt(2, Integer.parseInt(data));
				ResultSet result = state.executeQuery();
				if (result.next()) {
					map = Integer.parseInt(data);
					step++;
					PlugNManager.getInstance().startServer(game, map);
					end();
				}
				result.close();
				state.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
