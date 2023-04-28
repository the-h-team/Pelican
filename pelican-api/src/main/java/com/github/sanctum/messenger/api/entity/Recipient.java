package com.github.sanctum.messenger.api.entity;

import com.github.sanctum.messenger.api.DeliverySequence;
import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.panther.util.Deployable;
import org.jetbrains.annotations.NotNull;

public interface Recipient extends Messenger {

	@NotNull Deployable<Message> read(@NotNull DeliverySequence sequence);

	@NotNull Deployable<Message> read(@NotNull Message message);

	static Recipient byName(String name) {
		if (name.equals("SERVER")) return PelicanAPI.getInstance().getPelican("Pelican", Pelican.Type.UNKNOWN);
		return PelicanAPI.getInstance().getPelican(name, Pelican.Type.BUKKIT);
	}

}
