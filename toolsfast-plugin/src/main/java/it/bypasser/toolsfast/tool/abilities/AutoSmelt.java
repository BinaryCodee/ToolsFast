package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class AutoSmelt implements Ability {

    private final ToolsFast plugin;
    public AutoSmelt(ToolsFast plugin) { this.plugin = plugin; }

    @Override public String id() { return "AUTO_SMELT"; }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        Collection<ItemStack> drops = event.getBlock().getDrops(tool);
        List<ItemStack> smelted = new ArrayList<>();
        for (ItemStack d : drops) {
            Material m = d.getType();
            Material smelt = smeltOf(m);
            if (smelt != m) smelted.add(new ItemStack(smelt, d.getAmount()));
            else smelted.add(d);
        }
        event.setDropItems(false);
        for (ItemStack d : smelted) {
            var leftover = player.getInventory().addItem(d);
            for (ItemStack left : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), left);
            }
        }
    }

    private Material smeltOf(Material m) {
        return switch (m) {
            case IRON_ORE, DEEPSLATE_IRON_ORE, RAW_IRON -> Material.IRON_INGOT;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE, RAW_GOLD -> Material.GOLD_INGOT;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
            case NETHER_GOLD_ORE -> Material.GOLD_INGOT;
            case ANCIENT_DEBRIS -> Material.NETHERITE_SCRAP;
            case SAND, RED_SAND -> Material.GLASS;
            case COBBLESTONE -> Material.STONE;
            case CLAY_BALL -> Material.BRICK;
            case NETHERRACK -> Material.NETHER_BRICK;
            case CACTUS -> Material.GREEN_DYE;
            case KELP -> Material.DRIED_KELP;
            default -> m;
        };
    }
}
