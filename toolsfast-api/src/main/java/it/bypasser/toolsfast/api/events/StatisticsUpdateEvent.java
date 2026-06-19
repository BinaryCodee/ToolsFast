package it.bypasser.toolsfast.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class StatisticsUpdateEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final StatType type;
    private final long newValue;

    public StatisticsUpdateEvent(Player player, StatType type, long newValue) {
        this.player = player;
        this.type = type;
        this.newValue = newValue;
    }

    public Player player() { return player; }
    public StatType type() { return type; }
    public long newValue() { return newValue; }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }

    public enum StatType { BLOCKS, MONEY, TREES, CROPS, ITEMS_SOLD }
}
