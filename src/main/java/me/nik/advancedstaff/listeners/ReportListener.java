package me.nik.advancedstaff.listeners;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class ReportListener implements Listener {

    private final AdvancedStaff plugin;

    public ReportListener(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();

        if (!this.plugin.getReportManager().isAwaitingCustomReason(player)) return;

        e.setCancelled(true);

        UUID targetId = this.plugin.getReportManager().getAwaitingTarget(player);

        this.plugin.getReportManager().clearAwaiting(player);

        String message = e.getMessage();

        if (message.equalsIgnoreCase("cancel")) {
            player.sendMessage(MsgType.REPORT_CANCELLED.getMessage());
            return;
        }

        Player target = Bukkit.getPlayer(targetId);

        if (target == null) {
            player.sendMessage(MsgType.REPORT_TARGET_OFFLINE.getMessage());
            return;
        }

        //Concurrency, go back to main thread since we're using a hashmap implementation
        TaskUtils.task(() -> this.plugin.getReportManager().handleSubmit(player, target, message));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent e) {
        this.plugin.getReportManager().clearAwaiting(e.getPlayer());
    }
}