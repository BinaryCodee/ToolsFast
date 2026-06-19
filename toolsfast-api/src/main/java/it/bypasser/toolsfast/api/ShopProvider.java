package it.bypasser.toolsfast.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ShopProvider {
    String id();
    double priceOf(Player player, ItemStack item);
    default void onRegister() {}
}
