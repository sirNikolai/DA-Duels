package io.github.sirnik.daduels.models;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Container for data in a duel arena
 */
public class DuelArena {

    private String name;

    private Location player1Location;

    private Location player2Location;

    private DuelState currentState;

    private Player player1;

    private Player player2;

    public DuelArena(String name) {
        this.name = name;
        this.currentState = DuelState.DISABLED;
    }

    /**
     * Returns if an arena is full.
     *
     * @return <b>true</b> if both players are present.
     */
    public boolean isFull() {
        return player1 != null && player2 != null;
    }

    /**
     * Starts the game.
     * Sets arena state to in game, teleports the players to their correct start location.
     */
    public void startGame() {
        currentState = DuelState.INGAME;

        player1.teleport(player1Location);
        player2.teleport(player2Location);
    }

    /**
     * Ends the game.
     * Opens arena for new users and teleports current players to spawn location in given world.
     */
    public void endGame() {
        if(player1 != null) {
            player1.teleport(player1Location.getWorld().getSpawnLocation());
        }

        if(player2 != null) {
            player2.teleport(player2Location.getWorld().getSpawnLocation());
        }

        player1 = null;
        player2 = null;

        currentState = DuelState.OPEN;
    }

    /**
     * Attempts to remove a player from a game
     *
     * @param player Player to be removed
     */
    public void removePlayer(Player player) {
        if(player1 != null && player.getUniqueId().equals(player1.getUniqueId())) {
            player1 = null;
            return;
        }

        if(player2 != null && player.getUniqueId().equals(player2.getUniqueId())) {
            player2 = null;
        }
    }

    /**
     * Attempt to add player to arena.
     *
     * @param player Player to be added.
     *
     * @return <b>false</b> if player cannot be added to arena if full or arena disabled. <b>true</b> if added as player 1 or player 2.
     */
    public boolean addPlayer(Player player) {
        if(this.currentState != DuelState.OPEN) {
            return false;
        }

        if(player1 == null) {
            player1 = player;
            return true;
        }

        if(player2 == null) {
            player2 = player;
            return true;
        }

        return false;
    }

    // Getters and Setters //
    public String getName() {
        return name;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Location getPlayer1Location() {
        return player1Location;
    }

    public void setPlayer1Location(Location player1Location) {
        this.player1Location = player1Location;
    }

    public Location getPlayer2Location() {
        return player2Location;
    }

    public void setPlayer2Location(Location player2Location) {
        this.player2Location = player2Location;
    }

    public DuelState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(DuelState currentState) {
        this.currentState = currentState;
    }
}
