package io.github.sirnik.daduels.models;

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

    private DuelPlayer player1;

    private DuelPlayer player2;

    private long startTime;


    public DuelArena(String name) {
        this.startTime = 0L;
        this.index = -1;
        this.name = name;
        this.currentState = DuelState.DISABLED;
        this.blackListedSpells = new HashSet<>();
    }

    /**
     * Attempts to get {@linkplain DuelPlayer} container for a provided player.
     *
     * @param pl Player for whom to get representing {@linkplain DuelPlayer} object.
     *
     * @return {@linkplain DuelPlayer} container for target player. <b>null</b> if player container is not found.
     */
    public DuelPlayer getDuelPlayer(Player pl) {
        if(player1 != null && player1.getPlayer().getUniqueId().equals(pl.getUniqueId())) {
            return player1;
        }

        if(player2 != null && player2.getPlayer().getUniqueId().equals(pl.getUniqueId())) {
            return player2;
        }

        return null;
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
    }

    /**
     * Ends the game.
     * Clears players, arena state, and resets win counts.
     */
    public void endGame() {
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
        if(player1 != null && player.getUniqueId().equals(player1.getPlayer().getUniqueId())) {
            player1 = null;
            return;
        }

        if(player2 != null && player.getUniqueId().equals(player2.getPlayer().getUniqueId())) {
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
     * @return <b>null</b> if a player could not be added. Otherwise returns the created {@linkplain DuelPlayer} container for said player.
     */
    public DuelPlayer addPlayer(Player player) {
        if(this.currentState != DuelState.OPEN) {
            return null;
        }

        if(player1 == null) {
            player1 = new DuelPlayer(player, player1Location);
            Bukkit.getPluginManager().callEvent(new DuelMatchJoinEvent(player, this));
            return player1;
        }

        if(player.getUniqueId().equals(player1.getPlayer().getUniqueId())) {
            return null;
        }

        if(player2 == null) {
            player2 = new DuelPlayer(player, player2Location);
            Bukkit.getPluginManager().callEvent(new DuelMatchJoinEvent(player, this));
            return player2;
        }

        return null;
    }

    /**
     * Increment win for a target player.
     *
     * @param player Player for whom to record the win.
     *
     * @return new win count for target player.
     */
    public int addWin(Player player) {
        if(player.getUniqueId().equals(player1.getPlayer().getUniqueId())) {
            return player1.addWin();
        }

        return player2.addWin();
    }

    // Getters and Setters //

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public DuelPlayer getPlayer1() {
        return player1;
    }

    public DuelPlayer getPlayer2() {
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
