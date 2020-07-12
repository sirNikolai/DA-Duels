package io.github.sirnik.daduels.commands;

import io.github.sirnik.daduels.commands.management.CommandInfo;
import io.github.sirnik.daduels.commands.management.ExecutableCommand;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

@CommandInfo(description = "Removes player from arena", aliases = {"remove", "exit"})
public class ExitArena extends ExecutableCommand {

    static {
        Bukkit.getPluginManager().addPermission(new Permission("daduels.exitArena.others"));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "You cannot remove yourself from game as console.");
                return;
            }

            Player player = (Player) sender;

            if(ArenaManager.INSTANCE.removePlayer(player)) {
                MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.GOOD, "Successfully left arena");
                player.teleport(player.getWorld().getSpawnLocation());
                return;
            }

            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.NEUTRAL, "You are not in an arena");
            return;
        }

        if(!sender.hasPermission("daduels.exitArena.others")) {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Insufficient Permissions");
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if(player == null) {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Player not found");
            return;
        }

        if(ArenaManager.INSTANCE.removePlayer(player)) {
            MessageManager.getManager(player, sender).sendMessage(MessageManager.MessageType.GOOD, "Removed from arena");
            player.teleport(player.getWorld().getSpawnLocation());
        } else {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.NEUTRAL, "Player was not in an arena");
        }
    }
}
