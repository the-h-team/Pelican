package com.github.sanctum.messenger.bukkit.command;

import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.messenger.api.DeliverySequence;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.api.entity.Recipient;
import com.github.sanctum.messenger.util.PelicanUtility;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GiftCommand extends Command {
	public GiftCommand() {
		super("gift");
	}

	private final PelicanAPI api = PelicanAPI.getInstance();

	FancyMessage newMessage() {
		return PelicanUtility.appendPrefix(new FancyMessage());
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			Pelican base = api.getPelican(p.getName(), Pelican.Type.BUKKIT);

			if (base == null) return true;

			PantherCollection<Pelican> entities = new PantherList<>();
			for (String user : args) {
				Pelican target = api.getPelican(user, Pelican.Type.BUKKIT);
				if (target != null) {
					entities.add(target);
				}
			}

			if (!entities.isEmpty()) {
				ItemStack item = p.getInventory().getItemInMainHand();
				if (entities.size() > item.getAmount()) {
					// not enough of the item to share with this many people.
					return true;
				}
				if (entities.size() == 1) {
					Recipient r = entities.get(0);
					DeliverySequence sequence = base.write(new ItemStack(item), "Gift", r).complete();
					sequence.deploy();
					newMessage().then("&3You sent item").then(" ").then("&f[&dHOVER&f]").hover(item).then(" ").then("&3to user " + r.getName()).send(p).queue();
					item.setAmount(0);
				} else {
					for (Recipient r : entities) {
						ItemStack copy = new ItemStack(item);
						copy.setAmount(1);
						DeliverySequence sequence = base.write(copy, "Gift", r).complete();
						sequence.deploy();
						item.setAmount(item.getAmount() - 1);
						newMessage().then("&3You sent item").then(" ").then("&f[&dHOVER&f]").hover(copy).then(" ").then("&3to user " + r.getName()).send(p).queue();
					}
				}
			} else {
				// no peeps to send to.
			}

		} else {

		}
		return true;
	}
}
