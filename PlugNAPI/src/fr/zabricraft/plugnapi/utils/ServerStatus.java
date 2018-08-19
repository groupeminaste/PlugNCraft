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

public enum ServerStatus {

	STARTING("Démarrage du serveur en cours...", "Démarrage en cours..."), OFFLINE("Ce serveur est hors ligne", "Hors ligne"), WAITING("En attente de joueurs", "En attente"), COUNTDOWN(
			"La partie commence dans %d secondes", "Début dans %ds"), PLAYING("La partie est en cours",
					"En cours"), FINISHED("La partie est terminée", "Terminée");

	private String text;
	private String text_short;

	private ServerStatus(String text, String text_short) {
		this.text = text;
		this.text_short = text_short;
	}

	public String getText() {
		return text;
	}

	public String getShortText() {
		return text_short;
	}

}
