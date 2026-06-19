package it.bypasser.toolsfast.api;

import org.bukkit.entity.Player;

public interface PlaceholderProvider {
    String resolve(Player player);
}
