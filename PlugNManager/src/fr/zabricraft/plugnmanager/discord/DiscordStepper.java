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

import fr.zabricraft.plugnmanager.PlugNManager;
import sx.blah.discord.util.RequestBuffer;

public abstract class DiscordStepper {

	private long user;
	private long channel;
	protected int step;
	private boolean done;

	public DiscordStepper(long user, long channel) {
		this.user = user;
		this.channel = channel;
		this.step = 0;
		this.done = false;
		sendMessage();
	}

	public abstract String getMessage();

	public abstract void giveData(String data);

	public boolean isFor(long user, long channel) {
		return this.user == user && this.channel == channel;
	}
	
	public void end() {
		done = true;
	}

	public boolean isDone() {
		return done;
	}
	
	public long getUser() {
		return user;
	}
	
	public void sendMessage() {
		RequestBuffer.request(
				() -> PlugNManager.getInstance().getDiscord().getChannelByID(channel).sendMessage(getMessage()));
	}

}
