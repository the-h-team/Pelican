package com.github.sanctum.messenger.api.entity;

import com.github.sanctum.panther.util.Deployable;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public interface Messenger {

	@NotNull String getName();

	@NotNull UUID getId();

	boolean isOnline();

	Deployable<Void> send(Object o) throws NullPointerException;

}
