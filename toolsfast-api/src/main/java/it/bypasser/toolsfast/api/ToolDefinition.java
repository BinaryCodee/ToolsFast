package it.bypasser.toolsfast.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public interface ToolDefinition {
    String id();
    String displayName();
    ItemStack build(Player target);
    List<String> abilities();
    boolean matches(ItemStack item);
    default Consumer<Player> onReceive() { return null; }
}
