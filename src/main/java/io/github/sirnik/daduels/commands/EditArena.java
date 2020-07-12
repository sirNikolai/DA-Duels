package io.github.sirnik.daduels.commands;

import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaCreatorManager;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
        description = "Edits an existing Arena",
        aliases = {"edit"},
        mandatoryArgs = 1,
        nonPlayer = false,
        usage = "<Arena Name>")
public class EditArena extends ExecutableCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        DuelArena arena = ArenaManager.INSTANCE.getArena(args[0]);
        MessageManager manager = MessageManager.getManager(sender);

        if(arena == null) {
            manager.sendMessage(MessageManager.MessageType.BAD, "Arena with this name does not exist");
            manager.sendMessage(MessageManager.MessageType.BAD, "Please create one using " + ChatColor.BOLD + "/daduels create " + args[0]);
            return;
        }

        arena = new DuelArena(args[0]);

        if(!ArenaCreatorManager.INSTANCE.addCreator(player, arena)) {
            manager.sendMessage(MessageManager.MessageType.BAD, "You are already creating or modifying a different arena");
            return;
        }

        manager.sendMessage(MessageManager.MessageType.GOOD, "Entered creator mode. Remember to save when finished");
    }
}
