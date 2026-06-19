package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.CustomItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CustomItemManagerImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.CustomItemManager {

    private final ToolsFast plugin;
    private final Map<String, CustomItem> items = new LinkedHashMap<>();
    private final Map<String, CustomItem> external = new LinkedHashMap<>();

    public CustomItemManagerImpl(ToolsFast plugin) {
        this.plugin = plugin;
    }

    public synchronized void register(CustomItem item) {
        if (item == null) return;
        external.put(item.id().toLowerCase(), item);
    }

    public synchronized void load() {
        items.clear();
        File dir = new File(plugin.getDataFolder(), "custom-items");
        if (!dir.exists()) {
            dir.mkdirs();
            plugin.saveResource("custom-items/amethyst_drill.yml", true);
            plugin.saveResource("custom-items/amethyst_hammer.yml", true);
            plugin.saveResource("custom-items/amethyst_axe.yml", true);
            plugin.saveResource("custom-items/amethyst_shovel.yml", true);
            plugin.saveResource("custom-items/amethyst_bucket.yml", true);
            plugin.saveResource("custom-items/amethyst_sell_axe.yml", true);
            plugin.saveResource("custom-items/amethyst_multi_tool.yml", true);
            plugin.saveResource("custom-items/infinite_firework.yml", true);
            plugin.saveResource("custom-items/harvester_hoe.yml", true);
            plugin.saveResource("custom-items/trench_pickaxe.yml", true);
            plugin.saveResource("custom-items/tray_pickaxe.yml", true);
            plugin.saveResource("custom-items/sand_wand.yml", true);
            plugin.saveResource("custom-items/ice_wand.yml", true);
            plugin.saveResource("custom-items/craft_wand.yml", true);
            plugin.saveResource("custom-items/sell_wand.yml", true);
            plugin.saveResource("custom-items/lightning_wand.yml", true);
        }
        File[] files = dir.listFiles((d, n) -> n.endsWith(".yml"));
        if (files == null) return;
        for (File f : files) {
            try {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                CustomItemImpl item = new CustomItemImpl(cfg);
                items.put(item.id().toLowerCase(), item);
            } catch (Exception e) {
                plugin.getLogger().warning("Cannot load custom item " + f.getName() + ": " + e.getMessage());
            }
        }
        plugin.getLogger().info("Loaded " + items.size() + " custom items.");
    }

    @Override
    public synchronized CustomItem get(String id) {
        if (id == null) return null;
        CustomItem i = items.get(id.toLowerCase());
        if (i == null) i = external.get(id.toLowerCase());
        return i;
    }

    @Override
    public synchronized List<CustomItem> all() {
        List<CustomItem> out = new ArrayList<>(items.values());
        out.addAll(external.values());
        return out;
    }

    @Override
    public void reload() { load(); }

    public Collection<CustomItem> builtin() { return items.values(); }

    public static final class CustomItemImpl implements CustomItem {

        private final String id;
        private final org.bukkit.Material material;
        private final String name;
        private final List<String> lore;
        private final List<String> abilities;
        private final int customModelData;
        private final boolean unbreakable;
        private final boolean glow;
        private final int amount;

        public CustomItemImpl(ConfigurationSection cfg) {
            this.id = cfg.getString("id", "unknown").toLowerCase();
            String matName = cfg.getString("material", "STONE");
            org.bukkit.Material m;
            try { m = org.bukkit.Material.valueOf(matName.toUpperCase()); }
            catch (Exception e) { m = org.bukkit.Material.STONE; }
            this.material = m;
            this.name = cfg.getString("name", "&f" + id);
            this.lore = cfg.getStringList("lore");
            this.abilities = cfg.getStringList("abilities");
            this.customModelData = cfg.getInt("custom-model-data", -1);
            this.unbreakable = cfg.getBoolean("unbreakable", false);
            this.glow = cfg.getBoolean("glow", false);
            this.amount = cfg.getInt("amount", 1);
        }

        @Override public String id() { return id; }
        @Override public List<String> abilities() { return abilities; }
        @Override public int maxStackSize() { return material.getMaxStackSize(); }

        @Override
        public ItemStack build(Player target, int amount) {
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material, Math.max(1, amount));
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(it.bypasser.toolsfast.utils.Colors.color(name));
                meta.setLore(it.bypasser.toolsfast.utils.Colors.color(lore));
                if (customModelData >= 0) meta.setCustomModelData(customModelData);
                meta.setUnbreakable(unbreakable);
                if (glow) {
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
                    meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                }
                it.bypasser.toolsfast.utils.Keys.setString(meta, it.bypasser.toolsfast.utils.Keys.CUSTOM_ITEM_ID, id);
                item.setItemMeta(meta);
            }
            return item;
        }
    }
}
