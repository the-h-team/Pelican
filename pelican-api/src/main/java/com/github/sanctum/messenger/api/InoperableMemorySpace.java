package com.github.sanctum.messenger.api;

import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.panther.container.ImmutablePantherCollection;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;

public abstract class InoperableMemorySpace {

	static final PantherCollection<Pelican> ENTITIES = new PantherList<>();

	public static ImmutablePantherCollection<Pelican> values() {
		return ImmutablePantherCollection.of(ENTITIES);
	}

}
