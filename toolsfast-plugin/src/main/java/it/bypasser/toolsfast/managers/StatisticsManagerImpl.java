package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.events.StatisticsUpdateEvent;
import it.bypasser.toolsfast.api.events.StatisticsUpdateEvent.StatType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class StatisticsManagerImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.StatisticsManager {

    private final ToolsFast plugin;
    private final it.bypasser.toolsfast.database.Database db;
    private final Map<UUID, Long> blocksCache = new ConcurrentHashMap<>();
    private final Map<UUID, Long> moneyCache = new ConcurrentHashMap<>();
    private final Map<UUID, Long> treesCache = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cropsCache = new ConcurrentHashMap<>();
    private final Map<UUID, Long> itemsSoldCache = new ConcurrentHashMap<>();

    public StatisticsManagerImpl(ToolsFast plugin, it.bypasser.toolsfast.database.Database db) {
        this.plugin = plugin;
        this.db = db;
    }

    @Override public long getBlocks(UUID p) { return blocksCache.getOrDefault(p, 0L); }
    @Override public long getMoney(UUID p) { return moneyCache.getOrDefault(p, 0L); }
    @Override public long getTrees(UUID p) { return treesCache.getOrDefault(p, 0L); }
    @Override public long getCrops(UUID p) { return cropsCache.getOrDefault(p, 0L); }
    @Override public long getItemsSold(UUID p) { return itemsSoldCache.getOrDefault(p, 0L); }

    @Override public void addBlocks(UUID p, long amount) { update(p, amount, "blocks", blocksCache, StatType.BLOCKS); }
    @Override public void addMoney(UUID p, long amount) { update(p, amount, "money", moneyCache, StatType.MONEY); }
    @Override public void addTrees(UUID p, long amount) { update(p, amount, "trees", treesCache, StatType.TREES); }
    @Override public void addCrops(UUID p, long amount) { update(p, amount, "crops", cropsCache, StatType.CROPS); }
    @Override public void addItemsSold(UUID p, long amount) { update(p, amount, "items_sold", itemsSoldCache, StatType.ITEMS_SOLD); }

    private void update(UUID uuid, long amount, String column, Map<UUID, Long> cache, StatType type) {
        long newVal = cache.merge(uuid, amount, Long::sum);
        if (db != null) db.incrementStat(uuid.toString(), column, amount);
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) {
            Bukkit.getPluginManager().callEvent(new StatisticsUpdateEvent(p, type, newVal));
        }
    }

    public CompletableFuture<Void> load(UUID uuid) {
        if (db == null) return CompletableFuture.completedFuture(null);
        return CompletableFuture.allOf(
                db.getStat(uuid.toString(), "blocks").thenAccept(v -> blocksCache.put(uuid, v)),
                db.getStat(uuid.toString(), "money").thenAccept(v -> moneyCache.put(uuid, v)),
                db.getStat(uuid.toString(), "trees").thenAccept(v -> treesCache.put(uuid, v)),
                db.getStat(uuid.toString(), "crops").thenAccept(v -> cropsCache.put(uuid, v)),
                db.getStat(uuid.toString(), "items_sold").thenAccept(v -> itemsSoldCache.put(uuid, v))
        );
    }

    public void unload(UUID uuid) {
        blocksCache.remove(uuid);
        moneyCache.remove(uuid);
        treesCache.remove(uuid);
        cropsCache.remove(uuid);
        itemsSoldCache.remove(uuid);
    }

    @Override
    public Map<UUID, Long> topBlocks(int limit) {
        return topSync("blocks", limit);
    }

    @Override
    public Map<UUID, Long> topMoney(int limit) {
        return topSync("money", limit);
    }

    @Override
    public Map<UUID, Long> topTrees(int limit) {
        return topSync("trees", limit);
    }

    private Map<UUID, Long> topSync(String column, int limit) {
        if (db == null) return java.util.Collections.emptyMap();
        try {
            List<it.bypasser.toolsfast.database.Database.StatEntry> entries = db.topBy(column, limit).join();
            Map<UUID, Long> out = new java.util.LinkedHashMap<>();
            for (var e : entries) {
                try {
                    out.put(UUID.fromString(e.uuid()), e.value());
                } catch (IllegalArgumentException ignored) {}
            }
            return out;
        } catch (Throwable t) {
            return java.util.Collections.emptyMap();
        }
    }
}
