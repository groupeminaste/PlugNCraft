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

package fr.zabricraft.plugnmanager.discord;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import fr.zabricraft.plugnmanager.PlugNManager;
import fr.zabricraft.plugnmanager.minecraft.ServerStatus;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.RequestBuffer;

public class DiscordListener {

	@EventSubscriber
	public void onMessageReceived(MessageReceivedEvent e) {
		if (e.getAuthor().getLongID() != e.getClient().getOurUser().getLongID()) {
			String request = e.getMessage().getContent().trim();

			DiscordStepper stepper = PlugNManager.getInstance().getStepper(e.getAuthor().getLongID(),
					e.getChannel().getLongID());
			if (stepper != null) {
				stepper.giveData(request);
				stepper.sendMessage();
				if (stepper.isDone()) {
					PlugNManager.getInstance().getDiscordStepper().remove(stepper);
				}
				return;
			}

			String answer = PlugNManager.getInstance().getCraftSearchAIAnswer(request);
			if (e.getChannel().isPrivate() && answer.isEmpty()) {
				answer = PlugNManager.getInstance().getCraftSearchAIAnswer("dis plugncraft " + request);
			}
			if (!answer.isEmpty()) {
				if (answer.equals("startServer()")) {
					answer = "";
					PlugNManager.getInstance().getDiscordStepper()
							.add(new StartServerStepper(e.getAuthor().getLongID(), e.getChannel().getLongID()));
				}
				if (answer.equals("status()")) {
					answer = "Voici les statuts des serveurs de PlugNCraft :\n";
					try {
						PreparedStatement state = PlugNManager.getInstance().getConnection()
								.prepareStatement("SELECT * FROM servers WHERE status != ?");
						state.setString(1, ServerStatus.OFFLINE.toString());
						ResultSet result = state.executeQuery();
						while (result.next()) {
							answer += "\n`" + result.getString("id") + "` est ";
							switch (result.getString("status")) {
							case "STARTING":
								answer += "en cours de démarrage";
								break;
							case "WAITING":
								answer += "en attente, avec " + result.getInt("players") + " joueur(s) en ligne";
								break;
							case "COUNTDOWN":
								answer += "au compte à rebours, avec " + result.getInt("players")
										+ " joueur(s) en ligne";
								break;
							case "PLAYING":
								answer += "en cours, avec " + result.getInt("players") + " joueur(s) en ligne";
								break;
							case "FINISHED":
								answer += "terminé";
								break;
							default:
								answer += "de statut inconnu";
							}
							answer += ".";
						}
						result.close();
						state.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				if (answer.contains("@player")) {
					answer = answer.replaceAll("@player", e.getAuthor().mention());
				}
				if (answer.contains("@discord_server_count")) {
					answer = answer.replaceAll("@discord_server_count", e.getClient().getGuilds().size() + "");
				}
				if (answer.contains("@discord_server_list")) {
					String list = "";
					for (IGuild guild : e.getClient().getGuilds()) {
						list += ", " + guild.getName() + " (" + guild.getLongID() + ")";
					}
					answer = answer.replaceAll("@discord_server_list", list.substring(2));
				}
				if (!answer.isEmpty()) {
					String discord_msg = answer;
					RequestBuffer.request(() -> e.getChannel().sendMessage(discord_msg));
				}
			}
		}

	}

	@EventSubscriber
	public void onUserJoin(UserJoinEvent e) {
		if (e.getGuild().getLongID() == 264703386127958024L) {
			RequestBuffer.request(() -> e.getClient().getOrCreatePMChannel(e.getUser())
					.sendMessage("Bienvenue " + e.getUser().mention() + " sur le serveur Discord de "
							+ e.getGuild().getName() + "!\n\n"
							+ "PlugNCraft est le premier serveur automatisé sur demande.\n\n"
							+ "Tu n'as qu'à dire `Dis PlugNCraft` n'importe où où je suis présent."));
		}
	}

}
