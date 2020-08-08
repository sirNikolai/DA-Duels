package io.github.sirnik.daduels.utils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Manager that sends messages to players from set plugin
 */
public class MessageManager {

    private static MessageManager messageManager;

    private String name;
    private CommandSender[] receps;

    private MessageManager() {
        name = "";
    }

    /**
     * Get the messagemanager from a cache or create a new one. Prefix is created from the name of the main file
     *
     * @param recipients The conversables that receive message
     * @param <T> Type of recipient (typically either console sender or player)
     * @return instance of MessageManager
     */
    @SafeVarargs
    public static synchronized <T extends CommandSender> MessageManager getManager(T... recipients) {
        if (messageManager == null) {
            messageManager = new MessageManager();
            messageManager.name = ChatColor.DARK_PURPLE + "[" + ChatColor.YELLOW + "DADuels" + ChatColor.DARK_PURPLE + "] ";
        }

        messageManager.receps = recipients;
        return messageManager;
    }

    public void setPrefix(String prefix) {
        this.name = ChatColor.DARK_PURPLE + "[" + ChatColor.YELLOW + prefix + ChatColor.DARK_PURPLE + "] ";
    }

    /**
     * Explicitly changes the name of the command prefix
     *
     * @param name Name to be given to the prefix of the command
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sends a message to a recipient
     *
     * @param type   Type of message, see {@link MessageType}
     * @param string The message body
     */
    public void sendMessage(MessageType type, String string) {
        for (CommandSender recep : receps) {
            recep.sendMessage(name + type.getColour() + string);
        }
    }

    /**
     * Sends a JSON message to players for class info with cmd to click and hover to show message to hover
     * Note: Conversable recipient(s) must be of type player
     *
     * @param messagetype Message colour
     * @param msg         The message body itself
     * @param hover       Message that is shown on hover
     * @param cmd         Command that is run on click
     */
    public void sendHoverMessage(MessageType messagetype, String msg, String hover, String cmd) {
        TextComponent message = new TextComponent(name + messagetype.getColour() + msg);

        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

        for (CommandSender c : receps) {
            if (c instanceof Player) {
                ((Player) c).spigot().sendMessage(message);
            }
        }
    }

    /**
     * Sends a JSON message to players for class info with cmd to click and hover to show message to hover.
     * This method differs from {@linkplain #sendHoverMessage(MessageType, String, String, String)} by not sending the plugin prefix
     * Note: Conversable recipient(s) must be of type player
     *
     * @param messagetype Message colour
     * @param msg         The message body itself
     * @param hover       Message that is shown on hover
     * @param cmd         Command that is run on click
     */
    public void sendHoverMessageHeadless(MessageType messagetype, String msg, String hover, String cmd) {
        TextComponent message = new TextComponent(messagetype.getColour() + msg);

        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));

        for (CommandSender c : receps) {
            if (c instanceof Player) {
                ((Player) c).spigot().sendMessage(message);
            }
        }
    }

    /**
     * Provides mood types for message sending.
     */
    public enum MessageType {

        GOOD(ChatColor.GREEN),
        BAD(ChatColor.RED),
        NEUTRAL(ChatColor.YELLOW);

        MessageType(ChatColor colour) {
            this.colour = colour;
        }

        private ChatColor colour;

        private ChatColor getColour() {
            return this.colour;
        }
    }
}
