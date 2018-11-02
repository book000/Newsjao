package com.jaoafa.Newsjao.Event;

import java.util.ArrayList;
import java.util.List;

import com.jaoafa.Newsjao.NewsSpeak;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.audio.AudioPlayer;

public class UserLeaveEvent {
	@EventSubscriber
	public void onUserVoiceChannelLeaveEvent(UserVoiceChannelLeaveEvent event){
		if(event.getVoiceChannel().getLongID() != 189377933356302336L){
			return; // General VCじゃなければスキップ
		}
		System.out.println("VoiceLeave: " + event.getUser().getName() + " " + event.getVoiceChannel().getName());

		AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(event.getGuild());
		audioP.clear();

		List<IUser> noBots = new ArrayList<>();
		for(IUser user : event.getVoiceChannel().getConnectedUsers()){
			if(user.isBot()){
				continue;
			}
			noBots.add(user);
		}
		System.out.println("VCLeft: " + noBots.size());
		if(noBots.size() == 0){ // 自分含め
			event.getVoiceChannel().leave();

			if(NewsSpeak.newsmessage != null && !NewsSpeak.newsmessage.isDeleted()){
				NewsSpeak.newsmessage.delete();
			}
		}
	}


	@EventSubscriber
	public void onUserVoiceChannelMoveEvent(UserVoiceChannelMoveEvent event){
		if(event.getOldChannel().getLongID() != 189377933356302336L){
			return; // General VCじゃなければスキップ
		}
		System.out.println("VoiceLeave: " + event.getUser().getName() + " " + event.getOldChannel().getName());

		AudioPlayer audioP = AudioPlayer.getAudioPlayerForGuild(event.getGuild());
		audioP.clear();

		List<IUser> noBots = new ArrayList<>();
		for(IUser user : event.getOldChannel().getConnectedUsers()){
			if(user.isBot()){
				continue;
			}
			noBots.add(user);
		}
		System.out.println("VCLeft: " + noBots.size());
		if(noBots.size() == 0){ // 自分含め
			event.getOldChannel().leave();

			if(NewsSpeak.newsmessage != null && !NewsSpeak.newsmessage.isDeleted()){
				NewsSpeak.newsmessage.delete();
			}
		}
	}
}
