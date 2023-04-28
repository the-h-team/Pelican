package com.github.sanctum.messenger.bukkit.command;

import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.pagination.EasyPagination;
import com.github.sanctum.labyrinth.library.Mailer;
import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.util.PelicanUtility;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InboxCommand extends Command {
	public InboxCommand() {
		super("inbox");
	}

	private final PelicanAPI api = PelicanAPI.getInstance();

	FancyMessage newMessage() {
		return PelicanUtility.appendPrefix(new FancyMessage());
	}

	@NotNull
	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		return new ArrayList<>();
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Pelican base = api.getPelican(p.getName(), Pelican.Type.BUKKIT);

			if (base == null) return true;

			if (args.length == 0) {
				EasyPagination<Message> messages = new EasyPagination<>(p, base.inbox().stream().collect(Collectors.toList()), (o1, o2) -> o2.getDateSent().compareTo(o1.getDateSent()));
				messages.setHeader((player, message) -> message.then("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
				messages.setFormat((message, integer, fancy) -> {
					if (message.isText()) {
						fancy.then("&bMESSAGE").hover("&dSent: &f" + message.getDateSent().toLocaleString()).hover("&dSender: &f" + message.getSender().getName()).then(" ");
						if (message.getSubject() != null) {
							fancy.then("&7(&f&n" + message.getSubject() + "&7)").then(" ").then("&f|").then(" ").then("&6click").action(() -> {
								if (message.isValid()) {
									newMessage().then("&bMail opened.").send(p).queue();
									base.send(MessageFormat.format("&7[&b✉&7] &7{0} &r&l&m→&r &3&ome", message.getSender().getName()) + (" &7[&f" + message.getText() + "&7]")).deploy();
									base.destroy(message).queue();
								}
							});
						} else {
							fancy.then("&f|").then(" ").then("&6click").action(() -> {
								if (message.isValid()) {
									newMessage().then("&bMail opened.").send(p).queue();
									base.send(MessageFormat.format("&7[&b✉&7] &7{0} &r&l&m→&r &3&ome", message.getSender().getName()) + (" &7[&f" + message.getText() + "&7]")).deploy();
									base.destroy(message).queue();
								}
							});
						}
					} else {
						fancy.then("&3GIFT").hover("&dSent: &f" + message.getDateSent().toLocaleString()).hover("&dSender: &f" + message.getSender().getName()).hover("&dAmount: &f" + ((ItemStack)message.getRaw()).getAmount()).then(" ").then("&f|").then(" ").then("&6click").hover((ItemStack)message.getRaw()).action(() -> {
							if (message.isValid()) {
								base.send(message.getRaw()).queue();
								base.destroy(message).queue();
								newMessage().then("&bGift retrieved.").send(p).queue();
							}
						});
					}
				});
				messages.setFooter((player, message) -> message.then("&f&l&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
				messages.limit(8);
				if (base.inbox().size() == 0) {
					Mailer.empty(p).chat("&eYou currently have no mail. But hang around, im sure you'll get something!").deploy();
				} else {
					Mailer.empty(p).chat("&fInbox &7(&e" + base.inbox().size() + "&7)").deploy();
					messages.send(1);
				}
				return true;
			}

			if (args.length == 1) {

				return true;
			}

			if (args.length == 2) {

				return true;
			}

		} else {

		}
		return true;
	}
}
