package io.github.sirnik.daduels.utils;

import io.github.sirnik.daduels.database.Connector;
import io.github.sirnik.daduels.events.DuelMatchEndEvent;
import io.github.sirnik.daduels.events.DuelMatchStartEvent;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Arena Manager.
 *
 * All transactions with arenas and players should go through this class.
 */
public enum ArenaManager {
    INSTANCE;

    private Map<String, DuelArena> arenas;
    private Map<UUID, DuelArena> inGamePlayers;

    {
        arenas = new HashMap<>();
        inGamePlayers = new HashMap<>();
    }

    /**
     * Saves arena updates to database.
     *
     * @param arena Arena to be saved.
     */
    public void updateArena(DuelArena arena) {
        Connector.getInstance().saveArena(arena);
    }

    /**
     * Removes an existing arena.
     *
     * @param arena Arena to be removed
     */
    public void deleteArena(DuelArena arena) {
        arenas.remove(arena.getName().toUpperCase());
        Connector.getInstance().deleteArena(arena);
    }

    /**
     * Attempts to add arena to list.
     * If force is set to false, then if an arena already with said name exists, it will <i>not</i> be added.
     *
     * Side Effects:
     * <ul>
     *     <li>Attempts to enable arena</li>
     * </ul>
     *
     * @param arena Arena to be added.
     * @param force Whether to force add (overwrites previous arena with similar name).
     *
     * @return <b>true</b> if arena was added.
     */
    public boolean addArena(DuelArena arena, boolean force) {
        String name = arena.getName().toUpperCase();

        if(arenas.containsKey(name) && !force) {
            return false;
        }

        enableArena(arena);

        this.arenas.put(name, arena);
        return true;
    }

    /**
     * Gets all arenas in the game.
     *
     * @return all arenas in the game.
     */
    public Collection<DuelArena> getArenas() {
        return Collections.unmodifiableCollection(arenas.values());
    }

    /**
     * Gets arena with given name. <i>null</i> if nothing found.
     *
     * @param name Name of potential arena.
     *
     * @return Arena with said name.
     */
    public DuelArena getArena(String name) {
        return arenas.get(name.toUpperCase());
    }

    /**
     * Attempts to enable an arena. Returns <i>false</i> if it could not.
     * Possible reasons it could not enable the arena:
     * <ul>
     *     <li>Arena with given name not found</li>
     *     <li>Arena is currently mid game</li>
     *     <li>Arena has either player 1 spawn location and/or player 2 spawn location unset</li>
     * </ul>
     *
     * @param arena Arena to be enabled.
     *
     * @return <b>true</b> if arena was enabled.
     */
    public boolean enableArena(DuelArena arena) {
        if(arena == null) {
            return false;
        }

        if(arena.getCurrentState() == DuelState.INGAME) {
            return false;
        }

        if(arena.getPlayer1Location() == null || arena.getPlayer2Location() == null) {
            return false;
        }

        arena.setCurrentState(DuelState.OPEN);
        return true;
    }

    /**
     * Checks if player is in a game.
     * Returns <i>true</i> if a player is in an arena (arena can be {@link DuelState#INGAME} or {@link DuelState#OPEN}.
     *
     * @param player Player that is checked to be in game.
     *
     * @return <b>true</b> if player is in an arena.
     */
    public boolean isPlayerInGame(Player player) {
        return this.inGamePlayers.containsKey(player.getUniqueId());
    }

    /**
     * Get Arena for a given player.
     * <i>null</i> if player not in an arena.
     *
     * @param player Player that is checked for.
     *
     * @return Arena they are in. <b>null</b> if not in an arena.
     */
    public DuelArena getArenaForPlayer(Player player) {
        return this.inGamePlayers.getOrDefault(player.getUniqueId(), null);
    }

    /**
     * Disables an arena.
     * If match is ongoing then declares match stalemate and ends it.
     *
     * @param arena arena to be disabled
     *
     * @return <b>true</b> if arena was disabled. <b>false</b> is only when arena was not found.
     */
    public boolean disableArena(DuelArena arena) {
        if(arena == null) {
            return false;
        }

        this.endGame(arena);
        arena.setCurrentState(DuelState.DISABLED);

        return true;
    }

    /**
     * Starts the specified arena.
     * Arena must <i>not</i> be null.
     *
     * Side Effects:
     * <ul>
     *     <li>Initiates {@link DuelMatchStartEvent}</li>
     * </ul>
     *
     * @param arena Arena to be started
     */
    public void startGame(DuelArena arena) {
        Bukkit.getPluginManager().callEvent(new DuelMatchStartEvent(arena.getPlayer1(), arena.getPlayer2(), arena));
        arena.startGame();
    }

    /**
     * Ends game for the arena with specified player.
     * This will declare the player the loser of the battle.
     *
     * Side Effects:
     * <ul>
     *     <li>Initiates {@link DuelMatchEndEvent}</li>
     * </ul>
     *
     * @param loser Losing player.
     */
    public void endGame(Player loser) {
        DuelArena arena = this.inGamePlayers.get(loser.getUniqueId());

        if(arena == null) {
            return;
        }

        Player winner = arena.getPlayer1().getUniqueId().equals(loser.getUniqueId())
                ? arena.getPlayer2()
                : arena.getPlayer1();

        Bukkit.getPluginManager().callEvent(new DuelMatchEndEvent(winner, loser, arena));
        this.endGame(arena);
    }

    /**
     * Ends the game by specifying an arena.
     * <i>NOTE:</i> this should be triggered when there is no winner in an arena.
     *
     * @param arena Arena that is to reset.
     */
    public void endGame(DuelArena arena) {
        if(arena == null) {
            return;
        }

        inGamePlayers.remove(arena.getPlayer1().getUniqueId());
        inGamePlayers.remove(arena.getPlayer2().getUniqueId());
        
        arena.endGame();
    }

    /**
     * Removes player from arena.
     * If arena is in state {@link DuelState#INGAME} then removed player is declared the loser.
     *
     * @param player Player to be removed from arena.
     *
     * @return <b>true</b> if player was removed from an arena. <b>false</b> if the are not in an arena.
     */
    public boolean removePlayer(Player player) {
        DuelArena arena = inGamePlayers.remove(player.getUniqueId());

        if(arena != null) {
            if(arena.getCurrentState() == DuelState.INGAME) {
                this.endGame(player);
            } else {
                arena.removePlayer(player);
            }

            return true;
        }

        return false;
    }

    /**
     * Adds a player to an arena.
     * Returns <i>true</i> if it was added.
     * <i>false</i> is returned under the following conditions:
     * <ul>
     *     <li>arena is null</li>
     *     <li>arena is full</li>
     *     <li>player is already in an arena</li>
     * </ul>
     *
     * @param player Player to be added.
     * @param arena Arena to which the player should be added.
     *
     * @return if the player was added to arena.
     */
    public boolean addPlayer(Player player, DuelArena arena) {
        if(arena == null || this.inGamePlayers.containsKey(player.getUniqueId())) {
            return false;
        }

        if(arena.addPlayer(player)) {
            this.inGamePlayers.put(player.getUniqueId(), arena);
            return true;
        }

        return false;
    }
}
