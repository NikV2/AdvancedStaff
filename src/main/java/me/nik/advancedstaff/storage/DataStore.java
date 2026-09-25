package me.nik.advancedstaff.storage;

import java.util.List;
import java.util.Set;

public interface DataStore {

    StoreType type();

    void initialize();

    void shutdown();

    void set(String path, Object value);

    String getString(String path);

    String getString(String path, String def);

    int getInt(String path);

    long getLong(String path);

    boolean getBoolean(String path);

    List<String> getStringList(String path);

    boolean contains(String path);

    void remove(String path);

    Set<String> getKeys(String path);

    void save();
}