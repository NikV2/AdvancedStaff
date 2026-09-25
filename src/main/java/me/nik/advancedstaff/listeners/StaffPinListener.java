package me.nik.advancedstaff.listeners;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.utils.TaskUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StaffPinListener implements Listener {

    private final AdvancedStaff plugin;

    public StaffPinListener(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        plugin.getStaffPinManager().handleJoin(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        plugin.getStaffPinManager().handleQuit(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();

        boolean registering = plugin.getStaffPinManager().isRegistering(player);
        boolean locked = plugin.getStaffPinManager().isLocked(player);

        if (!registering && !locked) return;

        e.setCancelled(true);

        String pin = e.getMessage().trim();

        if (!player.isOnline()) return;

        if (registering) {
            registerPin(player, pin);
            return;
        }

        if (plugin.getStaffPinManager().authenticate(player, pin)) {
            player.sendMessage(MsgType.STAFFPIN_UNLOCKED.getMessage());
        } else {
            player.sendMessage(MsgType.STAFFPIN_INCORRECT.getMessage());
            player.sendMessage(MsgType.STAFFPIN_PROMPT.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();

        if (!plugin.getStaffPinManager().isLocked(player)) return;

        if (plugin.getStaffPinManager().isExcludedCommand(e.getMessage())) {
            return;
        }

        e.setCancelled(true);
        player.sendMessage(MsgType.STAFFPIN_LOCKED.getMessage());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (plugin.getStaffPinManager().isLocked(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(MsgType.STAFFPIN_LOCKED.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (plugin.getStaffPinManager().isLocked(e.getPlayer())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(MsgType.STAFFPIN_LOCKED.getMessage());
        }
    }

    private void registerPin(Player player, String pin) {
        if (!plugin.getStaffPinManager().isRegistering(player)) return;

        int min = Config.Setting.STAFFPIN_MIN_LENGTH.getInt();
        int max = Config.Setting.STAFFPIN_MAX_LENGTH.getInt();

        if (pin.length() < min || pin.length() > max) {
            player.sendMessage(MsgType.STAFFPIN_INVALID.getMessage()
                    .replace("%min%", String.valueOf(min))
                    .replace("%max%", String.valueOf(max)));
            return;
        }

        if (Config.Setting.STAFFPIN_NUMERIC_ONLY.getBoolean() && !pin.matches("\\d+")) {
            player.sendMessage(MsgType.STAFFPIN_NUMERIC.getMessage());
            return;
        }

        if (plugin.getStaffPinManager().hasPin(player)) {
            plugin.getStaffPinManager().cancelRegistration(player);
            player.sendMessage(MsgType.STAFFPIN_ALREADY_REGISTERED.getMessage());
            return;
        }

        plugin.getStaffPinManager().registerPin(player, pin);
        player.sendMessage(MsgType.STAFFPIN_REGISTERED.getMessage());
    }
}