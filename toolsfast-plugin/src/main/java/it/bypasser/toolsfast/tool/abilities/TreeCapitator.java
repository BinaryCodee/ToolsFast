package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class TreeCapitator implements Ability {

    private final ToolsFast plugin;
    private static final Set<Material> LOGS = EnumSet.of(
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG, Material.JUNGLE_LOG,
            Material.ACACIA_LOG, Material.DARK_OAK_LOG, Material.MANGROVE_LOG, Material.CHERRY_LOG,
            Material.CRIMSON_STEM, Material.WARPED_STEM,
            Material.STRIPPED_OAK_LOG, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_SPRUCE_LOG,
            Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_MANGROVE_LOG, Material.STRIPPED_CHERRY_LOG,
            Material.STRIPPED_CRIMSON_STEM, Material.STRIPPED_WARPED_STEM);
    private static final Set<Material> LEAVES = EnumSet.of(
            Material.OAK_LEAVES, Material.BIRCH_LEAVES, Material.SPRUCE_LEAVES, Material.JUNGLE_LEAVES,
            Material.ACACIA_LEAVES, Material.DARK_OAK_LEAVES, Material.MANGROVE_LEAVES, Material.CHERRY_LEAVES,
            Material.AZALEA_LEAVES, Material.FLOWERING_AZALEA_LEAVES,
            Material.NETHER_WART_BLOCK, Material.WARPED_WART_BLOCK,
            Material.CRIMSON_HYPHAE, Material.WARPED_HYPHAE);

    public TreeCapitator(ToolsFast plugin) { this.plugin = plugin; }

    @Override public String id() { return "TREE_CAPITATOR"; }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        if (event.isCancelled()) return;
        Block origin = event.getBlock();
        if (!LOGS.contains(origin.getType())) return;
        Player player = event.getPlayer();
        int maxBlocks = plugin.config().get().getInt("abilities.tree-capitator.max-blocks", 500);
        Set<Block> visited = new HashSet<>();
        Deque<Block> queue = new ArrayDeque<>();
        queue.add(origin);
        int broken = 0;
        while (!queue.isEmpty() && broken < maxBlocks) {
            Block b = queue.poll();
            if (!visited.add(b)) continue;
            if (!LOGS.contains(b.getType()) && !LEAVES.contains(b.getType())) continue;
            if (!plugin.hooks().canBuild(player, b.getLocation())) continue;
            if (!b.equals(origin)) {
                var drops = b.getDrops(tool);
                for (ItemStack d : drops) {
                    var leftover = player.getInventory().addItem(d);
                    for (ItemStack left : leftover.values()) {
                        player.getWorld().dropItemNaturally(b.getLocation(), left);
                    }
                }
                b.setType(Material.AIR, true);
                broken++;
            }
            for (BlockFace f : new BlockFace[]{BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.DOWN, BlockFace.NORTH_EAST, BlockFace.NORTH_WEST, BlockFace.SOUTH_EAST, BlockFace.SOUTH_WEST}) {
                Block rel = b.getRelative(f);
                if (LOGS.contains(rel.getType()) || LEAVES.contains(rel.getType())) {
                    queue.add(rel);
                }
            }
        }
        if (broken > 0) {
            plugin.statistics().addTrees(player.getUniqueId(), 1);
            String msg = plugin.messages().message("actionbar.tree");
            if (!msg.isEmpty() && plugin.nms() != null) plugin.nms().sendActionBar(player, msg);
            plugin.particles().spawn(origin.getLocation().add(0.5, 0.5, 0.5), "HAPPY_VILLAGER", 20, 0.6, 0.6, 0.6, 0.1);
        }
    }
}
