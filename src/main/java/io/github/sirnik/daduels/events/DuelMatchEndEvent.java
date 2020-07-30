package io.github.sirnik.daduels.events;

import io.github.sirnik.daduels.models.DuelArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchEndEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private Player winner;
    private Player loser;
    private DuelArena duelArena;

    public DuelMatchEndEvent(Player winner, Player loser, DuelArena duelArena) {
        this.winner = winner;
        this.loser = loser;
        this.duelArena = duelArena;
    }

    public Player getWinner() {
        return winner;
    }

    public Player getLoser() {
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
