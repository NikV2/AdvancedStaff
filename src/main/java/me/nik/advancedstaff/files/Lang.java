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
    }
}