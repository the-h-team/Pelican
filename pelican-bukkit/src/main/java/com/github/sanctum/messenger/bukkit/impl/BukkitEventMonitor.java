package com.github.sanctum.messenger.bukkit.impl;

import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.api.event.IncomingMessageEvent;
import com.github.sanctum.panther.event.Subscribe;
import com.github.sanctum.panther.event.Vent;

public class BukkitEventMonitor {

	@Subscribe(priority = Vent.Priority.LOW)
	public void onIncoming(IncomingMessageEvent e) {
		Pelican p = (Pelican) e.getRecipient();
		p.input(new BukkitMessage(e.getMessage()));
	}

}
