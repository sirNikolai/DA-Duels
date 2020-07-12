package io.github.sirnik.daduels.commands;

import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaCreatorManager;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        description = "Creates a new arena and enters creator mode",
        aliases = {"create", "new"},
        usage = "<Arena Name>",
        nonPlayer = false,
        mandatoryArgs = 1)
public class CreateArena extends ExecutableCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        DuelArena arena = ArenaManager.INSTANCE.getArena(args[0]);
        MessageManager manager = MessageManager.getManager(sender);

        if(arena != null) {
            manager.sendMessage(MessageManager.MessageType.BAD, "Arena with this name already exists");
            return;
        }

        arena = new DuelArena(args[0]);

        if(!ArenaCreatorManager.INSTANCE.addCreator(player, arena)) {
            manager.sendMessage(MessageManager.MessageType.BAD, "You are already creating or modifying a different arena");
            return;
        }

        ArenaManager.INSTANCE.addArena(arena, false);
        manager.sendMessage(MessageManager.MessageType.GOOD, "Entered creator mode. Remember to save when finished");
    }
}
