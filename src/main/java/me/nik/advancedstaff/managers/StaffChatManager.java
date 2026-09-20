package me.nik.advancedstaff.managers;

import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StaffChatManager implements AbstractManager {

    private final Set<UUID> staffChatPlayers = new HashSet<>();

    @Override
    public void initialize() {
    }

    public void toggleStaffChat(Player player) {

        UUID uuid = player.getUniqueId();

        if (this.staffChatPlayers.contains(uuid)) {
            this.staffChatPlayers.remove(uuid);
            player.sendMessage(MsgType.STAFFCHAT_DISABLED.getMessage());
        } else {
            this.staffChatPlayers.add(uuid);
            player.sendMessage(MsgType.STAFFCHAT_ENABLED.getMessage());
        }
    }

    public boolean isStaffChatEnabled(Player player) {
        return this.staffChatPlayers.contains(player.getUniqueId());
    }

    public void handlePlayerQuit(Player player) {
        if (isStaffChatEnabled(player)) {
            this.staffChatPlayers.remove(player.getUniqueId());
        }
    }

    public void sendMessage(Player player, String message) {

        String formattedMessage = ChatUtils.format(Config.Setting.STAFFCHAT_FORMAT.getString()
                .replace("%player%", player.getName())
                .replace("%message%", message));

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(p -> p != null && p.hasPermission(Permissions.ADMIN.getPermission()))
                .forEach(receiver -> receiver.sendMessage(formattedMessage));

        if (Config.Setting.STAFFCHAT_CONSOLE.getBoolean()) {
            Bukkit.getConsoleSender().sendMessage(formattedMessage);
        }
    }

    @Override
    public void shutdown() {
        this.staffChatPlayers.clear();
    }
}