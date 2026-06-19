package it.bypasser.toolsfast.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ToolReceiveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String toolId;
    private ItemStack item;
    private boolean cancelled;

    public ToolReceiveEvent(Player player, String toolId, ItemStack item) {
        this.player = player;
        this.toolId = toolId;
        this.item = item;
    }

    public Player player() { return player; }
    public String toolId() { return toolId; }
    public ItemStack item() { return item; }
    public void item(ItemStack newItem) { this.item = newItem; }

    @Override public boolean isCancelled() { return cancelled; }
    @Override public void setCancelled(boolean cancel) { this.cancelled = cancel; }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
