package me.nik.advancedstaff.managers;

import me.nik.advancedstaff.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryManager implements AbstractManager {

    public void openPlayerInventory(Player player, Player target) {
        if (player == null || target == null) {
            return;
        }

        Inventory inventory = Bukkit.createInventory(
                null,
                54,
                ChatUtils.format("&9Inventory: " + target.getName())
        );

        /*
         * Copy the target's normal inventory.
         */
        ItemStack[] contents = target.getInventory().getContents();

        for (int i = 0; i < 36 && i < contents.length; i++) {

            ItemStack item = contents[i];

            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            inventory.setItem(i, item.clone());
        }

        ItemStack[] armor = target.getInventory().getArmorContents();

        if (armor.length > 0) {
            setInspectionItem(inventory, 48, armor[0]);
        }

        if (armor.length > 1) {
            setInspectionItem(inventory, 47, armor[1]);
        }

        if (armor.length > 2) {
            setInspectionItem(inventory, 46, armor[2]);
        }

        if (armor.length > 3) {
            setInspectionItem(inventory, 45, armor[3]);
        }

        player.closeInventory();
        player.openInventory(inventory);
    }

    private void setInspectionItem(Inventory inventory, int slot, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        inventory.setItem(slot, item.clone());
    }

    public boolean isInspectionInventory(InventoryView inventory) {
        if (inventory == null) {
            return false;
        }

        String title = inventory.getTitle();

        return title != null && title.startsWith(ChatUtils.format("&9Inventory:"));
    }

    @Override
    public void initialize() {
    }

    @Override
    public void shutdown() {
    }
}