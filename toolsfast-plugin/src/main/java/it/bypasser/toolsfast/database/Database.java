package it.bypasser.toolsfast.database;

import it.bypasser.toolsfast.ToolsFast;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public final class Database {

    private final ToolsFast plugin;
    private final ExecutorService pool = Executors.newFixedThreadPool(4, r -> {
        Thread t = new Thread(r, "ToolsFast-DB");
        t.setDaemon(true);
        return t;
    });
    private volatile Connection connection;
    private boolean mysql;

    public Database(ToolsFast plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        FileConfiguration cfg = plugin.config().get();
        this.mysql = cfg.getBoolean("database.mysql.enabled", false);
        if (mysql) {
            String host = cfg.getString("database.mysql.host", "localhost");
            int port = cfg.getInt("database.mysql.port", 3306);
            String name = cfg.getString("database.mysql.database", "toolsfast");
            String user = cfg.getString("database.mysql.user", "root");
            String pass = cfg.getString("database.mysql.password", "");
            boolean ssl = cfg.getBoolean("database.mysql.ssl", false);
            String url = "jdbc:mysql://" + host + ":" + port + "/" + name + "?useSSL=" + ssl + "&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                try { Class.forName("org.gjt.mm.mysql.Driver"); }
                catch (ClassNotFoundException ignored) {}
            }
            connection = DriverManager.getConnection(url, user, pass);
        } else {
            File file = new File(plugin.getDataFolder(), "data.db");
            file.getParentFile().mkdirs();
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        }
        createTables();
    }

    private void createTables() {
        if (mysql) {
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS tf_stats (uuid VARCHAR(64) PRIMARY KEY, blocks BIGINT DEFAULT 0, money DOUBLE DEFAULT 0, trees BIGINT DEFAULT 0, crops BIGINT DEFAULT 0, items_sold BIGINT DEFAULT 0)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS tf_tool_uses (uuid VARCHAR(64), tool_id VARCHAR(64), uses BIGINT DEFAULT 0, PRIMARY KEY (uuid, tool_id))");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS tf_self_destruct (id INTEGER PRIMARY KEY AUTO_INCREMENT, uuid VARCHAR(64), tool_id VARCHAR(64), expires_at BIGINT)");
            } catch (SQLException e) {
                plugin.getLogger().warning("Create tables (mysql) failed: " + e.getMessage());
            }
        } else {
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS tf_stats (uuid TEXT PRIMARY KEY, blocks INTEGER DEFAULT 0, money REAL DEFAULT 0, trees INTEGER DEFAULT 0, crops INTEGER DEFAULT 0, items_sold INTEGER DEFAULT 0)");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS tf_tool_uses (uuid TEXT, tool_id TEXT, uses INTEGER DEFAULT 0, PRIMARY KEY (uuid, tool_id))");
                st.executeUpdate("CREATE TABLE IF NOT EXISTS tf_self_destruct (id INTEGER PRIMARY KEY AUTOINCREMENT, uuid TEXT, tool_id TEXT, expires_at INTEGER)");
            } catch (SQLException e) {
                plugin.getLogger().warning("Create tables (sqlite) failed: " + e.getMessage());
            }
        }
    }

    private boolean isMysql() { return mysql; }

    public CompletableFuture<Long> incrementStat(String uuid, String column, long amount) {
        return supplyAsync(() -> {
            try {
                String sql;
                if (isMysql()) {
                    sql = "INSERT INTO tf_stats (uuid, " + column + ") VALUES (?, ?) ON DUPLICATE KEY UPDATE " + column + "=" + column + " + VALUES(" + column + ")";
                } else {
                    sql = "INSERT INTO tf_stats (uuid, " + column + ") VALUES (?, ?) ON CONFLICT(uuid) DO UPDATE SET " + column + "=" + column + " + ?";
                }
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, uuid);
                    ps.setLong(2, amount);
                    if (!isMysql()) ps.setLong(3, amount);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement("SELECT " + column + " FROM tf_stats WHERE uuid = ?")) {
                    ps.setString(1, uuid);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return rs.getLong(1);
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("incrementStat failed: " + e.getMessage());
            }
            return amount;
        });
    }

    public CompletableFuture<Long> getStat(String uuid, String column) {
        return supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT " + column + " FROM tf_stats WHERE uuid = ?")) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("getStat failed: " + e.getMessage());
            }
            return 0L;
        });
    }

    public CompletableFuture<Long> incrementToolUse(String uuid, String toolId) {
        return supplyAsync(() -> {
            try {
                String sql;
                if (isMysql()) {
                    sql = "INSERT INTO tf_tool_uses (uuid, tool_id, uses) VALUES (?, ?, 1) ON DUPLICATE KEY UPDATE uses=uses+1";
                } else {
                    sql = "INSERT INTO tf_tool_uses (uuid, tool_id, uses) VALUES (?, ?, 1) ON CONFLICT(uuid, tool_id) DO UPDATE SET uses=uses+1";
                }
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, uuid);
                    ps.setString(2, toolId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = connection.prepareStatement("SELECT uses FROM tf_tool_uses WHERE uuid=? AND tool_id=?")) {
                    ps.setString(1, uuid);
                    ps.setString(2, toolId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return rs.getLong(1);
                    }
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("incrementToolUse failed: " + e.getMessage());
            }
            return 1L;
        });
    }

    public CompletableFuture<Long> getToolUses(String uuid, String toolId) {
        return supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT uses FROM tf_tool_uses WHERE uuid=? AND tool_id=?")) {
                ps.setString(1, uuid);
                ps.setString(2, toolId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getLong(1);
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("getToolUses failed: " + e.getMessage());
            }
            return 0L;
        });
    }

    public CompletableFuture<List<StatEntry>> topBy(String column, int limit) {
        return supplyAsync(() -> {
            List<StatEntry> out = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement("SELECT uuid, " + column + " AS value FROM tf_stats ORDER BY value DESC LIMIT ?")) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) out.add(new StatEntry(rs.getString(1), rs.getLong(2)));
                }
            } catch (SQLException e) {
                plugin.getLogger().warning("topBy failed: " + e.getMessage());
            }
            return out;
        });
    }

    public CompletableFuture<Void> recordSelfDestruct(String uuid, String toolId, long expiresAt) {
        return runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO tf_self_destruct (uuid, tool_id, expires_at) VALUES (?, ?, ?)")) {
                ps.setString(1, uuid);
                ps.setString(2, toolId);
                ps.setLong(3, expiresAt);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("recordSelfDestruct failed: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> purgeExpired(long now) {
        return runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM tf_self_destruct WHERE expires_at < ?")) {
                ps.setLong(1, now);
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().warning("purgeExpired failed: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<Void> updateRaw(Function<Connection, Void> action) {
        return runAsync(() -> action.apply(connection));
    }

    public void close() {
        pool.shutdown();
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }

    private CompletableFuture<Void> runAsync(Runnable r) {
        return CompletableFuture.runAsync(r, pool);
    }

    private <T> CompletableFuture<T> supplyAsync(java.util.function.Supplier<T> s) {
        return CompletableFuture.supplyAsync(s, pool);
    }

    public record StatEntry(String uuid, long value) {}
}
