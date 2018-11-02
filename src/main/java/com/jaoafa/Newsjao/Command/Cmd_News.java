package com.jaoafa.Newsjao.Command;

import com.jaoafa.Newsjao.NewsSpeak;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class Cmd_News {
	public static void onCommand(IDiscordClient client, IGuild guild, IChannel channel, IUser author, IMessage message, String[] args){
		IVoiceChannel vc = guild.getVoiceChannelByID(189377933356302336L); // General VC

		NewsSpeak.speakNews(client, channel, vc, false);
	}
}
