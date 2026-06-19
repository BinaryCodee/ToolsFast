package it.bypasser.toolsfast.api;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public interface Ability {
    String id();
    void onBreak(BlockBreakEvent event, ItemStack tool);
    default void onInteract(PlayerInteractEvent event, ItemStack tool) {}
    default void onLoad() {}
    default void onUnload() {}
}
