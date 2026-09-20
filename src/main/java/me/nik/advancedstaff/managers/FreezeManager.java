package me.nik.advancedstaff.managers;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.utils.TaskUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FreezeManager implements AbstractManager {

    private final AdvancedStaff plugin;

    private final Set<UUID> frozen = new HashSet<>();

    public FreezeManager(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        TaskUtils.taskTimerAsync(() -> getFrozenPlayers().forEach(player -> {
            if (player != null) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(MsgType.FROZEN_ACTIONBAR.getMessage()));
            }
        }), Config.Setting.FREEZE_ACTIONBAR_UPDATE.getLong(), Config.Setting.FREEZE_ACTIONBAR_UPDATE.getLong());
    }

    @Override
    public void shutdown() {
        unfreezeAll();
        this.frozen.clear();
    }

    public boolean isFrozen(Player player) {
        return frozen.contains(player.getUniqueId());
    }

    public void freeze(Player player) {
        if (isFrozen(player) || player.hasPermission(Permissions.ADMIN.getPermission())) return;
        frozen.add(player.getUniqueId());
        player.setMetadata("frozen", new FixedMetadataValue(plugin, true));
    }

    public void unFreeze(Player player) {
        if (!isFrozen(player)) return;
        frozen.remove(player.getUniqueId());
        player.removeMetadata("frozen", plugin);
    }

    public void unfreezeAll() {
        getFrozenPlayers().forEach(player -> {
            if (player != null) {
                player.removeMetadata("frozen", plugin);
            }
        });
        frozen.clear();
    }

    public void handleQuit(Player player) {
        if (!isFrozen(player)) return;
        frozen.remove(player.getUniqueId());

        Config.Setting.LOGOUT_COMMANDS.getStringList().forEach(command -> Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                command.replace("%player%", player.getName()).replace("%nl%", "\n")
        ));
    }

    public List<Player> getFrozenPlayers() {
        List<Player> result = new ArrayList<>();
        for (UUID id : frozen) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) result.add(p);
        }
        return result;
    }
}