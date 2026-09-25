package me.nik.advancedstaff.storage;

import me.nik.advancedstaff.AdvancedStaff;
import me.nik.advancedstaff.utils.ChatUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SqliteDataStore implements DataStore {

    private static final String LIST_DELIMITER = "\u0001";

    private final AdvancedStaff plugin;
    private final String fileName;

    private File file;
    private Connection connection;

    public SqliteDataStore(AdvancedStaff plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    @Override
    public StoreType type() {
        return StoreType.SQLITE;
    }

    @Override
    public synchronized void initialize() {
        this.file = new File(plugin.getDataFolder(), fileName + ".db");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());

            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE IF NOT EXISTS data (path TEXT PRIMARY KEY, value TEXT)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void shutdown() {
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void set(String path, Object value) {
        if (value == null) {
            remove(path);
            return;
        }

        String serialized = (value instanceof List)
                ? String.join(LIST_DELIMITER, ((List<?>) value).stream().map(String::valueOf).collect(Collectors.toList()))
                : String.valueOf(value);

        String sql = "INSERT INTO data (path, value) VALUES (?, ?) "
                + "ON CONFLICT(path) DO UPDATE SET value = excluded.value";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setString(2, serialized);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized String getString(String path) {
        return rawGet(path);
    }

    @Override
    public String getString(String path, String def) {
        String raw = rawGet(path);
        return raw == null ? def : raw;
    }

    @Override
    public synchronized int getInt(String path) {
        String raw = rawGet(path);
        if (raw == null) return 0;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public synchronized long getLong(String path) {
        String raw = rawGet(path);
        if (raw == null) return 0;
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public synchronized boolean getBoolean(String path) {
        String raw = rawGet(path);
        return raw != null && Boolean.parseBoolean(raw);
    }

    @Override
    public synchronized List<String> getStringList(String path) {
        String raw = rawGet(path);
        if (raw == null || raw.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(java.util.Arrays.asList(raw.split(LIST_DELIMITER, -1)));
    }

    private String rawGet(String path) {
        String sql = "SELECT value FROM data WHERE path = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, path);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("value") : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public synchronized boolean contains(String path) {
        String exactSql = "SELECT 1 FROM data WHERE path = ? LIMIT 1";
        String childSql = "SELECT 1 FROM data WHERE path LIKE ? LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(exactSql)) {
            ps.setString(1, path);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        try (PreparedStatement ps = connection.prepareStatement(childSql)) {
            ps.setString(1, path + ".%");
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized void remove(String path) {
        String sql = "DELETE FROM data WHERE path = ? OR path LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setString(2, path + ".%");
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Set<String> getKeys(String path) {
        String prefix = (path == null || path.isEmpty()) ? "" : path + ".";

        Set<String> keys = new LinkedHashSet<>();
        String sql = "SELECT path FROM data WHERE path LIKE ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fullPath = rs.getString("path");
                    String remainder = fullPath.substring(prefix.length());
                    int dot = remainder.indexOf('.');
                    keys.add(dot == -1 ? remainder : remainder.substring(0, dot));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptySet();
        }

        return keys;
    }

    @Override
    public void save() {
    }
}