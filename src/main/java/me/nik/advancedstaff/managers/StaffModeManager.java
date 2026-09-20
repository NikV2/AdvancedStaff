package me.nik.advancedstaff.managers;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class StaffModeManager implements AbstractManager {

    private final AdvancedStaff plugin;

    private final Map<UUID, StaffData> staff = new HashMap<>();

    public StaffModeManager(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
    }

    public void enterStaffMode(Player player) {
        if (isInStaffMode(player)) {
            return;
        }

        UUID uuid = player.getUniqueId();

        StaffData data = new StaffData(player);

        staff.put(uuid, data);

        player.getInventory().clear();

        giveStaffItems(player);

        if (Config.Setting.STAFFMODE_CREATIVE.getBoolean()) {
            player.setGameMode(GameMode.CREATIVE);
        }

        if (Config.Setting.STAFFMODE_FLIGHT.getBoolean()) {
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        if (Config.Setting.STAFFMODE_VANISH.getBoolean()) {
            this.plugin.getVanishManager().vanish(player);
        }
    }

    public void exitStaffMode(Player player) {

        UUID uuid = player.getUniqueId();

        StaffData data = staff.remove(uuid);

        if (data == null) {
            return;
        }

        if (!data.wasVanished()) {
            this.plugin.getVanishManager().unVanish(player);
        }

        player.getInventory().clear();

        data.restore(player);
    }

    public boolean isInStaffMode(Player player) {
        return staff.containsKey(player.getUniqueId());
    }

    private void giveStaffItems(Player player) {

        player.getInventory().setItem(
                0,
                StaffItems.RANDOM_PLAYER_TP
        );

        player.getInventory().setItem(
                2,
                StaffItems.INVENTORY
        );

        player.getInventory().setItem(
                4,
                StaffItems.VANISH
        );

        player.getInventory().setItem(
                6,
                StaffItems.FREEZE_PLAYER
        );

        player.getInventory().setItem(
                8,
                StaffItems.EXIT
        );
    }

    @Override
    public void shutdown() {

        for (UUID uuid : new HashSet<>(staff.keySet())) {

            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                exitStaffMode(player);
            }
        }

        staff.clear();
    }

    /**
     * Information saved before entering staff mode.
     */
    private static class StaffData {

        private final ItemStack[] inventory;
        private final ItemStack[] armor;

        private final Location location;

        private final GameMode gameMode;

        private final boolean allowFlight;
        private final boolean flying;

        private final int foodLevel;
        private final float saturation;

        private final double health;

        private final int level;
        private final float exp;

        private final int fireTicks;

        private final Collection<PotionEffect> potionEffects;

        private final boolean vanished;

        StaffData(Player player) {

            this.inventory = player.getInventory().getContents().clone();
            this.armor = player.getInventory().getArmorContents().clone();

            this.location = player.getLocation().clone();

            this.gameMode = player.getGameMode();

            this.allowFlight = player.getAllowFlight();
            this.flying = player.isFlying();

            this.foodLevel = player.getFoodLevel();
            this.saturation = player.getSaturation();

            this.health = player.getHealth();

            this.level = player.getLevel();
            this.exp = player.getExp();

            this.fireTicks = player.getFireTicks();

            this.potionEffects = new ArrayList<>(
                    player.getActivePotionEffects()
            );

            this.vanished = false;
        }

        public boolean wasVanished() {
            return vanished;
        }

        void restore(Player player) {

            player.teleport(location);

            player.setGameMode(gameMode);

            player.setAllowFlight(allowFlight);
            player.setFlying(flying);

            player.setFoodLevel(foodLevel);
            player.setSaturation(saturation);

            player.setHealth(
                    Math.min(health, player.getMaxHealth())
            );

            player.setLevel(level);
            player.setExp(exp);

            player.setFireTicks(fireTicks);

            for (PotionEffect effect :
                    new ArrayList<>(player.getActivePotionEffects())) {

                player.removePotionEffect(effect.getType());
            }

            for (PotionEffect effect : potionEffects) {
                player.addPotionEffect(effect);
            }

            player.getInventory().setContents(inventory);
            player.getInventory().setArmorContents(armor);

            player.updateInventory();
        }
    }

    public static class StaffItems {
        public static ItemStack RANDOM_PLAYER_TP = MiscUtils.makeItem(Material.PLAYER_HEAD, "&2Teleport to a random player", null);
        public static ItemStack FREEZE_PLAYER = MiscUtils.makeItem(Material.BLAZE_ROD, "&bFreeze player", null);
        public static ItemStack VANISH = MiscUtils.makeItem(Material.BEACON, "&6Toggle Vanish", null);
        public static ItemStack INVENTORY = MiscUtils.makeItem(Material.CHEST, "&eInspect inventory", null);
        public static ItemStack EXIT = MiscUtils.makeItem(Material.BEACON, "&cExit Staff Mode", null);
    }
}