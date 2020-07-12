package io.github.sirnik.daduels.utils;

import io.github.sirnik.daduels.models.DuelArena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public enum ArenaCreatorManager {
    INSTANCE;

    private final ItemStack EDITOR_TOOL;

    private Map<UUID, DuelArena> creators;

    {
        EDITOR_TOOL = new ItemStack(Material.GOLD_HOE);
        ItemMeta goalMeta = EDITOR_TOOL.getItemMeta();
        List<String> lore = new ArrayList<>();

        goalMeta.setDisplayName(ChatColor.YELLOW + "Spawn Selector");

        lore.add(ChatColor.AQUA + "Left Click to select player 1 spawn");
        lore.add(ChatColor.AQUA + "Right Click to select player 2 spawn");

        goalMeta.setLore(lore);
        EDITOR_TOOL.setItemMeta(goalMeta);

        creators = new HashMap<>();
    }

    /**
     * Gets arena that player is currently modifying.
     *
     * @param player Player that is modifying.
     *
     * @return Arena selected, <b>null</b> if there is no arena.
     */
    public DuelArena getArenaforCreator(Player player) {
        return creators.get(player.getUniqueId());
    }

    /**
     * Gets the material of the tool.
     *
     * @return Material of tool used.
     */
    public Material getToolType() {
        return EDITOR_TOOL.getType();
    }

    /**
     * Adds a player as creator for new arena.
     * Also gives player the item to add spawn points.
     *
     * @param player Player to be added.
     * @param arena Arena to be created or modified.
     *
     * @return <b>true</b> if player was added. <b>false</b> if player is already modifying a different arena.
     */
    public boolean addCreator(Player player, DuelArena arena) {
        if(creators.containsKey(player.getUniqueId())) {
            return false;
        }

        creators.put(player.getUniqueId(), arena);
        player.getInventory().addItem(EDITOR_TOOL.clone());
        return true;
    }

    /**
     * Removes player from creators list.
     *
     * @param player Player to be removed.
     */
    public void removeCreator(Player player) {
        this.creators.remove(player.getUniqueId());
    }

    /**
     * Attempts to add a spawn point.
     * Will return false if no arena found for player.
     *
     * @param player Player adding point.
     * @param spawnPoint The location where the spawn will be.
     * @param action The action type. {@link Action#LEFT_CLICK_BLOCK} is for player 1 spawn, {@link Action#RIGHT_CLICK_BLOCK} for player 2.
     *
     * @return <b>true</b> if spawn point is added.
     */
    public boolean addSpawnPoint(Player player, Location spawnPoint, Action action) {
        DuelArena arena = this.creators.get(player.getUniqueId());

        if(arena == null) {
            return false;
        }

        if(action == Action.LEFT_CLICK_BLOCK) {
            arena.setPlayer1Location(spawnPoint);
            return true;
        } else if(action == Action.RIGHT_CLICK_BLOCK) {
            arena.setPlayer2Location(spawnPoint);
            return true;
        }

        return false;
    }
}
