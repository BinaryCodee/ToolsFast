package it.bypasser.toolsfast.listeners;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import it.bypasser.toolsfast.utils.Keys;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class BlockBreakListener implements Listener {

    private final ToolsFast plugin;
    public BlockBreakListener(ToolsFast plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        String toolId = plugin.toolRegistry().toolIdOf(item);
        if (toolId == null) return;

        if (plugin.selfDestruct().isDelayed(item)) {
            plugin.selfDestruct().activateIfDelayed(item);
            player.getInventory().setItemInMainHand(item);
        }

        var def = plugin.toolRegistry().get(toolId);
        if (def != null) {
            for (String ab : def.abilities()) {
                Ability ability = plugin.abilityRegistry().get(ab);
                if (ability != null) {
                    try { ability.onBreak(event, item); } catch (Throwable t) {
                        plugin.getLogger().warning("Ability " + ab + " failed: " + t.getMessage());
                    }
                }
            }
            plugin.statistics().addBlocks(player.getUniqueId(), 1);
        }

        if (plugin.config().get().getBoolean("particles.enable-on-break", true)) {
            String particle = plugin.config().get().getString("particles.break.default", "END_ROD");
            int count = plugin.config().get().getInt("particles.break.count", 8);
            plugin.particles().spawn(event.getBlock().getLocation().add(0.5, 0.5, 0.5), particle, count, 0.3, 0.3, 0.3, 0.02);
        }

        Keys.setLong(meta, Keys.TOOL_USES, Keys.getLong(meta, Keys.TOOL_USES) + 1);
        item.setItemMeta(meta);
        if (plugin.database() != null) {
            plugin.database().incrementToolUse(player.getUniqueId().toString(), toolId);
        }
    }
}
