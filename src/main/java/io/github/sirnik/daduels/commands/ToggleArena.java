package io.github.sirnik.daduels.commands;

import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelState;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.command.CommandSender;

@CommandInfo(
        description = "Toggles the status of an arena",
        aliases = {"toggle"},
        usage = "<Arena Name>",
        mandatoryArgs = 1)
public class ToggleArena extends ExecutableCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        DuelArena arena = ArenaManager.INSTANCE.getArena(args[0]);
        MessageManager manager = MessageManager.getManager(sender);

        if(arena == null) {
            manager.sendMessage(MessageManager.MessageType.BAD, "Arena with given name not found");
            return;
        }

        if(arena.getCurrentState() == DuelState.DISABLED) {
            if(!ArenaManager.INSTANCE.enableArena(arena)) {
                manager.sendMessage(MessageManager.MessageType.BAD, "Arena could not be enabled");
                manager.sendMessage(MessageManager.MessageType.BAD, "Please check it isn't in game and spawn locations are set");
            } else {
                manager.sendMessage(MessageManager.MessageType.GOOD, "Arena " + args[0] + " has been enabled!");
            }
        } else {
            if(!ArenaManager.INSTANCE.disableArena(arena)) {
                manager.sendMessage(MessageManager.MessageType.BAD, "Arena could not be disabled");
            } else {
                manager.sendMessage(MessageManager.MessageType.GOOD, "Arena " + args[0] + " has been disabled!");
            }
        }
    }
}
