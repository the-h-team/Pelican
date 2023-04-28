package com.github.sanctum.messenger.util;

import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.api.entity.Sender;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleMessage implements Message {

	private final Object message;
	private final String sender;
	private final String subject;
	private final Date date;
	int validity;
	public SimpleMessage(@NotNull Object message, @NotNull String sender, @Nullable String subject) {
		this.message = message;
		this.sender = sender;
		this.subject = subject;
		this.date = new Date();
	}

	@Override
	public @NotNull("Sender can not be null!") Sender getSender() {
		Sender s = PelicanAPI.getInstance().getPelican(sender, Pelican.Type.BUKKIT);
		assert s != null;
		return s;
	}

	@Override
	public @Nullable String getSubject() {
		return subject;
	}

	@Override
	public @NotNull String getText() {
		return message.toString();
	}

	@Override
	public @NotNull Object getRaw() {
		return message;
	}

	@Override
	public @NotNull Date getDateSent() {
		return this.date;
	}

	@Override
	public boolean isText() {
		return getRaw() instanceof String;
	}

	@Override
	public boolean isItem() {
		return getRaw().getClass().getName().contains("ItemStack");
	}

	@Override
	public boolean setValid(boolean valid) {
		if (!isValid()) return false;
		if (!valid) validity = 1;
		return true;
	}

	@Override
	public boolean isValid() {
		return PelicanAPI.getInstance().getPelican(sender, Pelican.Type.BUKKIT) != null && validity == 0;
	}
}
