package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.utils.ArenaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerQuitTeleport implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ArenaManager.INSTANCE.endGame(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        ArenaManager.INSTANCE.endGame(event.getPlayer());
    }
}
