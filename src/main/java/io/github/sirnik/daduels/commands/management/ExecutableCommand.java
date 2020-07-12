package io.github.sirnik.daduels.commands.management;

import org.bukkit.command.CommandSender;

/**
 * Commands that can be executed by the handler
 */
public abstract class ExecutableCommand {

    public abstract void onCommand(CommandSender sender, String[] args);

}
