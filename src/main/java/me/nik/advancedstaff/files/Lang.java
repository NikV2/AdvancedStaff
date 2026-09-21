package me.nik.advancedstaff.files;

import me.nik.advancedstaff.utils.MiscUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Lang {
    private File file;
    private FileConfiguration lang;

    public void setup(JavaPlugin plugin) {

        this.file = new File(plugin.getDataFolder(), "lang.yml");

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException ignored) {
            }
        }

        reload();
    }

    public FileConfiguration get() {
        return lang;
    }

    public void save() {
        try {
            this.lang.save(file);
        } catch (IOException ignored) {
        }
    }

    public void reload() {
        this.lang = MiscUtils.loadConfigurationUTF_8(file);
    }

    public void addDefaults() {
        get().addDefault("prefix", "&f&l[&bAdvancedStaff&f&l]&f»&r ");
        get().addDefault("teleported", "&fYou have been randomly teleported to &b%player%");
        get().addDefault("staffchat_enabled", "&fYou have enabled the &bstaff chat");
        get().addDefault("staffchat_disabled", "&fYou have disabled the &bstaff chat");
        get().addDefault("vanish_enabled", "&fYou are now &binvisible");
        get().addDefault("vanish_disabled", "&fYou are no longer &binvisible");
        get().addDefault("vanish_actionbar", "&fYou are &bvanished");
        get().addDefault("staffmode_enabled", "&fYou have enabled &bStaff Mode");
        get().addDefault("staffmode_disabled", "&fYou have disabled &bStaff Mode");
        get().addDefault("frozen", "&b%player% &fhas been frozen!");
        get().addDefault("unfrozen", "&b%player% &fis no longer frozen!");
        get().addDefault("frozen_actionbar", "&cYou have been &6Frozen&c, Do not log out!");
        get().addDefault("update_found", "&fThere is a new version available, Your version &b%current% &fnew version &b%new%");
        get().addDefault("update_not_found", "&fNo updates are found, You're running the latest version!");
        get().addDefault("no_perm", "&cYou do not have permission to do that!");
        get().addDefault("reloaded", "&fYou have successfully reloaded the plugin!");
        get().addDefault("console_commands", "&c&lYou cannot run this command through the console!");
        get().addDefault("report_submitted", "&fYour report against &b%player% &fhas been submitted");
        get().addDefault("report_cannot_self", "&cYou cannot report yourself!");
        get().addDefault("report_cooldown", "&cYou must wait before submitting another report!");
        get().addDefault("report_max_active", "&cYou already have too many active reports open!");
        get().addDefault("report_duplicate", "&cYou have already reported this player!");
        get().addDefault("report_target_offline", "&cThat player is not online!");
        get().addDefault("report_custom_prompt", "&fType your reason in chat, or type &bcancel &fto back out");
        get().addDefault("report_cancelled", "&fReport cancelled");
        get().addDefault("report_staff_notify", "&b%reporter% &fhas reported &b%target% &ffor &b%reason%");
        get().addDefault("report_resolved", "&fReport &b#%id% &fmarked as resolved");
        get().addDefault("report_rejected", "&fReport &b#%id% &frejected");
        get().addDefault("staffpin_registered", "&fYour staff PIN has been &bregistered&f");
        get().addDefault("staffpin_updated", "&fYour staff PIN has been &bupdated&f");
        get().addDefault("staffpin_removed", "&fYour staff PIN has been &bremoved&f");
        get().addDefault("staffpin_already_registered", "&cYou already have a staff PIN. Use &f/advancedstaff staffpin remove &cto remove it first");
        get().addDefault("staffpin_not_registered", "&cYou do not have a staff PIN");
        get().addDefault("staffpin_invalid", "&cYour staff PIN must be between &f%min% &cand &f%max% &ccharacters");
        get().addDefault("staffpin_numeric", "&cYour staff PIN may only contain numbers");
        get().addDefault("staffpin_prompt", "&cYour staff PIN is required. Type it in chat to continue");
        get().addDefault("staffpin_registration_prompt", "&fType your new staff PIN in chat to register it");
        get().addDefault("staffpin_unlocked", "&fYour staff access has been restored");
        get().addDefault("staffpin_incorrect", "&cIncorrect staff PIN");
        get().addDefault("staffpin_locked", "&cYou must enter your staff PIN before you can do that");

    }
}