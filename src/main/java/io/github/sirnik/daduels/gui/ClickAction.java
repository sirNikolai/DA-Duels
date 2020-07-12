package io.github.sirnik.daduels.gui;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ClickAction {
    void onClick(InventoryClickEvent event);
}
