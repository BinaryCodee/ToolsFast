package it.bypasser.toolsfast.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class SellEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final int itemsSold;
    private final double money;

    public SellEvent(Player player, int itemsSold, double money) {
        this.player = player;
        this.itemsSold = itemsSold;
        this.money = money;
    }

    public Player player() { return player; }
    public int itemsSold() { return itemsSold; }
    public double money() { return money; }

    @Override public @NotNull HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
