package io.github.sirnik.daduels.events;

import io.github.sirnik.daduels.models.DuelArena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DuelMatchStartEvent extends Event {
    private static HandlerList handlers = new HandlerList();

    private Player player1;
    private Player player2;
    private DuelArena duelArena;

    public DuelMatchStartEvent(Player player1, Player player2, DuelArena duelArena) {
        this.player1 = player1;
        this.player2 = player2;
        this.duelArena = duelArena;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public DuelArena getDuelArena() {
        return duelArena;
    }
}
