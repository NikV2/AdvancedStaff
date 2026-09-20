package me.nik.advancedstaff.gui;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu implements InventoryHolder {

    protected final AdvancedStaff plugin;

    protected Inventory inventory;

    protected PlayerMenu playerMenu;

    public Menu(PlayerMenu playerMenu, AdvancedStaff plugin) {
        this.playerMenu = playerMenu;
        this.plugin = plugin;
    }

    protected abstract String getMenuName();

    protected abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    protected abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setMenuItems();

        playerMenu.getOwner().openInventory(inventory);
    }

    protected ItemStack makeHead(String player, String displayName, List<String> lore) {

        ItemStack item;

        Material material = Material.getMaterial("SKULL_ITEM");

        if (material == null) {

            item = new ItemStack(Material.PLAYER_HEAD);

        } else item = new ItemStack(material, 1, (byte) 3);

        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();

        try {
            itemMeta.setOwner(player);
        } catch (NullPointerException ignored) {
        }

        itemMeta.setDisplayName(ChatUtils.format(displayName));

        if (lore != null) {

            List<String> loreList = new ArrayList<>();

            for (String l : lore) {

                loreList.add(ChatUtils.format(l));
            }

            itemMeta.setLore(loreList);
        }

        item.setItemMeta(itemMeta);

        return item;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}