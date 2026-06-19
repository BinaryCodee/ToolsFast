package it.bypasser.toolsfast.listeners;

import it.bypasser.toolsfast.ToolsFast;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class JoinQuitListener implements Listener {

    private final ToolsFast plugin;
    public JoinQuitListener(ToolsFast plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (plugin.statistics() != null && plugin.database() != null) {
            plugin.statistics().load(p.getUniqueId()).exceptionally(t -> {
                plugin.getLogger().warning("Failed to load stats for " + p.getName() + ": " + t.getMessage());
                return null;
            });
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if (plugin.statistics() != null) plugin.statistics().unload(p.getUniqueId());
        if (plugin.nms() != null) plugin.nms().removeBossBar(p);
    }
}
