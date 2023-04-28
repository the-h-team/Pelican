package com.github.sanctum.messenger.bukkit.command;

import com.github.sanctum.labyrinth.formatting.FancyMessage;
import com.github.sanctum.labyrinth.formatting.completion.SimpleTabCompletion;
import com.github.sanctum.labyrinth.formatting.completion.TabCompletionIndex;
import com.github.sanctum.messenger.api.DeliverySequence;
import com.github.sanctum.messenger.api.PelicanAPI;
import com.github.sanctum.messenger.api.entity.Pelican;
import com.github.sanctum.messenger.util.PelicanUtility;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageCommand extends Command {
	public MessageCommand() {
		super("message");
		setAliases(Arrays.asList("msg", "tell", "t", "mail"));
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

				return true;
			}

			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("auto")) {
					boolean n = !base.isManualRead();
					base.setManualRead(n);
					newMessage().then("&bAutomatic message reading set to &f" + (!n)).send(p).queue();
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
