package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.utils.ArenaManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if(!ArenaManager.INSTANCE.isPlayerInGame(player)) {
            return;
        }

        if(event.getFinalDamage() >= player.getHealth()) {
            event.setCancelled(true);
            ArenaManager.INSTANCE.endGame(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onParticularDamage(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();

        if(ArenaManager.INSTANCE.isPlayerInGame(player)) {
            event.setCancelled(true);
        }
    }
}
