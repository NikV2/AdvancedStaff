package me.nik.advancedstaff.storage;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.utils.ChatUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class YamlDataStore implements DataStore {

    private final AdvancedStaff plugin;
    private final String fileName;

    private File file;
    private YamlConfiguration data;

    public YamlDataStore(AdvancedStaff plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    @Override
    public StoreType type() {
        return StoreType.YAML;
    }

    @Override
    public void initialize() {
        this.file = new File(plugin.getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.data = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void shutdown() {
        save();
    }

    @Override
    public void set(String path, Object value) {
        data.set(path, value);
    }

    @Override
    public String getString(String path) {
        return data.getString(path);
    }

    @Override
    public String getString(String path, String def) {
        return data.getString(path, def);
    }

    @Override
    public int getInt(String path) {
        return data.getInt(path);
    }

    @Override
    public long getLong(String path) {
        return data.getLong(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return data.getBoolean(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return data.getStringList(path);
    }

    @Override
    public boolean contains(String path) {
        return data.contains(path);
    }

    @Override
    public void remove(String path) {
        data.set(path, null);
    }

    @Override
    public Set<String> getKeys(String path) {
        if (path == null || path.isEmpty()) {
            return data.getKeys(false);
        }
        ConfigurationSection section = data.getConfigurationSection(path);
        return section == null ? Collections.emptySet() : section.getKeys(false);
    }

    @Override
    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}