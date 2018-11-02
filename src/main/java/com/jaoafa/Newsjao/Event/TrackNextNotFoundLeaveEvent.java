package com.jaoafa.Newsjao.Event;

import com.jaoafa.Newsjao.NewsSpeak;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.audio.events.TrackFinishEvent;

public class TrackNextNotFoundLeaveEvent {
	@EventSubscriber
	public void onTrackFinishEvent(TrackFinishEvent event){
		if(event.getNewTrack().isPresent()){
			return;
		}
		IVoiceChannel botVoiceChannel = event.getClient().getOurUser().getVoiceStateForGuild(event.getPlayer().getGuild()).getChannel();

 		if(botVoiceChannel == null) return;

 		botVoiceChannel.leave();

		if(NewsSpeak.newsmessage != null && !NewsSpeak.newsmessage.isDeleted()){
			NewsSpeak.newsmessage.delete();
		}
	}
}
