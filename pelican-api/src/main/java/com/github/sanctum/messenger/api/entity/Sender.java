package com.github.sanctum.messenger.api.entity;

import com.github.sanctum.messenger.api.DeliverySequence;
import com.github.sanctum.panther.util.Deployable;
import org.jetbrains.annotations.NotNull;

public interface Sender extends Messenger {

	@NotNull Deployable<DeliverySequence> write(@NotNull Object o, Recipient... recipients);

	@NotNull Deployable<DeliverySequence> write(@NotNull Object o, @NotNull String subject, Recipient... recipients);

	Deployable<Void> write(@NotNull DeliverySequence sequence);

	default boolean isConsole() {
		return getClass().getName().contains("ServerEntity");
	}

}
