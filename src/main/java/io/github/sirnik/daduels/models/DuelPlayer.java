package io.github.sirnik.daduels.models;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Container for a dueller.
 */
public class DuelPlayer {
    private static final Scoreboard HEALTH_BOARD = createHealthScoreboard();

    private Player player;

    private Scoreboard existingScoreboard;

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
        this.existingScoreboard = player.getScoreboard();
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
     *   <li>Sets scoreboard to show health.</li>
     * </ul>
     */
    public void resetPlayerStats() {
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setFireTicks(0);

        player.setScoreboard(HEALTH_BOARD);
        player.setHealth(player.getHealthScale());
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

    public Scoreboard getExistingScoreboard() {
        return existingScoreboard;
    }

    private static Scoreboard createHealthScoreboard() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("showhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName(ChatColor.DARK_RED + "❤");

        return scoreboard;
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
