package io.github.sirnik.daduels.events;

import io.github.sirnik.daduels.models.DuelArena;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class DuelMatchJoinEvent extends PlayerEvent {

    private static HandlerList handlers = new HandlerList();

    private DuelArena arena;

    public DuelMatchJoinEvent(Player who, DuelArena arena) {
        super(who);

        this.arena = arena;
    }

    public DuelArena getArena() {
        return arena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
