package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class AutoPickup implements Ability {

    private final ToolsFast plugin;
    public AutoPickup(ToolsFast plugin) { this.plugin = plugin; }

    @Override public String id() { return "AUTO_PICKUP"; }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        var drops = event.getBlock().getDrops(tool);
        for (ItemStack d : drops) {
            var leftover = player.getInventory().addItem(d);
            for (ItemStack left : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), left);
            }
        }
        event.setDropItems(false);
        event.setExpToDrop(0);
    }
}
