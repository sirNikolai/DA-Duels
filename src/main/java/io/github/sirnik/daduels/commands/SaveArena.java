package io.github.sirnik.daduels.commands;

import io.github.sirnik.daduels.DADuels;
import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaCreatorManager;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(description = "Saves an arena and exits creator mode", aliases = {"save", "finish"}, nonPlayer = false)
public class SaveArena  extends ExecutableCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        DuelArena arena = ArenaCreatorManager.INSTANCE.getArenaforCreator(player);

        if(arena == null) {
            MessageManager.getManager(player).sendMessage(MessageManager.MessageType.BAD, "You have no arena selected");
            return;
        }

        if(arena.getPlayer2Location() == null || arena.getPlayer1Location() == null) {
            MessageManager.getManager(player).sendMessage(MessageManager.MessageType.BAD, "Please set spawn locations first");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(DADuels.getInstance(), () -> ArenaManager.INSTANCE.updateArena(arena));

        ArenaCreatorManager.INSTANCE.removeCreator(player);

        MessageManager.getManager(player).sendMessage(MessageManager.MessageType.GOOD, "Arena saved and exited creator mode");
    }
}
