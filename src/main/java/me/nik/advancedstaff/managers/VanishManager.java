package me.nik.advancedstaff.managers;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.utils.ReflectionUtils;
import me.nik.advancedstaff.utils.TaskUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VanishManager implements AbstractManager {

    private static final String TEAM_NAME = "vanished";

    private final AdvancedStaff plugin;
    private final Set<UUID> vanished = new HashSet<>();

    public VanishManager(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        if (Config.Setting.VANISH_DISABLE_COLLISIONS.getBoolean() && ReflectionUtils.isCollisionRuleSupported()) {
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
            Team team = board.getTeam(TEAM_NAME);
            if (team == null) {
                team = board.registerNewTeam(TEAM_NAME);
            }
            ReflectionUtils.setNeverCollide(team);
        }

        //Actionbar Task
        TaskUtils.taskTimerAsync(() ->
                        getVanishedPlayers().forEach(player -> {
                                    if (player != null) {
                                        player.spigot().sendMessage(
                                                ChatMessageType.ACTION_BAR,
                                                new TextComponent(MsgType.VANISH_ACTIONBAR.getMessage()));
                                    }
                                }
                        ),
                Config.Setting.VANISH_ACTIONBAR_UPDATE.getLong(), Config.Setting.VANISH_ACTIONBAR_UPDATE.getLong());
    }

    @Override
    public void shutdown() {
        unVanishAll();
        this.vanished.clear();
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public void vanish(Player player) {
        if (isVanished(player)) return;
        vanished.add(player.getUniqueId());
        player.setMetadata("vanished", new FixedMetadataValue(plugin, true));

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (online.equals(player)) continue;
            if (!online.hasPermission(Permissions.ADMIN.getPermission())) {
                ReflectionUtils.hidePlayer(plugin, online, player);
            }
        }

        addToCollisionTeam(player);
    }

    public void unVanish(Player player) {
        if (!isVanished(player)) return;
        vanished.remove(player.getUniqueId());
        player.removeMetadata("vanished", plugin);

        for (Player online : Bukkit.getOnlinePlayers()) {
            ReflectionUtils.showPlayer(plugin, online, player);
        }

        removeFromCollisionTeam(player);
    }

    public void unVanishAll() {
        for (UUID id : new ArrayList<>(vanished)) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) unVanish(p);
        }
    }

    public void refreshFor(Player joining) {
        boolean canSeeVanished = joining.hasPermission(Permissions.ADMIN.getPermission());
        for (UUID id : vanished) {
            Player vp = Bukkit.getPlayer(id);
            if (vp == null || vp.equals(joining)) continue;
            if (!canSeeVanished) {
                ReflectionUtils.hidePlayer(plugin, joining, vp);
            }
        }

        if (isVanished(joining)) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.equals(joining)) continue;
                if (!online.hasPermission(Permissions.ADMIN.getPermission())) {
                    ReflectionUtils.hidePlayer(plugin, online, joining);
                }
            }
        }
    }

    private void addToCollisionTeam(Player player) {
        if (!ReflectionUtils.isCollisionRuleSupported()) return;
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(TEAM_NAME);
        if (team != null) team.addEntry(player.getName());
    }

    private void removeFromCollisionTeam(Player player) {
        if (!ReflectionUtils.isCollisionRuleSupported()) return;
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(TEAM_NAME);
        if (team != null) team.removeEntry(player.getName());
    }

    public List<Player> getVanishedPlayers() {
        List<Player> result = new ArrayList<>();
        for (UUID id : vanished) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) result.add(p);
        }
        return result;
    }
}