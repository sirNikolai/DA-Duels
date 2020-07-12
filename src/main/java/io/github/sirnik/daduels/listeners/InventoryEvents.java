package io.github.sirnik.daduels.listeners;

import io.github.sirnik.daduels.gui.Menu;
import io.github.sirnik.daduels.utils.InventoryMenuManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryEvents implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Menu menu = InventoryMenuManager.INSTANCE.getMenuForPlayer((Player) event.getWhoClicked());

        if(menu == null) {
            return;
        }

        if(event.getClickedInventory() == null || event.getCurrentItem() == null)
            return;

        menu.onClick(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player)) {
            return;
        }

        InventoryMenuManager.INSTANCE.closeMenu((Player) event.getPlayer());
    }
}
