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
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;

public class MatchEvents implements Listener {

    @EventHandler
    public void onStart(DuelMatchStartEvent e) {
        for(PotionEffect effect : e.getPlayer1().getActivePotionEffects()) {
            e.getPlayer1().removePotionEffect(effect.getType());
        }

        for(PotionEffect effect : e.getPlayer2().getActivePotionEffects()) {
            e.getPlayer2().removePotionEffect(effect.getType());
        }

        e.getPlayer1().setGameMode(GameMode.SURVIVAL);
        e.getPlayer2().setGameMode(GameMode.SURVIVAL);

        e.getPlayer1().setHealth(e.getPlayer1().getHealthScale());
        e.getPlayer2().setHealth(e.getPlayer2().getHealthScale());

        e.getPlayer1().teleport(e.getDuelArena().getPlayer1Location());
        e.getPlayer2().teleport(e.getDuelArena().getPlayer2Location());

        String startString = String.format(
                "%s Round: %d/%d",
                ChatColor.YELLOW,
                e.getDuelArena().getPlayer1Wins() + e.getDuelArena().getPlayer2Wins() + 1,
                ArenaManager.INSTANCE.getWinTarget() * 2 - 1);

        e.getPlayer1().sendTitle(ChatColor.GREEN + "En Garde!", startString, 10, 70, 20);
        e.getPlayer2().sendTitle(ChatColor.GREEN + "En Garde!", startString, 10, 70, 20);

        e.getDuelArena().setStartTime(System.currentTimeMillis());
    }

    @EventHandler
    public void onEnd(DuelMatchEndEvent e) {
        e.getWinner().setHealth(e.getWinner().getHealthScale());
        e.getLoser().setHealth(e.getLoser().getHealthScale());

        e.getWinner().sendTitle(ChatColor.GREEN + "Winner!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);
        e.getLoser().sendTitle(ChatColor.RED + "Loser!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);

        e.getWinner().teleport(e.getWinner().getWorld().getSpawnLocation());
        e.getLoser().teleport(e.getLoser().getWorld().getSpawnLocation());

        Bukkit.getScheduler().runTaskAsynchronously(DADuels.getInstance(), () -> {
            try {
                ApiQuerier.recordResult(e.getWinner().getUniqueId(), e.getLoser().getUniqueId());
            } catch (IOException ex) {
                MessageManager.getManager(e.getLoser(), e.getWinner()).sendMessage(
                        MessageManager.MessageType.BAD,
                        "Save failed to site. Please send screenshot to sirNik: " + e.getWinner().getUniqueId() + " " + e.getLoser().getUniqueId());
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
