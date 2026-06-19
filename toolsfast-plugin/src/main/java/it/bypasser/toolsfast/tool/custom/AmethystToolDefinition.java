package it.bypasser.toolsfast.tool.custom;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.ToolDefinition;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class AmethystToolDefinition implements ToolDefinition {

    private final ToolsFast plugin;
    private final String id;
    private final String displayName;
    private final Material material;
    private final List<String> lore;
    private final List<String> abilities;
    private final boolean unbreakable;
    private final int customModelData;

    public AmethystToolDefinition(ToolsFast plugin, String id, ConfigurationSection cfg) {
        this.plugin = plugin;
        this.id = id;
        this.displayName = cfg.getString("name", "&#C77DFF&l" + id.replace("_", " ").toUpperCase());
        String mat = cfg.getString("material", "NETHERITE_PICKAXE");
        Material m;
        try { m = Material.valueOf(mat.toUpperCase()); }
        catch (Exception e) { m = Material.NETHERITE_PICKAXE; }
        this.material = m;
        this.lore = cfg.getStringList("lore");
        this.abilities = cfg.getStringList("abilities");
        this.unbreakable = cfg.getBoolean("unbreakable", true);
        this.customModelData = cfg.getInt("custom-model-data", -1);
    }

    public AmethystToolDefinition(ToolsFast plugin, String id, Material material, String displayName,
                                  List<String> lore, List<String> abilities) {
        this.plugin = plugin;
        this.id = id;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.abilities = abilities;
        this.unbreakable = true;
        this.customModelData = -1;
    }

    @Override public String id() { return id; }
    @Override public String displayName() { return displayName; }
    @Override public List<String> abilities() { return abilities; }

    @Override
    public org.bukkit.inventory.ItemStack build(org.bukkit.entity.Player target) {
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(it.bypasser.toolsfast.utils.Colors.color(displayName));
            List<String> finalLore = new java.util.ArrayList<>(lore);
            if (target != null) {
                finalLore = it.bypasser.toolsfast.utils.Colors.replace(finalLore, "%blocks%",
                        String.valueOf(plugin.statistics().getBlocks(target.getUniqueId())));
                finalLore = it.bypasser.toolsfast.utils.Colors.replace(finalLore, "%trees%",
                        String.valueOf(plugin.statistics().getTrees(target.getUniqueId())));
                finalLore = it.bypasser.toolsfast.utils.Colors.replace(finalLore, "%durability%",
                        String.valueOf(material.getMaxDurability() - it.bypasser.toolsfast.utils.Keys.getLong(meta, it.bypasser.toolsfast.utils.Keys.TOOL_USES)));
            }
            meta.setLore(it.bypasser.toolsfast.utils.Colors.color(finalLore));
            meta.setUnbreakable(unbreakable);
            if (customModelData >= 0) meta.setCustomModelData(customModelData);
            it.bypasser.toolsfast.utils.Keys.setString(meta, it.bypasser.toolsfast.utils.Keys.TOOL_ID, id);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public boolean matches(org.bukkit.inventory.ItemStack item) {
        if (item == null) return false;
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        String tid = it.bypasser.toolsfast.utils.Keys.getString(meta, it.bypasser.toolsfast.utils.Keys.TOOL_ID);
        return id.equalsIgnoreCase(tid);
    }
}
