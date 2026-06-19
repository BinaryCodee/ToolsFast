package it.bypasser.toolsfast.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface TokenEnchant {
    String id();
    String displayName();
    int maxLevel();
    void apply(Player player, ItemStack item, ItemMeta meta, int level);
    default boolean isTriggeredOnBreak() { return false; }
    default boolean isTriggeredOnInteract() { return false; }
}
