package com.github.sanctum.messenger.bukkit.command;

import com.github.sanctum.labyrinth.LabyrinthProvider;
import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.labyrinth.library.CompostElement;
import com.github.sanctum.labyrinth.library.InventorySync;
import com.github.sanctum.labyrinth.library.ItemCompost;
import com.github.sanctum.labyrinth.library.ItemMatcher;
import com.github.sanctum.labyrinth.library.ItemSync;
import com.github.sanctum.labyrinth.library.StringUtils;
import com.github.sanctum.messenger.api.DeliverySequence;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.api.entity.Recipient;
import com.github.sanctum.messenger.util.PelicanUtility;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MessageCommand extends Command {
	public MessageCommand() {
		super("message");
		setAliases(Arrays.asList("msg", "gift", "send", "tell", "t", "mail"));
	}

	private final PelicanAPI api = PelicanAPI.getInstance();

	FancyMessage newMessage() {
		return PelicanUtility.appendPrefix(new FancyMessage());
	}

	@NotNull
	@Override
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		return SimpleTabCompletion.of(args).then(TabCompletionIndex.ONE, Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList())).get();
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Pelican base = api.getPelican(p.getName(), Pelican.Type.BUKKIT);

			if (base == null) return true;

			if (args.length == 0) {
				newMessage().then("&fUsage: &c/" + commandLabel + " <playerNames,..> [message]").send(p).queue();
				newMessage().then("&fLeaving the message field blank will gift the specified player(s) the item in your hand.").send(p).queue();
				return true;
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("auto")) {
					boolean n = !base.isManualRead();
					base.setManualRead(n);
					newMessage().then("&bAutomatic message reading set to &f" + (!n)).send(p).queue();
				}
				PantherCollection<Pelican> entities = new PantherList<>();
				for (String user : args[0].split(",")) {
					Pelican pelican = api.getPelican(user, Pelican.Type.BUKKIT);
					if (pelican != null) {
						entities.add(pelican);
					}
				}

				if (!entities.isEmpty()) {
					ItemStack item = p.getInventory().getItemInMainHand();
					if (entities.size() > item.getAmount()) {
						// not enough of the item to share with this many people.
						newMessage().then("&cYou don't have enough of this item to share with people.").send(p).queue();
						return true;
					}
					if (entities.size() == 1) {
						Recipient r = entities.get(0);
						DeliverySequence sequence = base.write(new ItemStack(item), "Gift", r).complete();
						sequence.deploy();
						newMessage().then("&3You sent item").then(" ").then("&f[&dHOVER&f]").hover(item).then(" ").then("&3to user " + r.getName()).send(p).queue();
						item.setAmount(0);
					} else {
						ItemStack copy = new ItemStack(item);
						newMessage().then("&3You sent item").then(" ").then("&f[&dHOVER&f]").hover(copy).then(" ").then("&3to &e" + entities.size() + " &3user(s)").send(p).queue();
						for (Recipient r : entities) {
							copy.setAmount(1);
							DeliverySequence sequence = base.write(copy, "Gift", r).complete();
							sequence.deploy();
							item.setAmount(item.getAmount() - 1);
						}
					}
				} else {
					newMessage().then("&cNot able to match the provided player(s) from the database.").send(p).queue();
				}
				return true;
			}

			Pelican target = api.getPelican(args[0], Pelican.Type.BUKKIT);
			if (target != null) {
				StringBuilder builder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					builder.append(args[i]).append(" ");
				}
				DeliverySequence sequence = base.write(builder.toString().trim(), target).complete();
				sequence.deploy();
				newMessage().then("&3You sent message").then(" ").then("&f[&dHOVER&f]").hover("&6&o" + ChatColor.stripColor(builder.toString().trim())).then(" ").then("&3to user " + target.getName()).send(p).queue();
			}


		} else {

		}
		return true;
	}

}
