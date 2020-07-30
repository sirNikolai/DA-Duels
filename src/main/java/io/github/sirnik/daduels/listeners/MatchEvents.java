package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.DADuels;
import io.github.sirnik.daduels.events.DuelMatchEndEvent;
import io.github.sirnik.daduels.events.DuelMatchJoinEvent;
import io.github.sirnik.daduels.events.DuelMatchStartEvent;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.website.ApiQuerier;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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

        e.getPlayer1().setHealth(e.getPlayer1().getHealthScale());
        e.getPlayer2().setHealth(e.getPlayer2().getHealthScale());

        e.getPlayer1().sendTitle(ChatColor.GREEN + "En Garde!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);
        e.getPlayer2().sendTitle(ChatColor.GREEN + "En Garde!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);
    }

    @EventHandler
    public void onEnd(DuelMatchEndEvent e) {
        e.getWinner().setHealth(e.getWinner().getHealthScale());
        e.getLoser().setHealth(e.getLoser().getHealthScale());

        e.getWinner().sendTitle(ChatColor.GREEN + "Winner!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);
        e.getLoser().sendTitle(ChatColor.RED + "Loser!", ChatColor.YELLOW + "Arena: " + e.getDuelArena().getName(), 10, 70, 20);

        Bukkit.getScheduler().runTaskAsynchronously(DADuels.getInstance(), () -> {
            try {
                ApiQuerier.recordResult(e.getWinner().getUniqueId(), e.getLoser().getUniqueId());
            } catch (IOException ex) {
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
