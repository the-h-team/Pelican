package com.github.sanctum.messenger.api.event;

import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Recipient;
import com.github.sanctum.panther.event.Vent;
import org.jetbrains.annotations.NotNull;

public final class IncomingMessageEvent extends Vent {

	private final Message m;
	private final Recipient r;

	public IncomingMessageEvent(@NotNull Message m, @NotNull Recipient recipient) {
		super((Host)PelicanAPI.getInstance(), true);
		this.m = m;
		this.r = recipient;
	}

	public Message getMessage() {
		return m;
	}

	public Recipient getRecipient() {
		return r;
	}
}
