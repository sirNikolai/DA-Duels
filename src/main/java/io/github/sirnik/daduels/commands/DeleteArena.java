package io.github.sirnik.daduels.commands;

import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.command.CommandSender;

@CommandInfo(description = "Deletes an arena with specified name", aliases = {"delete", "del"}, mandatoryArgs = 1, usage = "<Arena Name>")
public class DeleteArena extends ExecutableCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        DuelArena arena = ArenaManager.INSTANCE.getArena(args[0]);

        if(arena == null) {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Arena does not exist");
            return;
        }

        ArenaManager.INSTANCE.disableArena(arena);
        ArenaManager.INSTANCE.deleteArena(arena);
        MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.GOOD, "Arena has been deleted");
    }
}
