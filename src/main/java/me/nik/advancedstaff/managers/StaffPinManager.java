package me.nik.advancedstaff.managers;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.enums.MsgType;
import me.nik.advancedstaff.enums.Permissions;
import me.nik.advancedstaff.files.Config;
import me.nik.advancedstaff.storage.DataStore;
import me.nik.advancedstaff.storage.SqliteDataStore;
import me.nik.advancedstaff.storage.StoreType;
import me.nik.advancedstaff.storage.YamlDataStore;
import me.nik.advancedstaff.utils.ChatUtils;
import me.nik.advancedstaff.utils.TaskUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class StaffPinManager implements AbstractManager {

    private static final int SALT_LENGTH = 16;
    private static final String DEFAULT_HASH_METHOD = "SHA256";

    private final AdvancedStaff plugin;
    private final Map<UUID, PinData> pins = new ConcurrentHashMap<>();
    private final Deque<UUID> authenticated = new ConcurrentLinkedDeque<>();
    private final Deque<UUID> registering = new ConcurrentLinkedDeque<>();
    private final SecureRandom random = new SecureRandom();

    private DataStore storage;

    public StaffPinManager(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        TaskUtils.taskAsync(() -> {
            String type = Config.Setting.STAFFPIN_STORAGE_TYPE.getString();

            if ("SQLITE".equalsIgnoreCase(type)) {
                this.storage = new SqliteDataStore(plugin, "pins");
            } else {
                if (!"YAML".equalsIgnoreCase(type)) {
                    ChatUtils.log("Unknown storage.type '" + type + "' - defaulting to YAML.");
                }
                this.storage = new YamlDataStore(plugin, "pins");
            }

            this.storage.initialize();

            load();
        });
    }

    @Override
    public void shutdown() {
        if (storage.type() != StoreType.SQLITE) {
            save();
        }
        pins.clear();
        authenticated.clear();
        registering.clear();
    }

    public boolean isRegistering(Player player) {
        return player != null && registering.contains(player.getUniqueId());
    }

    public void beginRegistration(Player player) {
        if (player != null) registering.add(player.getUniqueId());
    }

    public void cancelRegistration(Player player) {
        if (player != null) registering.remove(player.getUniqueId());
    }

    public boolean hasPin(Player player) {
        return player != null && pins.containsKey(player.getUniqueId());
    }

    public boolean isAuthenticated(Player player) {
        return player != null && authenticated.contains(player.getUniqueId());
    }

    public boolean isLocked(Player player) {
        return player != null
                && player.hasPermission(Permissions.ADMIN.getPermission())
                && hasPin(player)
                && !isAuthenticated(player);
    }

    public boolean registerPin(Player player, String pin) {
        if (player == null || pin == null) return false;

        PinData data = createPinData(pin);
        if (data == null) return false;

        boolean update = pins.containsKey(player.getUniqueId());

        pins.put(player.getUniqueId(), data);
        authenticated.add(player.getUniqueId());
        registering.remove(player.getUniqueId());
        save();

        return !update;
    }

    public boolean removePin(Player player) {
        if (player == null) return false;

        PinData removed = pins.remove(player.getUniqueId());
        authenticated.remove(player.getUniqueId());
        registering.remove(player.getUniqueId());

        if (removed == null) return false;

        TaskUtils.taskAsync(this::save);
        return true;
    }

    public boolean authenticate(Player player, String pin) {
        if (player == null || pin == null) return false;

        PinData data = pins.get(player.getUniqueId());
        if (data == null) return false;

        boolean valid = verify(pin, data);

        if (valid) {
            authenticated.add(player.getUniqueId());
        }

        return valid;
    }

    public void lock(Player player) {
        if (player != null && hasPin(player)) {
            authenticated.remove(player.getUniqueId());
        }
    }

    public void handleJoin(Player player) {
        if (player == null || this.storage == null) return;

        authenticated.remove(player.getUniqueId());

        if (!isLocked(player)) return;

        player.sendMessage(MsgType.STAFFPIN_PROMPT.getMessage());
    }

    public void handleQuit(Player player) {
        if (player != null) {
            authenticated.remove(player.getUniqueId());
            registering.remove(player.getUniqueId());
        }
    }

    public boolean isExcludedCommand(String command) {
        if (command == null) return false;

        String normalized = command.startsWith("/") ? command.substring(1) : command;
        String label = normalized.split(" ", 2)[0].toLowerCase();

        for (String excluded : Config.Setting.STAFFPIN_EXCLUDED_COMMANDS.getStringList()) {
            if (excluded == null) continue;

            String value = excluded.trim().toLowerCase();
            if (value.startsWith("/")) value = value.substring(1);

            if (label.equals(value)) {
                return true;
            }
        }

        return false;
    }

    private PinData createPinData(String pin) {
        try {
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            String hashMethod = getHashMethod();
            byte[] hash = hash(pin, salt, hashMethod);

            return new PinData(
                    Base64.getEncoder().encodeToString(salt),
                    Base64.getEncoder().encodeToString(hash),
                    hashMethod
            );
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    private boolean verify(String pin, PinData data) {
        try {
            byte[] salt = Base64.getDecoder().decode(data.salt);
            byte[] expected = Base64.getDecoder().decode(data.hash);
            byte[] actual = hash(pin, salt, data.hashMethod);

            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] hash(String pin, byte[] salt, String method) throws GeneralSecurityException {
        if (method == null || method.trim().isEmpty()) {
            method = DEFAULT_HASH_METHOD;
        }

        String algorithm;
        switch (method.toUpperCase()) {
            case "SHA256":
            case "SHA-256":
                algorithm = "SHA-256";
                break;
            case "SHA512":
            case "SHA-512":
                algorithm = "SHA-512";
                break;
            default:
                throw new GeneralSecurityException("Unsupported staff PIN hash method: " + method);
        }

        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(salt);
        return digest.digest(pin.getBytes(StandardCharsets.UTF_8));
    }

    private String getHashMethod() {
        String method = Config.Setting.STAFFPIN_HASH_METHOD.getString();

        if ("SHA512".equalsIgnoreCase(method) || "SHA-512".equalsIgnoreCase(method)) {
            return "SHA512";
        }

        return DEFAULT_HASH_METHOD;
    }

    private boolean isSupportedHashMethod(String method) {
        return "SHA256".equalsIgnoreCase(method)
                || "SHA-256".equalsIgnoreCase(method)
                || "SHA512".equalsIgnoreCase(method)
                || "SHA-512".equalsIgnoreCase(method);
    }

    private void load() {
        Set<String> section = storage.getKeys("staff-pins");

        if (section == null || section.isEmpty()) return;

        for (String key : section) {
            try {
                UUID uuid = UUID.fromString(key);
                String path = "staff-pins." + key;

                String salt = storage.getString(path + ".salt");
                String hash = storage.getString(path + ".hash");
                String hashMethod = storage.getString(path + ".hash-method", DEFAULT_HASH_METHOD);

                if (salt == null || hash == null || !isSupportedHashMethod(hashMethod)) continue;

                pins.put(uuid, new PinData(salt, hash, hashMethod));
            } catch (Exception ignored) {
            }
        }
    }

    public void save() {
        if (storage == null) return;

        storage.set("staff-pins", null);

        for (Map.Entry<UUID, PinData> entry : pins.entrySet()) {
            String path = "staff-pins." + entry.getKey();
            PinData data = entry.getValue();

            storage.set(path + ".salt", data.salt);
            storage.set(path + ".hash", data.hash);
            storage.set(path + ".hash-method", data.hashMethod);
        }

        storage.shutdown();
    }

    private static final class PinData {
        private final String salt;
        private final String hash;
        private final String hashMethod;

        private PinData(String salt, String hash, String hashMethod) {
            this.salt = salt;
            this.hash = hash;
            this.hashMethod = hashMethod;
        }
    }
}