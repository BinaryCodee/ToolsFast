package it.bypasser.toolsfast.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public final class Keys {

    private Keys() {}

    public static final NamespacedKey TOOL_ID = new NamespacedKey("toolsfast", "tool_id");
    public static final NamespacedKey TOOL_USES = new NamespacedKey("toolsfast", "tool_uses");
    public static final NamespacedKey SELF_DESTRUCT_END = new NamespacedKey("toolsfast", "sd_end");
    public static final NamespacedKey SELF_DESTRUCT_DELAYED = new NamespacedKey("toolsfast", "sd_delayed");
    public static final NamespacedKey SELF_DESTRUCT_DURATION = new NamespacedKey("toolsfast", "sd_duration");
    public static final NamespacedKey SELF_DESTRUCT_STARTED = new NamespacedKey("toolsfast", "sd_started");
    public static final NamespacedKey CUSTOM_ITEM_ID = new NamespacedKey("toolsfast", "custom_id");
    public static final NamespacedKey OWNER = new NamespacedKey("toolsfast", "owner");
    public static final NamespacedKey STATS_BLOCKS = new NamespacedKey("toolsfast", "blocks");
    public static final NamespacedKey STATS_TREES = new NamespacedKey("toolsfast", "trees");

    public static void setString(PersistentDataHolder holder, NamespacedKey key, String value) {
        if (holder == null) return;
        PersistentDataContainer c = holder.getPersistentDataContainer();
        c.set(key, PersistentDataType.STRING, value == null ? "" : value);
    }

    public static String getString(PersistentDataHolder holder, NamespacedKey key) {
        if (holder == null) return null;
        PersistentDataContainer c = holder.getPersistentDataContainer();
        return c.get(key, PersistentDataType.STRING);
    }

    public static void setLong(PersistentDataHolder holder, NamespacedKey key, long value) {
        if (holder == null) return;
        holder.getPersistentDataContainer().set(key, PersistentDataType.LONG, value);
    }

    public static long getLong(PersistentDataHolder holder, NamespacedKey key) {
        if (holder == null) return 0L;
        return holder.getPersistentDataContainer().getOrDefault(key, PersistentDataType.LONG, 0L);
    }

    public static boolean has(PersistentDataHolder holder, NamespacedKey key) {
        if (holder == null) return false;
        return holder.getPersistentDataContainer().has(key);
    }

    public static void remove(PersistentDataHolder holder, NamespacedKey key) {
        if (holder == null) return;
        holder.getPersistentDataContainer().remove(key);
    }

    public static String ownerString(UUID uuid) {
        return uuid == null ? "" : uuid.toString();
    }
}
