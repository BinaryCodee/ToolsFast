package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class TrenchBreak implements Ability {

    private final ToolsFast plugin;
    public TrenchBreak(ToolsFast plugin) { this.plugin = plugin; }

    @Override public String id() { return "TRENCH_BREAK"; }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        if (event.isCancelled()) return;
        Block origin = event.getBlock();
        Player player = event.getPlayer();
        int size = plugin.config().get().getInt("abilities.trench.size", 3);
        int r = (size - 1) / 2;
        int broken = 0;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Block b = origin.getRelative(x, y, z);
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
        }
        if (broken > 0) {
            plugin.statistics().addBlocks(player.getUniqueId(), broken);
        }
    }
}
