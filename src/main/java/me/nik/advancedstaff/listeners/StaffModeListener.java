package me.nik.advancedstaff.listeners;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.managers.StaffModeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StaffModeListener implements Listener {

    private final AdvancedStaff plugin;

    public StaffModeListener(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    public void teleportToRandomPlayer(Player staff) {
        if (staff == null) {
            return;
        }

        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {

            if (player == null || player.getUniqueId().equals(staff.getUniqueId())) {
                continue;
            }

            if (Config.Setting.STAFFMODE_PREVENT_TELEPORTING_TO_OTHER_STAFF.getBoolean() && player.hasPermission(Permissions.ADMIN.getPermission())) {
                continue;
            }

            players.add(player);
        }

        if (players.isEmpty()) return;

        Player target = players.get(ThreadLocalRandom.current().nextInt(players.size()));

        staff.teleport(target.getLocation());

        staff.sendMessage(MsgType.TELEPORTED.getMessage().replace("%player%", target.getName()));
    }

    @EventHandler
    public void onInventory(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        if (Config.Setting.STAFFMODE_PREVENT_INTERACTING_WITH_STAFF_ITEMS.getBoolean()) {

            Player player = (Player) e.getWhoClicked();

            if (this.plugin.getStaffModeManager().isInStaffMode(player)) {

                //These are the slots we put our staff items on
                switch (e.getSlot()) {
                    case 0:
                    case 2:
                    case 4:
                    case 6:
                    case 8:
                        e.setCancelled(true);
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;

        if (Config.Setting.STAFFMODE_PREVENT_INTERACTING_WITH_STAFF_ITEMS.getBoolean()) {

            Player player = (Player) e.getWhoClicked();

            if (this.plugin.getStaffModeManager().isInStaffMode(player)) {
                if (isItem(e.getCursor(), StaffModeManager.StaffItems.EXIT,
                        StaffModeManager.StaffItems.FREEZE_PLAYER,
                        StaffModeManager.StaffItems.INVENTORY,
                        StaffModeManager.StaffItems.VANISH,
                        StaffModeManager.StaffItems.RANDOM_PLAYER_TP)) {
                    //Cancel
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        if (!plugin.getStaffModeManager().isInStaffMode(player)) {
            return;
        }

        ItemStack item = e.getItem();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        Action action = e.getAction();

        switch (action) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:

                e.setCancelled(true);

                /*
                 * Random player teleport
                 */
                if (isItem(item, StaffModeManager.StaffItems.RANDOM_PLAYER_TP)) {
                    teleportToRandomPlayer(player);
                    return;
                }

                /*
                 * Toggle vanish
                 */
                if (isItem(item, StaffModeManager.StaffItems.VANISH)) {
                    if (this.plugin.getVanishManager().isVanished(player)) {
                        this.plugin.getVanishManager().unVanish(player);
                        player.sendMessage(MsgType.VANISH_DISABLED.getMessage());
                    } else {
                        this.plugin.getVanishManager().vanish(player);
                        player.sendMessage(MsgType.VANISH_ENABLED.getMessage());
                    }

                    return;
                }

                /*
                 * Exit staff mode
                 */
                if (isItem(item, StaffModeManager.StaffItems.EXIT)) {
                    plugin.getStaffModeManager().exitStaffMode(player);
                }

                break;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEntityEvent e) {

        Player staff = e.getPlayer();

        if (!plugin.getStaffModeManager().isInStaffMode(staff)) {
            return;
        }

        if (!(e.getRightClicked() instanceof Player)) {
            return;
        }

        Player target = (Player) e.getRightClicked();

        /*
         * Don't allow staff to interact with themselves
         */
        if (staff.equals(target)) {
            return;
        }

        ItemStack item = staff.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        e.setCancelled(true);

        /*
         * Freeze player
         */
        if (isItem(item, StaffModeManager.StaffItems.FREEZE_PLAYER)) {
            if (this.plugin.getFreezeManager().isFrozen(target)) {
                this.plugin.getFreezeManager().unFreeze(target);
                staff.sendMessage(MsgType.UNFROZEN.getMessage().replace("%player%", target.getName()));
            } else {
                this.plugin.getFreezeManager().freeze(target);
                staff.sendMessage(MsgType.FROZEN.getMessage().replace("%player%", target.getName()));
            }

            return;
        }

        /*
         * Inspect inventory
         */
        if (isItem(item, StaffModeManager.StaffItems.INVENTORY)) {
            plugin.getInventoryManager().openPlayerInventory(staff, target);
        }
    }

    private boolean isItem(ItemStack item, ItemStack... staffItems) {
        if (item == null || staffItems == null) {
            return false;
        }

        for (ItemStack staffItem : staffItems) {

            if (item.getType() != staffItem.getType()) {
                return false;
            }

            if (!item.hasItemMeta() || !staffItem.hasItemMeta()) {
                return false;
            }

            ItemMeta itemMeta = item.getItemMeta();
            ItemMeta staffMeta = staffItem.getItemMeta();

            if (!itemMeta.hasDisplayName() || !staffMeta.hasDisplayName()) {
                return false;
            }

            return itemMeta.getDisplayName().equals(staffMeta.getDisplayName());
        }

        return false;
    }

    private boolean isItem(ItemStack item, ItemStack staffItem) {
        if (item == null || staffItem == null) {
            return false;
        }

        if (item.getType() != staffItem.getType()) {
            return false;
        }

        if (!item.hasItemMeta() || !staffItem.hasItemMeta()) {
            return false;
        }

        ItemMeta itemMeta = item.getItemMeta();
        ItemMeta staffMeta = staffItem.getItemMeta();

        if (!itemMeta.hasDisplayName() || !staffMeta.hasDisplayName()) {
            return false;
        }

        return itemMeta.getDisplayName().equals(staffMeta.getDisplayName());
    }
}