package com.github.sanctum.messenger.bukkit.entity;

import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.panther.util.Deployable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class ServerEntity extends Pelican {
	@Override
	public boolean isOnline() {
		return Bukkit.getPluginManager().isPluginEnabled("Pelican");
	}

	@Override
	public Deployable<Void> send(Object o) {
		return null;
	}

	@Override
	public @NotNull String getName() {
		return "Pelican";
	}

	@Override
	public @NotNull UUID getId() {
		return UUID.nameUUIDFromBytes(getName().getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public void open(@NotNull Object target) {

	}

	@Override
	public Type getType() {
		return Type.UNKNOWN;
	}
}
