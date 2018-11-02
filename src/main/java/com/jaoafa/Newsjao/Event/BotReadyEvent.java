package com.jaoafa.Newsjao.Event;

import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;

public class BotReadyEvent {
	@EventSubscriber
	public void onReadyEvent(ReadyEvent event) {
		System.out.println("Ready: " + event.getClient().getOurUser().getName());
	}
}
