package me.nik.advancedstaff.files;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.files.commentedfiles.CommentedFileConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {

    private static final String[] HEADER = new String[]{
            "+----------------------------------------------------------------------------------------------+",
            "|                                                                                              |",
            "|                                          AdvancedStaff                                       |",
            "|                                                                                              |",
            "|                               Discord: https://discord.gg/m7j2Y9H                            |",
            "|                                                                                              |",
            "|                                           Author: Nik                                        |",
            "|                                                                                              |",
            "+----------------------------------------------------------------------------------------------+"
    };

    private final AdvancedStaff plugin;
    private CommentedFileConfiguration configuration;
    private static boolean exists;

    public Config(AdvancedStaff plugin) {
        this.plugin = plugin;
    }

    public void setup() {

        File configFile = new File(this.plugin.getDataFolder(), "config.yml");

        exists = configFile.exists();

        boolean setHeaderFooter = !exists;

        boolean changed = setHeaderFooter;

        this.configuration = CommentedFileConfiguration.loadConfiguration(this.plugin, configFile);

        if (setHeaderFooter) {
            this.configuration.addComments(HEADER);
        }

        for (Setting setting : Setting.values()) {

            setting.reset();

            changed |= setting.setIfNotExists(this.configuration);
        }

        if (changed) this.configuration.save();

        for (Setting setting : Setting.values()) setting.loadValue();
    }

    public void reset() {
        for (Setting setting : Setting.values()) setting.reset();
    }

    /**
     * @return the config.yml as a CommentedFileConfiguration
     */
    public CommentedFileConfiguration getConfig() {
        return this.configuration;
    }

    public enum Setting {

        CHECK_UPDATES("check_updates", true, "Should we check for updates on startup?"),

        VANISH("vanish", "", "Vanish Settings"),
        VANISH_ALIASES("vanish.aliases", Arrays.asList("vanish", "v"), "Typing the aliases below will execute the vanish command"),
        VANISH_DISABLE_COLLISIONS("vanish.disable_collisions", true, "Should we disable the vanished player's collision?"),
        VANISH_DISABLE_ITEM_DROP("vanish.disable_item_drop", true, "Should we prevent the vanished player from dropping items?"),
        VANISH_DISABLE_JOIN_QUIT_MESSAGES("vanish.disable_join_quit_messages", true, "Should we disable the vanished player's join-quit messages?"),
        VANISH_DISABLE_ITEM_PICK("vanish.disable_item_pick", true, "Should we prevent the vanished player from picking up items?"),
        VANISH_DISABLE_MOB_TARGETING("vanish.disable_mob_targeting", true, "Should we prevent the vanished player from being targeted by mobs?"),
        VANISH_SILENT_CHEST_OPENING("vanish.silent_chest_opening", true, "Should chest opening for vanished players be silent?", "Nearby players will not hear or see the chest being opened"),
        VANISH_DISABLE_PHYSICAL_EVENTS("vanish.disable_physical_events", true, "Should we disable the vanished player's physical events?", "This prevents vanished players from enabling pressure plates and so on"),
        VANISH_ACTIONBAR_UPDATE("vanish.actionbar_update", 20L, "How often should the actionbar update for vanished players?", "(In ticks)"),

        FREEZE("freeze", "", "Freeze Settings"),
        FREEZE_ALIASES("freeze.aliases", Collections.singletonList("freeze"), "Typing the aliases below will execute the freeze command"),
        LOGOUT_COMMANDS("freeze.logout_commands", Collections.singletonList(
                "ban %player% Logged out during staff interrogation"
        ), "The commands that will be executed once a frozen player disconnects", "(%nl% for new line)"),
        FREEZE_ACTIONBAR_UPDATE("freeze.actionbar_update", 20L, "How often should the actionbar update for frozen players?", "(In ticks)"),

        STAFFCHAT("staff_chat", "", "Staff Chat Settings"),
        STAFFCHAT_ALIASES("staff_chat.aliases", Arrays.asList("staffchat", "sc"), "Typing the aliases below will execute the staffchat command"),
        STAFFCHAT_FORMAT("staff_chat.format", "&f&l[&bStaffChat&f&l] &b%player%&f» &9%message%", "The chat formatting that will show to staff members", "Placeholders: %message%, %player%", "Color codes are supported"),
        STAFFCHAT_CONSOLE("staff_chat.console", true, "Should staff chat messages also be shown to the console?"),
        STAFFCHAT_DISABLE_ON_QUIT("staff_chat.disable_on_quit", true, "Should staff chat be disabled when staff members quit?"),

        STAFFMODE("staffmode", "", "Staff Mode Settings"),
        STAFFMODE_ALIASES("staffmode.aliases", Arrays.asList("staffmode", "staff"), "Typing the aliases below will execute the staffmode command"),
        STAFFMODE_CREATIVE("staffmode.creative", true, "Should we put players who enter the staff mode to creative?"),
        STAFFMODE_FLIGHT("staffmode.flight", true, "Should we put players who enter the staff mode to flight mode?"),
        STAFFMODE_VANISH("staffmode.vanish", true, "Should we put players who enter the staff mode to vanish mode?"),
        STAFFMODE_PREVENT_INTERACTING_WITH_STAFF_ITEMS("staffmode.prevent_interacting_with_staff_items", true, "Should we prevent any type of interaction with the staff inventory items?"),
        STAFFMODE_PREVENT_TELEPORTING_TO_OTHER_STAFF("staffmode.prevent_teleporting_to_other_staff", true, "Should we prevent the random teleportation from teleporting to staff members?"),

        INVENTORY("inventory", "", "Inventory Settings"),
        INVENTORY_ALIASES("inventory.aliases", Collections.singletonList("inventory"), "Typing the aliases below will execute the inventory command"),

        STAFFPIN("staff_pin", "", "Staff PIN Settings"),
        STAFFPIN_ALIASES("staff_pin.aliases", Arrays.asList("staffpin", "pin"), "Typing the aliases below will execute the staffpin command"),
        STAFFPIN_MIN_LENGTH("staff_pin.min_length", 4, "Minimum PIN length"),
        STAFFPIN_MAX_LENGTH("staff_pin.max_length", 12, "Maximum PIN length"),
        STAFFPIN_NUMERIC_ONLY("staff_pin.numeric_only", true, "Should staff PINs contain numbers only?"),
        STAFFPIN_HASH_METHOD("staff_pin.hash_method", "SHA256", "Hash algorithm used to protect newly registered staff PINs", "Supported: SHA256, SHA512"),
        STAFFPIN_EXCLUDED_COMMANDS("staff_pin.excluded_commands", Arrays.asList(
                "login",
                "log",
                "l",
                "register",
                "reg",
                "email",
                "captcha",
                "2fa",
                "totp"
        ), "Commands staff can use while their PIN is locked"),

        REPORT("report", "", "Report Settings"),
        REPORT_ALIASES("report.aliases", Arrays.asList("report", "rep"), "Typing the aliases below will execute the report command"),
        REPORTS_ALIASES("report.reports_aliases", Collections.singletonList("reports"), "Typing the aliases below will execute the reports (staff management) command"),
        REPORT_COOLDOWN("report.cooldown_seconds", 60L, "How many seconds a player must wait between submitting reports"),
        REPORT_MAX_ACTIVE_PER_PLAYER("report.max_active_per_player", 3, "Maximum number of a player's own open reports allowed at once", "0 = unlimited"),
        REPORT_ALLOW_CUSTOM_REASON("report.allow_custom_reason", true, "Should players be able to type a custom reason instead of picking a preset one?"),
        REPORT_NOTIFY_STAFF("report.notify_staff", true, "Should online staff with the manage permission be notified when a new report comes in?"),
        REPORT_COMPLETED_HANDLING("report.completed_handling", "KEEP", "What happens to a report once it's resolved/rejected", "KEEP = stays in reports.yml for history, DELETE = removed immediately"),
        REPORT_REASONS("report.reasons", Arrays.asList("Hacking / Cheating", "Chat Abuse", "Exploiting", "Scamming", "Inappropriate Name/Skin"), "The preset reasons shown as buttons in the report GUI");

        private final String key;
        private final Object defaultValue;
        private boolean excluded;
        private final String[] comments;
        private Object value = null;

        Setting(String key, Object defaultValue, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
        }

        Setting(String key, Object defaultValue, boolean excluded, String... comments) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.comments = comments != null ? comments : new String[0];
            this.excluded = excluded;
        }

        /**
         * Gets the setting as a boolean
         *
         * @return The setting as a boolean
         */
        public boolean getBoolean() {
            this.loadValue();
            return (boolean) this.value;
        }

        public String getKey() {
            return this.key;
        }

        /**
         * @return the setting as an int
         */
        public int getInt() {
            this.loadValue();
            return (int) this.getNumber();
        }

        /**
         * @return the setting as a short
         */
        public short getShort() {
            this.loadValue();
            return (short) this.getNumber();
        }

        /**
         * @return the setting as a long
         */
        public long getLong() {
            this.loadValue();
            return (long) this.getNumber();
        }

        /**
         * @return the setting as a double
         */
        public double getDouble() {
            this.loadValue();
            return this.getNumber();
        }

        /**
         * @return the setting as a float
         */
        public float getFloat() {
            this.loadValue();
            return (float) this.getNumber();
        }

        /**
         * @return the setting as a String
         */
        public String getString() {
            this.loadValue();
            return String.valueOf(this.value);
        }

        private double getNumber() {
            if (this.value instanceof Integer) {
                return (int) this.value;
            } else if (this.value instanceof Short) {
                return (short) this.value;
            } else if (this.value instanceof Byte) {
                return (byte) this.value;
            } else if (this.value instanceof Float) {
                return (float) this.value;
            } else if (this.value instanceof Long) {
                return (long) this.value;
            }

            return (double) this.value;
        }

        /**
         * @return the setting as a string list
         */
        @SuppressWarnings("unchecked")
        public List<String> getStringList() {
            this.loadValue();
            return (List<String>) this.value;
        }

        private boolean setIfNotExists(CommentedFileConfiguration fileConfiguration) {
            this.loadValue();

            if (exists && this.excluded) return false;

            if (fileConfiguration.get(this.key) == null) {
                List<String> comments = Stream.of(this.comments).collect(Collectors.toList());
                if (this.defaultValue != null) {
                    fileConfiguration.set(this.key, this.defaultValue, comments.toArray(new String[0]));
                } else {
                    fileConfiguration.addComments(comments.toArray(new String[0]));
                }

                return true;
            }

            return false;
        }

        /**
         * Resets the cached value
         */
        public void reset() {
            this.value = null;
        }

        /**
         * @return true if this setting is only a section and doesn't contain an actual value
         */
        public boolean isSection() {
            return this.defaultValue == null;
        }

        /**
         * Loads the value from the config and caches it if it isn't set yet
         */
        public void loadValue() {
            if (this.value != null) return;
            this.value = AdvancedStaff.getInstance().getConfiguration().getConfig().get(this.key);
        }
    }
}