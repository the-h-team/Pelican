package com.github.sanctum.messenger.bukkit.command;

import com.github.sanctum.messenger.api.PelicanAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PelicanCommand extends Command {
	public PelicanCommand() {
		super("pelican");
	}

	private final PelicanAPI api = PelicanAPI.getInstance();

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (args.length == 0) {

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
