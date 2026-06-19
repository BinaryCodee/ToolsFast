package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public final class AutoReplant implements Ability {

    private final ToolsFast plugin;
    private static final Set<Material> CROPS = EnumSet.of(
            Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.NETHER_WART);

    public AutoReplant(ToolsFast plugin) { this.plugin = plugin; }

    @Override public String id() { return "AUTO_REPLANT"; }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        if (event.isCancelled()) return;
        Block b = event.getBlock();
        if (!CROPS.contains(b.getType())) return;
        Player player = event.getPlayer();
        Material seed = seedOf(b.getType());
        boolean mature = isMature(b);
        if (mature) {
            event.setDropItems(false);
            for (ItemStack d : b.getDrops(tool)) {
                var leftover = player.getInventory().addItem(d);
                for (ItemStack l : leftover.values()) player.getWorld().dropItemNaturally(b.getLocation(), l);
            }
            plugin.statistics().addCrops(player.getUniqueId(), 1);
        }
        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (b.getType().isAir()) b.setType(seed);
        }, 1L);
    }

    private boolean isMature(Block b) {
        if (b.getBlockData() instanceof Ageable ageable) {
            return ageable.getAge() == ageable.getMaximumAge();
        }
        return true;
    }

    private Material seedOf(Material crop) {
        return switch (crop) {
            case WHEAT -> Material.WHEAT_SEEDS;
            case CARROTS -> Material.CARROT;
            case POTATOES -> Material.POTATO;
            case BEETROOTS -> Material.BEETROOT_SEEDS;
            case NETHER_WART -> Material.NETHER_WART;
            default -> crop;
        };
    }
}
