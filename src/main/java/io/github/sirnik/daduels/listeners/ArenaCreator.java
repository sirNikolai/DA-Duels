package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.utils.ArenaCreatorManager;
import io.github.sirnik.daduels.utils.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ArenaCreator implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() == null) {
            return;
        }

        if(e.getPlayer().getInventory().getItemInMainHand().getType() != ArenaCreatorManager.INSTANCE.getToolType()) {
            return;
        }

        if(ArenaCreatorManager.INSTANCE.addSpawnPoint(e.getPlayer(), e.getClickedBlock().getLocation(), e.getAction())) {
            e.setCancelled(true);

            MessageManager.getManager(e.getPlayer()).sendMessage(MessageManager.MessageType.GOOD, "Point set.");

            MessageManager.getManager(e.getPlayer()).sendMessage(
                    MessageManager.MessageType.NEUTRAL,
                    "Remember to save after finishing.");
        }
    }
}
