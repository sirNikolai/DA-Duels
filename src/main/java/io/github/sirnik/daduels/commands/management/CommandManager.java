package io.github.sirnik.daduels.commands.management;

import io.github.sirnik.daduels.utils.MessageManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements CommandExecutor {

    private List<ExecutableCommand> cmds;
    private String name;
    private String permName;

    /**
     * Create Command class
     *
     * @param name The name of the plugin
     */
    public CommandManager(String name) {
        this.name = name;
        this.permName = name.replaceAll(" ", "").toLowerCase();

        cmds = new ArrayList<>();
        Bukkit.getPluginManager().addPermission(new Permission(permName + ".cmds"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            if (!sender.hasPermission(permName + ".cmds")) {
                MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Insufficient permissions");
                return true;
            }

            sendCommandList(sender, cmd);
            return true;
        }

        ExecutableCommand wanted = null;

        for (ExecutableCommand gcmd : cmds) {
            CommandInfo info = gcmd.getClass().getAnnotation(CommandInfo.class);

            for (String alias : info.aliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    wanted = gcmd;
                    break;
                }
            }
        }

        if (wanted == null) {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Command not found!");
            if (sender.hasPermission(permName + ".cmds")) {
                MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Please do /" + cmd.getName() + " to get a list of commands");
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            if (!wanted.getClass().getAnnotation(CommandInfo.class).nonPlayer()) {
                sender.sendMessage(ChatColor.RED + "Must be a player to use this command");
                return true;
            }
        }

        ArrayList<String> newArgs = new ArrayList<>();
        Collections.addAll(newArgs, args);
        newArgs.remove(0);
        args = newArgs.toArray(new String[0]);

        if (args.length < wanted.getClass().getAnnotation(CommandInfo.class).mandatoryArgs()) {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Insufficient arguments!");
            CommandInfo info = wanted.getClass().getAnnotation(CommandInfo.class);
            sender.sendMessage(ChatColor.AQUA + "/" + cmd.getName() + " [" + StringUtils.join(info.aliases(), " | ").trim() + "] " + info.usage());
            return true;
        }

        if (!sender.hasPermission(permName + "." + wanted.getClass().getSimpleName().toLowerCase())) {
            MessageManager.getManager(sender).sendMessage(MessageManager.MessageType.BAD, "Insufficient permissions");
            return true;
        }

        wanted.onCommand(sender, args);

        return true;
    }

    public void addCommand(ExecutableCommand cmd) {
        this.cmds.add(cmd);
        Bukkit.getPluginManager().addPermission(new Permission(permName + "." + cmd.getClass().getSimpleName().toLowerCase()));
    }

    private void sendCommandList(CommandSender sender, Command cmd) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "-=-=-=-" + ChatColor.DARK_PURPLE + "[" + ChatColor.YELLOW + name + ChatColor.DARK_PURPLE + "]" + ChatColor.LIGHT_PURPLE + "-=-=-=-");
        for (ExecutableCommand gcmd : cmds) {
            CommandInfo info = gcmd.getClass().getAnnotation(CommandInfo.class);
            if (!info.hidden()) {
                sender.sendMessage(ChatColor.AQUA + "/" + cmd.getName() + " [" + StringUtils.join(info.aliases(), " | ").trim() + "] " + info.usage() + ChatColor.BLUE + " - " + info.description());
            }
        }
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "-=-=-=-=-=-=-=-=-=-=-=-");
    }
}
