package it.bypasser.toolsfast.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Optional;

public final class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemBuilder name(String name) {
        if (meta != null) meta.setDisplayName(Colors.color(name));
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        if (meta != null) meta.setLore(Colors.color(lines));
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(Math.max(1, Math.min(item.getMaxStackSize(), amount)));
        return this;
    }

    public ItemBuilder customModelData(int data) {
        if (meta != null) meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        if (meta != null) meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (meta != null && glow) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemMeta meta() { return meta; }

    public ItemStack build() {
        if (meta != null) item.setItemMeta(meta);
        return item;
    }

    public static Optional<ItemMeta> metaOrEmpty(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return Optional.empty();
        ItemMeta m = item.getItemMeta();
        return Optional.ofNullable(m);
    }
}
