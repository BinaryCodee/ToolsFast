package it.bypasser.toolsfast.listeners;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.utils.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class InventoryListener implements Listener {

    private final ToolsFast plugin;
    public InventoryListener(ToolsFast plugin) { this.plugin = plugin; }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack cursor = event.getCursor();
        ItemStack current = event.getCurrentItem();
        if (cursor != null) checkDelayed(player, cursor);
        if (current != null) checkDelayed(player, current);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        checkDelayed(event.getPlayer(), event.getItem());
    }

    private void checkDelayed(Player player, ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (plugin.selfDestruct().hasSelfDestruct(item)) {
            plugin.selfDestruct().updateLore(item);
        }
    }
}
