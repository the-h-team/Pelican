package com.github.sanctum.messenger.bukkit.impl;

import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.api.entity.Sender;
import java.util.Date;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitMessage implements Message {

	final boolean physical;
	final String sender;
	final String subject;
	Date sent = new Date();
	int validity;
	ItemStack item;
	String text;

	public BukkitMessage(Message adapter) {
		this(adapter.getRaw(), adapter.getSender().getName(), adapter.getSubject());
	}

	public BukkitMessage(@NotNull Object message, @NotNull String sender, @Nullable String subject) {
		this.physical = message instanceof ItemStack;
		this.sender = sender;
		this.subject = subject;
		if (physical) {
			this.item = (ItemStack) message;
		} else this.text = message.toString();
	}

	public BukkitMessage(@NotNull Object message, @NotNull String sender, @Nullable String subject, Date date) {
		this.physical = message instanceof ItemStack;
		this.sender = sender;
		this.sent = date;
		this.subject = subject;
		if (physical) {
			this.item = (ItemStack) message;
		} else this.text = message.toString();
	}

	@Override
	public @NotNull Sender getSender() {
		Sender s = PelicanAPI.getInstance().getPelican(sender, Pelican.Type.BUKKIT);
		assert s != null;
		return s;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public @NotNull String getText() {
		return text;
	}

	@Override
	public @NotNull Object getRaw() {
		return isItem() ? item : getText();
	}

	@Override
	public @NotNull Date getDateSent() {
		return sent;
	}

	@Override
	public boolean isText() {
		return !physical;
	}

	@Override
	public boolean isItem() {
		return physical;
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
