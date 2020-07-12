package io.github.sirnik.daduels.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Menu {
    private Inventory i;

    private String name;
    private int size;
    private int page;

    private Map<ItemStack, ClickAction> items;

    public Menu(String name, int size) {
        this.name = name;

        if (size % 9 != 0) {
            Bukkit.getLogger().warning(name + " has an invalid size. Must be multiple of 9");
            size = 45;
        } else if (size > 45 || size < 9) {
            Bukkit.getLogger().warning(name + " has an invalid size. Must be max 45, minimum 9");
            size = 45;
        }

        //Adding a compulsory next page/last page
        this.size = size + 9;
        this.page = 0;

        this.items = new TreeMap<>(Comparator.comparing(i -> {
            if (!i.hasItemMeta())
                return "";

            return i.getItemMeta().getDisplayName();
        }));
        this.i = null;
    }

    public void switchItem(ItemStack remove, ItemStack putIn) {
        ClickAction action = items.remove(remove);

        if (action != null) {
            items.put(putIn, action);
        }
    }

    /**
     * Add item to gui using its existing ItemMeta
     *
     * @param i      Itemstack to be added
     * @param action Action when item is clicked
     */
    public void addItem(ItemStack i, ClickAction action) {
        items.put(i, action);
    }

    /**
     * Add item to gui
     *
     * @param i             Itemstack to be added
     * @param action        Action when item is clicked
     * @param enchantment   What enchantment to be set on item
     * @param silentEnchant If enchant should be seen or not when hovering
     * @param displayName   What the item will display as its name on hover
     * @param lore          What is displayed below the item name
     */
    public void addItem(ItemStack i, ClickAction action, Enchantment enchantment, boolean silentEnchant, String displayName, String... lore) {
        if (enchantment != null) {
            i.addUnsafeEnchantment(enchantment, 1);
        }

        ItemMeta meta = i.getItemMeta();

        if (silentEnchant) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        meta.setDisplayName(displayName);
        meta.setLore(Arrays.asList(lore));

        i.setItemMeta(meta);

        items.put(i, action);
    }

    public void onClick(InventoryClickEvent e) {
        int slot = e.getRawSlot();
        ItemStack i = e.getCurrentItem();

        if (slot == size - 1) {
            nextPage();
        } else if (slot == size - 9) {
            prevPage();
        } else if (slot < size - 9) {
            ClickAction action = items.get(i);

            if (action != null)
                action.onClick(e);
        }
    }

    /**
     * Opens menu for player
     *
     * @param pl Player who opened inventory
     */
    public void open(Player pl) {
        i = Bukkit.createInventory(null, size, name);

        renderPage();
        pl.openInventory(i);
    }

    /**
     * Attempts to move to the next page in pages
     */
    public void nextPage() {
        page++;

        if (!renderPage()) {
            page--;
        }
    }

    /**
     * Attempts to move backwards in pages
     */
    public void prevPage() {
        if (page == 0)
            return;

        page--;
        renderPage();
    }

    private boolean renderPage() {
        int size = i.getSize();

        //Set per page stuff
        int startIndex = (page == 0 ? 0 : (page * size) - 9);

        List<ItemStack> items = new ArrayList<>(this.items.keySet());

        //equal because index -1 on size
        //no more items to show
        if (startIndex >= items.size()) {
            return false;
        }

        i.clear();

        //Under setting for menu
        ItemStack nextPage = new ItemStack(Material.ARROW), prevPage = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPage.getItemMeta(), prevMeta = prevPage.getItemMeta();

        nextMeta.setDisplayName(ChatColor.DARK_GREEN + "[" + ChatColor.GREEN + "Next Page" + ChatColor.DARK_GREEN + "]");
        prevMeta.setDisplayName(ChatColor.GOLD + "[" + ChatColor.YELLOW + "Previous Page" + ChatColor.GOLD + "]");

        nextPage.setItemMeta(nextMeta);
        prevPage.setItemMeta(prevMeta);

        ItemStack yellowGlass = new ItemStack(Material.STAINED_GLASS_PANE), purpleGlass = new ItemStack(Material.STAINED_GLASS_PANE);
        ItemMeta yelloMeta = yellowGlass.getItemMeta(), purpleMeta = purpleGlass.getItemMeta();

        yelloMeta.setDisplayName(" ");
        purpleMeta.setDisplayName(" ");

        yellowGlass.setItemMeta(yelloMeta);
        purpleGlass.setItemMeta(purpleMeta);

        yellowGlass.setDurability((byte) 4);
        purpleGlass.setDurability((byte) 10);

        i.setItem(size - 1, nextPage);
        i.setItem(size - 9, prevPage);

        for (int slot = size - 2; slot > size - 9; slot--) {
            if (slot % 2 == 0) {
                i.setItem(slot, yellowGlass);
            } else {
                i.setItem(slot, purpleGlass);
            }
        }

        for (int slot = 0; slot < size - 9; slot++) {
            if (items.size() > slot + startIndex) {
                ItemStack item = items.get(slot + startIndex);
                i.addItem(item);
            } else {
                break;
            }
        }
        return true;
    }
}