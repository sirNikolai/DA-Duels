package io.github.sirnik.daduels.events;

import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when a match finishes
 */
public class DuelMatchEndEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private DuelPlayer winner;
    private DuelPlayer loser;
    private DuelArena duelArena;

    public DuelMatchEndEvent(DuelPlayer winner, DuelPlayer loser, DuelArena duelArena) {
        this.winner = winner;
        this.loser = loser;
        this.duelArena = duelArena;
    }

    public DuelPlayer getWinner() {
        return winner;
    }

    public DuelPlayer getLoser() {
        return loser;
    }

    public DuelArena getDuelArena() {
        return duelArena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
