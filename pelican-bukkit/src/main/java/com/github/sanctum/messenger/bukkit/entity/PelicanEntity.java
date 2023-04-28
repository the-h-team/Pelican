package com.github.sanctum.messenger.bukkit.entity;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.panther.util.Deployable;
import java.util.UUID;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PelicanEntity extends Pelican {

	private final PlayerSearch parent;

	public PelicanEntity(PlayerSearch search) {
		this.parent = search;
	}

	@Override
	public @NotNull String getName() {
		return parent.getName();
	}

	@Override
	public @NotNull UUID getId() {
		return parent.getId();
	}

	@Override
	public void open(@NotNull Object target) {

	}

	@Override
	public boolean isOnline() {
		return parent.getPlayer().isOnline();
	}

	@Override
	public Deployable<Void> send(Object o) throws NullPointerException {
		return Deployable.of(() -> {
			if (o instanceof ItemStack) {
				LabyrinthProvider.getInstance().getItemComposter().add((ItemStack) o, parent.getPlayer().getPlayer());
			} else {
				if (o instanceof BaseComponent[]) {
					parent.getPlayer().getPlayer().spigot().sendMessage((BaseComponent[]) o);
					return;
				}
				if (o instanceof BaseComponent) {
					parent.getPlayer().getPlayer().spigot().sendMessage((BaseComponent) o);
					return;
				}
				new FancyMessage().then(o.toString()).send(parent.getPlayer().getPlayer()).queue();
			}
		}, TaskService.ASYNCHRONOUS);
	}

	@Override
	public Type getType() {
		return Type.BUKKIT;
	}
}
