package it.bypasser.toolsfast.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface CustomItem {
    String id();
    ItemStack build(Player target, int amount);
    List<String> abilities();
    default int maxStackSize() { return 1; }
}
