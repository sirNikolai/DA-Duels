package io.github.sirnik.daduels.models;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Container for a dueller.
 */
public class DuelPlayer {

    private Player player;

    private int wins;

    private Location spawnLocation;

    private long lastCast;

    /**
     * Constructor for player
     *
     * @param player Player for who this applies for.
     * @param spawnLocation The location in the arena where player will spawn.
     */
    public DuelPlayer(Player player, Location spawnLocation) {
        this.player = player;
        this.spawnLocation = spawnLocation;
        this.lastCast = 0L;
        this.wins = 0;
    }

    /**
     * Prepares a player for duels.
     *
     * This does the following:
     * <ul>
     *   <li>This sets game mode to {@linkplain GameMode#SURVIVAL}.</li>
     *   <li>Removes all potion effects.</li>
     *   <li>Extinguishes flames from player.</li>
     *   <li>Sets player back to max health.</li>
     *   <li>Teleports player to their start location.</li>
     * </ul>
     */
    public void preparePlayer() {
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setFireTicks(0);
        player.setHealth(player.getHealthScale());

        player.teleport(spawnLocation);
    }

    /**
     * Increments win amount.
     *
     * @return New win count
     */
    public int addWin() {
        return ++wins;
    }

    // Setters and Getters //
    public void setLastCast(long lastCast) {
        this.lastCast = lastCast;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWins() {
        return wins;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public long getLastCast() {
        return lastCast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DuelPlayer that = (DuelPlayer) o;

        if(that.getPlayer() == null) return false;

        return player.getUniqueId().equals(that.player.getUniqueId());
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }
}
