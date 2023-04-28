package com.github.sanctum.messenger.bukkit.command;

import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.messenger.api.DeliverySequence;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.util.PelicanUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReplyCommand extends Command {
	public ReplyCommand() {
		super("reply");
		setAliases(Collections.singletonList("r"));
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

				return true;
			}

			StringBuilder builder = new StringBuilder();
			for (String arg : args) {
				builder.append(arg).append(" ");
			}

			Pelican recent = base.getRecent();
			if (recent != null) {
				DeliverySequence sequence = base.write(builder.toString().trim(), recent).complete();
				sequence.deploy();
				newMessage().then("&3You sent message").then(" ").then("&f[&dHOVER&f]").hover("&6&o" + ChatColor.stripColor(builder.toString().trim())).then(" ").then("&3to user " + recent.getName()).send(p).queue();
			}

		} else {

		}
		return true;
	}
}
