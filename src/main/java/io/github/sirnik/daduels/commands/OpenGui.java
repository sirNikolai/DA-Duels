package io.github.sirnik.daduels.commands;

import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.utils.InventoryMenuManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        description = "Opens Arenas GUI",
        aliases = {"og", "list", "guis", "l"},
        nonPlayer = false)
public class OpenGui extends ExecutableCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        InventoryMenuManager.INSTANCE.openMenu((Player) sender);
    }
}
