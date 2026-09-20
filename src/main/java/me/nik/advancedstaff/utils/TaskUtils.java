package me.nik.advancedstaff.utils;

import me.nik.advancedstaff.AdvancedStaff;
import org.bukkit.Bukkit;

public final class TaskUtils {

    private TaskUtils() {
    }

    public static void taskTimer(Runnable runnable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimer(AdvancedStaff.getInstance(), runnable, delay, interval);
    }

    public static void taskTimerAsync(Runnable runnable, long delay, long interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(AdvancedStaff.getInstance(), runnable, delay, interval);
    }

    public static void task(Runnable runnable) {
        Bukkit.getScheduler().runTask(AdvancedStaff.getInstance(), runnable);
    }

    public static void taskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(AdvancedStaff.getInstance(), runnable);
    }

    public static void taskLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(AdvancedStaff.getInstance(), runnable, delay);
    }

    public static void taskLaterAsync(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(AdvancedStaff.getInstance(), runnable, delay);
    }
}