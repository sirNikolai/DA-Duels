package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.events.DuelMatchEndEvent;
import io.github.sirnik.daduels.events.DuelMatchStartEvent;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.utils.ArenaManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        DuelArena duelArena = ArenaManager.INSTANCE.getArenaForPlayer(player);


        if(duelArena == null) {
            return;
        }

        Player winner = player.getUniqueId().equals(duelArena.getPlayer1().getUniqueId())
                ? duelArena.getPlayer2()
                : duelArena.getPlayer1();

        if(event.getFinalDamage() >= player.getHealth()) {
            event.setCancelled(true);
            if(ArenaManager.INSTANCE.endBattle(winner, player)) {
                ArenaManager.INSTANCE.endGame(player);
                Bukkit.getPluginManager().callEvent(new DuelMatchEndEvent(winner, player, duelArena));
            } else{
                MessageManager.getManager(duelArena.getPlayer1(), duelArena.getPlayer2()).sendMessage(MessageManager.MessageType.NEUTRAL, winner.getName() + " wins the round!");
                Bukkit.getPluginManager().callEvent(new DuelMatchStartEvent(duelArena.getPlayer1(), duelArena.getPlayer2(), duelArena));
            }
        }
    }
}
