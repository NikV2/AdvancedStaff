package me.nik.advancedstaff.listeners;

import me.nik.advancedstaff.AdvancedStaff;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListener implements Listener {

    private final AdvancedStaff plugin;

    public FreezeListener(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (!this.plugin.getFreezeManager().isFrozen(player)) return;

        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null) return;

        if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
            Location fixed = from.clone();
            fixed.setYaw(to.getYaw());
            fixed.setPitch(to.getPitch());
            e.setTo(fixed);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        this.plugin.getFreezeManager().handleQuit(e.getPlayer());
    }
}