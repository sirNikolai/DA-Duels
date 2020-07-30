package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.utils.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ArenaManager.INSTANCE.removePlayer(event.getPlayer());
    }
}
