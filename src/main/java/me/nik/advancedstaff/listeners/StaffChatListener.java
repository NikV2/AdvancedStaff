package me.nik.advancedstaff.listeners;

import me.nik.advancedstaff.AdvancedStaff;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StaffChatListener implements Listener {

    private final AdvancedStaff plugin;

    public StaffChatListener(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (!this.plugin.getStaffChatManager().isStaffChatEnabled(player)) return;

        e.setCancelled(true);

        this.plugin.getStaffChatManager().sendMessage(player, e.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        this.plugin.getStaffChatManager().handlePlayerQuit(e.getPlayer());
    }
}