package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.ToolDefinition;
import it.bypasser.toolsfast.utils.Keys;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ToolRegistryImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.ToolRegistry {

    private final ToolsFast plugin;
    private final Map<String, ToolDefinition> tools = new LinkedHashMap<>();

    public ToolRegistryImpl(ToolsFast plugin) {
        this.plugin = plugin;
    }

    @Override
    public synchronized void register(ToolDefinition tool) {
        if (tool == null) return;
        tools.put(tool.id().toLowerCase(), tool);
    }

    @Override
    public synchronized ToolDefinition get(String id) {
        if (id == null) return null;
        return tools.get(id.toLowerCase());
    }

    @Override
    public synchronized List<ToolDefinition> all() {
        return new ArrayList<>(tools.values());
    }

    public synchronized boolean matches(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return Keys.has(meta, Keys.TOOL_ID) || Keys.has(meta, Keys.CUSTOM_ITEM_ID);
    }

    public synchronized String toolIdOf(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        String id = Keys.getString(meta, Keys.TOOL_ID);
        if (id == null || id.isEmpty()) id = Keys.getString(meta, Keys.CUSTOM_ITEM_ID);
        return id;
    }

    public void reload() {
        plugin.tools().reload();
        ConfigurationSection section = plugin.tools().get().getConfigurationSection("tools");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            ConfigurationSection sec = section.getConfigurationSection(key);
            if (sec == null) continue;
            YamlToolDefinition def = new YamlToolDefinition(key, sec);
            tools.put(key.toLowerCase(), def);
        }
    }

    public Collection<ToolDefinition> definitions() { return tools.values(); }

    public static final class YamlToolDefinition implements ToolDefinition {

        private final String id;
        private final String displayName;
        private final org.bukkit.Material material;
        private final List<String> lore;
        private final List<String> abilities;
        private final int customModelData;
        private final boolean unbreakable;

        public YamlToolDefinition(String id, ConfigurationSection cfg) {
            this.id = id;
            this.displayName = cfg.getString("name", "&f" + id);
            String matName = cfg.getString("material", "STONE");
            org.bukkit.Material m;
            try { m = org.bukkit.Material.valueOf(matName.toUpperCase()); }
            catch (Exception e) { m = org.bukkit.Material.STONE; }
            this.material = m;
            this.lore = cfg.getStringList("lore");
            this.abilities = cfg.getStringList("abilities");
            this.customModelData = cfg.getInt("custom-model-data", -1);
            this.unbreakable = cfg.getBoolean("unbreakable", true);
        }

        @Override public String id() { return id; }
        @Override public String displayName() { return displayName; }
        @Override public List<String> abilities() { return abilities; }

        @Override
        public ItemStack build(Player target) {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(it.bypasser.toolsfast.utils.Colors.color(displayName));
                if (!lore.isEmpty()) meta.setLore(it.bypasser.toolsfast.utils.Colors.color(lore));
                if (customModelData >= 0) meta.setCustomModelData(customModelData);
                meta.setUnbreakable(unbreakable);
                it.bypasser.toolsfast.utils.Keys.setString(meta, it.bypasser.toolsfast.utils.Keys.TOOL_ID, id);
                item.setItemMeta(meta);
            }
            return item;
        }

        @Override
        public boolean matches(ItemStack item) {
            if (item == null) return false;
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return false;
            String tid = it.bypasser.toolsfast.utils.Keys.getString(meta, it.bypasser.toolsfast.utils.Keys.TOOL_ID);
            return id.equalsIgnoreCase(tid);
        }
    }
}
