package io.github.sirnik.daduels.models;

import io.github.nikmang.daspells.spells.Spell;
import io.github.sirnik.daduels.events.DuelMatchJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Container for data in a duel arena
 */
public class DuelArena {

    private long index;

    private String name;

    private Location player1Location;

    private Location player2Location;

    private DuelState currentState;

    private Set<DuelSpell> blackListedSpells;

    private Player player1;

    private Player player2;

    public DuelArena(String name) {
        this.index = -1;
        this.name = name;
        this.currentState = DuelState.DISABLED;
        this.blackListedSpells = new HashSet<>();
    }

    /**
     * Remove spell from blacklist.
     *
     * @param spell Spell to be removed from blacklist.
     */
    public void removeSpellFromBlackList(DuelSpell spell) {
        blackListedSpells.remove(spell);
    }

    /**
     * Add spell to blacklist.
     *
     * @param spell Spell to be added to blacklist.
     */
    public void addSpellToBlacklist(DuelSpell spell) {
        blackListedSpells.add(spell);
    }

    /**
     * Checks if spell is blacklisted in the arena.
     *
     * @param spellName Spell name to be checked. Case insensitive
     *
     * @return <b>true</b> if spell is blacklisted in the arena.
     */
    public boolean isSpellBlackListed(String spellName) {
        return blackListedSpells.contains(new DuelSpell(spellName));
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
     * Side Effects:
     * <ul>
     *     <li>Calls {@linkplain DuelMatchJoinEvent}</li>
     * </ul>
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
            Bukkit.getPluginManager().callEvent(new DuelMatchJoinEvent(player, this));
            return true;
        }

        if(player.getUniqueId().equals(player1.getUniqueId())) {
            return false;
        }

        if(player2 == null) {
            player2 = player;
            Bukkit.getPluginManager().callEvent(new DuelMatchJoinEvent(player, this));
            return true;
        }

        return false;
    }

    // Getters and Setters //

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

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

    public Set<DuelSpell> getBlackListedSpells() {
        return Collections.unmodifiableSet(blackListedSpells);
    }
}
