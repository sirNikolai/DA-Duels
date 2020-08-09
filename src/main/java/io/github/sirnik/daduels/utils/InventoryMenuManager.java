package io.github.sirnik.daduels.utils;

import io.github.sirnik.daduels.gui.Menu;
import io.github.sirnik.daduels.models.DuelArena;
import io.github.sirnik.daduels.models.DuelSpell;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum InventoryMenuManager {
    INSTANCE;

    private final Map<UUID, Menu> inMenu;

    {
        inMenu = new HashMap<>();
    }

    /**
     * Opens menu for player.
     *
     * @param player Player who will open menu.
     */
    public void openMenu(Player player) {
        Menu menu = createMenu();

        inMenu.put(player.getUniqueId(), menu);

        menu.open(player);
    }

    /**
     * Returns active menu for player.
     *
     * @param player Player who has menu open.
     *
     * @return The menu a player has or null if one is not found.
     */
    public Menu getMenuForPlayer(Player player) {
        return inMenu.get(player.getUniqueId());
    }

    /**
     * Close menu for chosen player.
     *
     * @param player Player for whom to close menu.
     */
    public void closeMenu(Player player) {
        inMenu.remove(player.getUniqueId());
    }

    /**
     * Creates a new menu of arenas.
     *
     * @return The menu of updated arenas.
     */
    public Menu createMenu() {
        Menu menu = new Menu(
                ChatColor.DARK_PURPLE + "[" + ChatColor.YELLOW + "Arena List" + ChatColor.DARK_PURPLE + "]",
                45);

        for(DuelArena arena : ArenaManager.INSTANCE.getArenas()) {
            menu.addItem(createWool(arena), (InventoryClickEvent e) -> ArenaManager.INSTANCE.addPlayer(((Player) e.getWhoClicked()), arena));
        }

        return menu;
    }

    private ItemStack createWool(DuelArena arena) {
        ItemStack wool = null;
        List<String> lore = null;

        String players = Stream
                .of(arena.getPlayer1(), arena.getPlayer2())
                .filter(Objects::nonNull)
                .map(p -> p.getPlayer().getName())
                .collect(Collectors.joining(", "));

        String blacklistedSpells = arena.getBlackListedSpells().stream().map(DuelSpell::getSpellName).collect(Collectors.joining(", "));

        switch (arena.getCurrentState()) {
            case DISABLED:
                wool = new Wool(DyeColor.GRAY).toItemStack(1);
                lore = Collections.singletonList(ChatColor.DARK_RED + "Arena Closed");
                break;
            case OPEN:
                wool = new Wool(DyeColor.GREEN).toItemStack(1);
                lore = Arrays.asList(
                        ChatColor.DARK_GREEN + "Arena Open",
                        ChatColor.YELLOW + "==========",
                        ChatColor.GREEN + "Current Players:",
                        ChatColor.GREEN + players,
                        ChatColor.YELLOW + "==========",
                        ChatColor.RED + "Blacklisted Spells:",
                        ChatColor.GREEN + blacklistedSpells);
                break;
            case INGAME:
                wool = new Wool(DyeColor.RED).toItemStack(1);
                lore = Arrays.asList(
                        ChatColor.DARK_RED + "Game In Progress",
                        ChatColor.YELLOW + "==========",
                        ChatColor.GREEN + "Current Players:",
                        ChatColor.GREEN + players);
                break;
        }

        ItemMeta meta = wool.getItemMeta();

        meta.setDisplayName(ChatColor.YELLOW + arena.getName());
        meta.setLore(lore);

        wool.setItemMeta(meta);

        return wool;
    }
}
