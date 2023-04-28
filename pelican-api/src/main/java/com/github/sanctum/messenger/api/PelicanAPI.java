package com.github.sanctum.messenger.api;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.messenger.api.entity.Pelican;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PelicanAPI {

	static PelicanAPI getInstance() {
		return LabyrinthProvider.getInstance().getServicesManager().load(PelicanAPI.class);
	}

	default @Nullable Pelican getPelican(@NotNull String name, @NotNull Pelican.Type type) {
		return InoperableMemorySpace.ENTITIES.stream().filter(e -> e.getType() == type && (e.getName().equals(name) || e.getId().toString().equals(name))).findFirst().orElse(null);
	}

	default void log(@NotNull Pelican entity) throws EntityDuplicationException {
		if (getPelican(entity.getName(), entity.getType()) != null) throw new EntityDuplicationException("Pelican entities can only be registered one time, " + entity.getName() + " already cached.");
		InoperableMemorySpace.ENTITIES.add(entity);
	}

}
