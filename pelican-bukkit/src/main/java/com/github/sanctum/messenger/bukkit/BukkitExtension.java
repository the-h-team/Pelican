package com.github.sanctum.messenger.bukkit;

import com.github.sanctum.messenger.bukkit.impl.BukkitEventMonitor;
import com.github.sanctum.messenger.bukkit.impl.BukkitSerializer;
import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.data.FileList;
import com.github.sanctum.labyrinth.data.FileManager;
import com.github.sanctum.labyrinth.data.Registry;
import com.github.sanctum.labyrinth.data.service.PlayerSearch;
import com.github.sanctum.labyrinth.event.DefaultEvent;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.library.CommandUtils;
import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.messenger.api.EntityDuplicationException;
import com.github.sanctum.messenger.api.InoperableMemorySpace;
import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.api.entity.Recipient;
import com.github.sanctum.messenger.api.entity.Sender;
import com.github.sanctum.messenger.api.event.IncomingMessageEvent;
import com.github.sanctum.messenger.bukkit.entity.PelicanEntity;
import com.github.sanctum.messenger.bukkit.entity.ServerEntity;
import com.github.sanctum.messenger.util.PelicanUtility;
import com.github.sanctum.panther.event.Subscribe;
import com.github.sanctum.panther.event.Vent;
import com.github.sanctum.panther.event.VentMap;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.file.JsonAdapter;
import com.github.sanctum.panther.file.Node;
import java.text.MessageFormat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitExtension extends JavaPlugin implements Vent.Host, PelicanAPI {

	@Override
	public void onEnable() {
		// Plugin startup logic
		JsonAdapter.register(BukkitSerializer.class);
		LabyrinthProvider.getInstance().getServicesManager().register(this, this, ServicePriority.High);
		VentMap.getInstance().subscribeAll(this, this, new BukkitEventMonitor());
		FileManager data = FileList.search(this).get("data", null, Configurable.Type.JSON);
		try {
			log(new ServerEntity());
		} catch (EntityDuplicationException e) {
			e.printStackTrace();
		}
		for (PlayerSearch search : PlayerSearch.values()) {
			Pelican entry = new PelicanEntity(search);
			try {
				log(entry);
			} catch (EntityDuplicationException e) {
				e.printStackTrace();
			}
		}
		TaskScheduler.of(() -> {
			for (Pelican pelican : InoperableMemorySpace.values()) {
				Node n = data.getRoot().getNode(pelican.getId().toString());
				if (n.get() != null) {
					for (Message e : n.get(Message[].class)) {
						pelican.read(e).queue();
					}
				}
			}
			getLogger().info("Successfully logged user messages.");
		}).scheduleLater(48L);
		new Registry<>(Command.class).source(this).filter("com.github.sanctum.messenger.bukkit").operate(cmd -> {
			CommandUtils.read(registry -> {
				CommandMap map = registry.getKey();
				map.register(cmd.getLabel(), getName(), cmd);
				return null;
			});
		});
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		FileManager data = FileList.search(this).get("data", null, Configurable.Type.JSON);
		for (Pelican ent : InoperableMemorySpace.values()) {
			data.getRoot().getNode(ent.getId().toString()).set(ent.inbox().stream().toArray(Message[]::new));
		}
		data.getRoot().save();
		LabyrinthProvider.getInstance().getServicesManager().unregister(this);
	}

	@Subscribe
	public void onJoin(DefaultEvent.Join e) {
		Player p = e.getPlayer();
		Pelican base = getPelican(p.getName(), Pelican.Type.BUKKIT);
		if (base != null) {
			if (base.inbox().size() > 0) {
				TaskScheduler.of(() -> {
					p.sendMessage(" ");
					FancyMessage m = PelicanUtility.appendPrefix(new FancyMessage());
					m.then("&e&oYou have &d" + base.inbox().size() + " &e&ounread messages ").then("&f[&bCLICK&f]").command("inbox").then(" &e&oto read.").send(p).deploy();
					p.sendMessage(" ");
				}).scheduleLater(24L);
			}
		}
	}

	@Subscribe(priority = Vent.Priority.MEDIUM)
	public void onMessageReceive(IncomingMessageEvent e) {
		Message m = e.getMessage();
		Sender s = m.getSender();
		Recipient r = e.getRecipient();
		if (!s.isConsole()) {
			if (m.isItem()) {
				Pelican pelican = (Pelican) r;
				if (pelican.isManualRead()) {
					FancyMessage message = PelicanUtility.appendPrefix(new FancyMessage()).then("New").then(" ").then("&7[&3&lHOVER&7]").hover((ItemStack)m.getRaw()).then(" ").then("from " + s.getName()).then(" ").then("&7[&6CLICK&7]").action(() -> {
						if (r.isOnline() && m.isValid()) {
							r.send(m.getRaw()).deploy();
							pelican.destroy(m).queue();
						}
					}).then(" ").then("to obtain.");
					if (r.isOnline()) {
						r.send(message.build()).queue();
					}
				} else {
					r.send(m.getRaw()).queue();
					r.send(PelicanUtility.appendPrefix(new FancyMessage()).then("New").then(" ").then("&7[&3&lHOVER&7]").hover((ItemStack)m.getRaw()).then(" ").then("from " + s.getName()).build()).queue();
					pelican.destroy(m).queue();
				}
			} else if (m.isText()) {
				Pelican pelican = (Pelican) r;
				if (pelican.isManualRead()) {
					FancyMessage message = PelicanUtility.appendPrefix(new FancyMessage("New message from " + s.getName()).then(" ").then("&7[&3&lCLICK&7]").action(() -> {
						if (r.isOnline() && m.isValid()) {
							r.send(MessageFormat.format("&7[&b✉&7] &7{0} &r&l&m→&r &3&ome", m.getSender().getName()) + (" &7[&f" + m.getText() + "&7]")).deploy();
							pelican.destroy(m).queue();
						}
					}).then(" ").then("to read."));
					if (r.isOnline() && m.isValid()) {
						r.send(message.build()).deploy();
					}
				} else {
					r.send(MessageFormat.format("&7[&b✉&7] &7{0} &r&l&m→&r &3&ome", m.getSender().getName()) + (" &7[&f" + m.getText() + "&7]")).deploy();
					pelican.destroy(m).queue();
				}
			}
		} else {
			// TODO: send from console messages
		}

	}

}
