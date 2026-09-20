package me.nik.advancedstaff.listeners;

import me.nik.advancedstaff.AdvancedStaff;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

public class InventoryListener implements Listener {

    private final AdvancedStaff plugin;

    public InventoryListener(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (isInspectionInventory(e)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (isInspectionInventory(e)) {
            e.setCancelled(true);
        }
    }

    private boolean isInspectionInventory(InventoryInteractEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return false;
        }

        return plugin.getInventoryManager().isInspectionInventory(e.getView());
    }
}