package com.github.sanctum.messenger.util;

import com.github.sanctum.labyrinth.formatting.FancyMessage;
import org.jetbrains.annotations.NotNull;

public final class PelicanUtility {

	public static FancyMessage appendPrefix(@NotNull FancyMessage message) {
		return appendSpecialPrefix(message, "&7[&bPelican&7]");
	}

	public static FancyMessage appendSpecialPrefix(@NotNull FancyMessage message, @NotNull String prefix) {
		return new FancyMessage().then(prefix).then(" ").append(message);
	}

}
