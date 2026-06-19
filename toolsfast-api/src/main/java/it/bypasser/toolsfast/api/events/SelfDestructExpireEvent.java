package it.bypasser.toolsfast.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SelfDestructExpireEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final ItemStack item;
    private final String toolId;

    public SelfDestructExpireEvent(Player player, ItemStack item, String toolId) {
        this.player = player;
        this.item = item;
        this.toolId = toolId;
    }

    public Player player() { return player; }
    public ItemStack item() { return item; }
    public String toolId() { return toolId; }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
