package com.github.sanctum.messenger.api;

import com.github.sanctum.messenger.api.entity.Recipient;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherSet;
import org.jetbrains.annotations.NotNull;

public interface DeliverySequence {

	@NotNull Recipient[] getResponders();

	@NotNull Message getContent();

	default void deploy() {
		for (Recipient r : getResponders()) {
			r.read(this).queue();
		}
	}

	static Builder builder() {
		return new Builder() {
		};
	}

	abstract class Builder {

		private final PantherCollection<Recipient> recipients = new PantherSet<>();
		private Message context;

		public Builder add(@NotNull Recipient recipient) {
			recipients.add(recipient);
			return this;
		}

		public Builder set(@NotNull Message context) {
			this.context = context;
			return this;
		}

		public DeliverySequence build() {
			return new DeliverySequence() {
				@Override
				public @NotNull Recipient[] getResponders() {
					return recipients.stream().toArray(Recipient[]::new);
				}

				@Override
				public @NotNull Message getContent() {
					return context;
				}
			};
		}

	}

}
