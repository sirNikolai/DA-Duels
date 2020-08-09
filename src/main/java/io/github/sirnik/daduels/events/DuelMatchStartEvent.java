package io.github.sirnik.daduels.events;

import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a match starts.
 * This is when the second player joins the arena.
 */
public class DuelMatchStartEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private DuelPlayer player1;
    private DuelPlayer player2;
    private DuelArena duelArena;

    public DuelMatchStartEvent(DuelPlayer player1, DuelPlayer player2, DuelArena duelArena) {
        this.player1 = player1;
        this.player2 = player2;
        this.duelArena = duelArena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public DuelPlayer getPlayer1() {
        return player1;
    }

    public DuelPlayer getPlayer2() {
        return player2;
    }

    public DuelArena getDuelArena() {
        return duelArena;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
