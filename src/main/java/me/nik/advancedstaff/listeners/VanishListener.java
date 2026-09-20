package me.nik.advancedstaff.listeners;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.EnderChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class VanishListener implements Listener {

    private final AdvancedStaff plugin;

    public VanishListener(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        this.plugin.getVanishManager().refreshFor(player);

        if (Config.Setting.VANISH_DISABLE_JOIN_QUIT_MESSAGES.getBoolean()) {
            if (this.plugin.getVanishManager().isVanished(player)) {
                e.setJoinMessage(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        if (Config.Setting.VANISH_DISABLE_JOIN_QUIT_MESSAGES.getBoolean()) {
            if (this.plugin.getVanishManager().isVanished(e.getPlayer())) {
                e.setQuitMessage(null);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTarget(EntityTargetEvent e) {
        if (Config.Setting.VANISH_DISABLE_MOB_TARGETING.getBoolean()) {
            if (e.getTarget() instanceof Player) {
                Player target = (Player) e.getTarget();
                if (this.plugin.getVanishManager().isVanished(target)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent e) {
        if (Config.Setting.VANISH_DISABLE_ITEM_PICK.getBoolean()) {
            if (e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if (this.plugin.getVanishManager().isVanished(player)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent e) {
        if (Config.Setting.VANISH_DISABLE_ITEM_PICK.getBoolean()) {
            if (this.plugin.getVanishManager().isVanished(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (Config.Setting.VANISH_DISABLE_ITEM_DROP.getBoolean()) {
            if (this.plugin.getVanishManager().isVanished(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPhysical(PlayerInteractEvent event) {
        if (Config.Setting.VANISH_DISABLE_PHYSICAL_EVENTS.getBoolean()) {
            if (event.getAction() != Action.PHYSICAL) return;

            Player player = event.getPlayer();
            if (this.plugin.getVanishManager().isVanished(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (Config.Setting.VANISH_SILENT_CHEST_OPENING.getBoolean()) {
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

            Player player = e.getPlayer();
            if (!this.plugin.getVanishManager().isVanished(player)) return;

            Block block = e.getClickedBlock();
            if (block == null) return;

            if (player.isSneaking() && e.getItem() != null && e.getItem().getType().isBlock()) {
                return;
            }

            BlockState state = block.getState();
            if (!(state instanceof InventoryHolder)) return;

            boolean isEnderChest = state instanceof EnderChest;
            Inventory real = isEnderChest ? player.getEnderChest() : ((InventoryHolder) state).getInventory();

            e.setCancelled(true);

            ChestHolder holder = new ChestHolder(block, isEnderChest);
            Inventory clone = Bukkit.createInventory(holder, real.getType());
            clone.setContents(real.getContents());
            holder.inventory = clone;

            player.openInventory(clone);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (Config.Setting.VANISH_SILENT_CHEST_OPENING.getBoolean()) {
            if (!(e.getInventory().getHolder() instanceof ChestHolder)) return;
            if (!(e.getPlayer() instanceof Player)) return;

            ChestHolder holder = (ChestHolder) e.getInventory().getHolder();
            Inventory clone = e.getInventory();

            if (holder.enderChest) {
                e.getPlayer().getEnderChest().setContents(clone.getContents());
                return;
            }

            BlockState freshState = holder.block.getState();
            if (freshState instanceof InventoryHolder) {
                ((InventoryHolder) freshState).getInventory().setContents(clone.getContents());
            }
        }
    }

    private static final class ChestHolder implements InventoryHolder {
        private final Block block;
        private final boolean enderChest;
        private Inventory inventory;

        ChestHolder(Block block, boolean enderChest) {
            this.block = block;
            this.enderChest = enderChest;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}