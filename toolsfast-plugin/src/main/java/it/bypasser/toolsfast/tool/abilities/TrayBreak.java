package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public final class TrayBreak implements Ability {

    private final ToolsFast plugin;
    public TrayBreak(ToolsFast plugin) { this.plugin = plugin; }

    @Override public String id() { return "TRAY_BREAK"; }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        if (event.isCancelled()) return;
        Block origin = event.getBlock();
        Player player = event.getPlayer();
        int radius = plugin.config().get().getInt("abilities.tray.radius", 2);
        int broken = 0;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                Block b = origin.getRelative(x, 0, z);
                if (b.getType().isAir() || b.getType() == Material.BEDROCK) continue;
                if (!plugin.hooks().canBuild(player, b.getLocation())) continue;
                var drops = b.getDrops(tool);
                for (ItemStack d : drops) {
                    var leftover = player.getInventory().addItem(d);
                    for (ItemStack l : leftover.values()) b.getWorld().dropItemNaturally(b.getLocation(), l);
                }
                b.setType(Material.AIR, true);
                broken++;
            }
        }
        if (broken > 0) {
            plugin.statistics().addBlocks(player.getUniqueId(), broken);
        }
    }
}
