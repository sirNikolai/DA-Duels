package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.DADuels;
import io.github.sirnik.daduels.events.DuelMatchEndEvent;
import io.github.sirnik.daduels.events.DuelMatchJoinEvent;
import io.github.sirnik.daduels.events.DuelMatchStartEvent;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import io.github.sirnik.daduels.website.ApiQuerier;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class MatchEvents implements Listener {

    @EventHandler
    public void onStart(DuelMatchStartEvent e) {
        e.getPlayer1().resetPlayerStats();
        e.getPlayer2().resetPlayerStats();

        e.getPlayer1().getPlayer().teleport(e.getPlayer1().getSpawnLocation());
        e.getPlayer2().getPlayer().teleport(e.getPlayer2().getSpawnLocation());

        String startString = String.format(
                "%s Round: %d/%d",
                ChatColor.YELLOW,
                e.getDuelArena().getPlayer1().getWins() + e.getDuelArena().getPlayer2().getWins() + 1,
                ArenaManager.INSTANCE.getWinTarget() * 2 - 1);

        e.getPlayer1().getPlayer().sendTitle(ChatColor.GREEN + "En Garde!", startString, 10, 70, 20);
        e.getPlayer2().getPlayer().sendTitle(ChatColor.GREEN + "En Garde!", startString, 10, 70, 20);

        e.getDuelArena().setStartTime(System.currentTimeMillis());
    }

    @EventHandler
    public void onEnd(DuelMatchEndEvent e) {
        Player winner = e.getWinner().getPlayer();
        Player loser = e.getLoser().getPlayer();

        e.getWinner().resetPlayerStats();
        e.getLoser().resetPlayerStats();

        e.getWinner().getPlayer().setScoreboard(e.getWinner().getExistingScoreboard());
        e.getLoser().getPlayer().setScoreboard(e.getLoser().getExistingScoreboard());

        winner.sendTitle(ChatColor.GREEN + "Winner!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);
        loser.sendTitle(ChatColor.RED + "Loser!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);

        winner.teleport(winner.getWorld().getSpawnLocation());
        loser.teleport(loser.getWorld().getSpawnLocation());

        Bukkit.getScheduler().runTaskAsynchronously(DADuels.getInstance(), () -> {
            try {
                ApiQuerier.recordResult(winner.getUniqueId(), loser.getUniqueId());
            } catch (IOException ex) {
                MessageManager.getManager(loser, winner).sendMessage(
                        MessageManager.MessageType.BAD,
                        "Save failed to site. Please send screenshot to sirNik: " + winner.getUniqueId() + " " + loser.getUniqueId());
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onJoinArena(DuelMatchJoinEvent event) {
        event.getPlayer().closeInventory();

        if(event.getArena().isFull()) {
            ArenaManager.INSTANCE.startGame(event.getArena());
        }
    }
}
