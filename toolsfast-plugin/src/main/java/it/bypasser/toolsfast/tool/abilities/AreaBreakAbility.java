package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import it.bypasser.toolsfast.utils.Keys;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.EnumSet;
import java.util.Set;

public abstract class AreaBreakAbility implements Ability {

    protected final ToolsFast plugin;
    protected final int radius;

    protected AreaBreakAbility(ToolsFast plugin, int radius) {
        this.plugin = plugin;
        this.radius = radius;
    }

    @Override
    public void onBreak(BlockBreakEvent event, ItemStack tool) {
        if (event.isCancelled()) return;
        Block origin = event.getBlock();
        Player player = event.getPlayer();
        if (!plugin.hooks().canBuild(player, origin.getLocation())) return;
        if (plugin.config().get().getBoolean("abilities.require-sneak", false) && !player.isSneaking()) return;

        BlockFace face = getFacing(player);
        int fortune = tool.getEnchantmentLevel(Enchantment.FORTUNE);
        boolean silk = tool.getEnchantmentLevel(Enchantment.SILK_TOUCH) > 0;
        boolean autoSmelt = hasAbility(tool, "AUTO_SMELT");
        boolean autoPickup = hasAbility(tool, "AUTO_PICKUP");

        int broken = 0;
        for (Block b : getAreaBlocks(origin, face)) {
            if (b.getType().isAir() || b.getType() == Material.BEDROCK) continue;
            if (b.equals(origin)) continue;
            if (b.getType().getHardness() < 0) continue;
            if (!plugin.hooks().canBuild(player, b.getLocation())) continue;
            if (!isMineable(b.getType())) continue;
            java.util.Collection<ItemStack> drops = b.getDrops(tool);
            if (silk && isSilkable(b.getType())) {
                drops = java.util.List.of(new ItemStack(b.getType()));
            } else if (autoSmelt) {
                drops = applySmelt(drops);
            } else if (fortune > 0) {
                drops = applyFortune(drops, b.getType(), fortune);
            }
            if (autoPickup) {
                giveOrDrop(player, drops);
            } else {
                for (ItemStack d : drops) b.getWorld().dropItemNaturally(b.getLocation(), d);
            }
            b.setType(Material.AIR, true);
            plugin.statistics().addBlocks(player.getUniqueId(), 1);
            broken++;
        }
        if (broken > 0) {
            String msg = plugin.messages().message("actionbar.mining").replace("%blocks%", String.valueOf(broken));
            if (!msg.isEmpty()) {
                if (plugin.nms() != null) plugin.nms().sendActionBar(player, msg);
                else player.sendMessage(msg);
            }
        }
    }

    protected boolean hasAbility(ItemStack tool, String id) {
        if (tool == null) return false;
        ItemMeta meta = tool.getItemMeta();
        if (meta == null) return false;
        String idStr = Keys.getString(meta, Keys.TOOL_ID);
        if (idStr == null) return false;
        var def = plugin.toolRegistry().get(idStr);
        if (def == null) return false;
        return def.abilities().stream().anyMatch(a -> a.equalsIgnoreCase(id));
    }

    protected void giveOrDrop(Player player, java.util.Collection<ItemStack> drops) {
        for (ItemStack d : drops) {
            if (d == null || d.getType().isAir()) continue;
            java.util.Map<Integer, ItemStack> leftover = player.getInventory().addItem(d);
            for (ItemStack left : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), left);
            }
        }
    }

    protected java.util.Collection<ItemStack> applySmelt(java.util.Collection<ItemStack> drops) {
        java.util.List<ItemStack> out = new java.util.ArrayList<>();
        for (ItemStack d : drops) {
            Material m = d.getType();
            Material smelted = switch (m) {
                case IRON_ORE, DEEPSLATE_IRON_ORE, RAW_IRON -> Material.IRON_INGOT;
                case GOLD_ORE, DEEPSLATE_GOLD_ORE, RAW_GOLD -> Material.GOLD_INGOT;
                case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
                case NETHER_GOLD_ORE -> Material.GOLD_INGOT;
                case ANCIENT_DEBRIS -> Material.NETHERITE_SCRAP;
                case SAND -> Material.GLASS;
                case RED_SAND -> Material.GLASS;
                case COBBLESTONE -> Material.STONE;
                case CLAY_BALL -> Material.BRICK;
                case NETHERRACK -> Material.NETHER_BRICK;
                case CACTUS -> Material.GREEN_DYE;
                case KELP -> Material.DRIED_KELP;
                case STONE -> Material.STONE;
                default -> m;
            };
            if (smelted != m) {
                ItemStack n = new ItemStack(smelted, d.getAmount());
                out.add(n);
            } else {
                out.add(d);
            }
        }
        return out;
    }

    protected java.util.Collection<ItemStack> applyFortune(java.util.Collection<ItemStack> drops, Material blockType, int fortune) {
        java.util.List<ItemStack> out = new java.util.ArrayList<>();
        for (ItemStack d : drops) {
            int amount = d.getAmount();
            if (isFortunable(blockType)) {
                int bonus = (int) (Math.random() * (fortune + 2));
                amount = d.getAmount() + bonus;
            }
            out.add(new ItemStack(d.getType(), amount));
        }
        return out;
    }

    protected boolean isFortunable(Material m) {
        return m == Material.COAL_ORE || m == Material.DIAMOND_ORE || m == Material.EMERALD_ORE ||
                m == Material.LAPIS_ORE || m == Material.REDSTONE_ORE || m == Material.NETHER_QUARTZ_ORE ||
                m == Material.DEEPSLATE_COAL_ORE || m == Material.DEEPSLATE_DIAMOND_ORE ||
                m == Material.DEEPSLATE_EMERALD_ORE || m == Material.DEEPSLATE_LAPIS_ORE ||
                m == Material.DEEPSLATE_REDSTONE_ORE;
    }

    protected boolean isSilkable(Material m) {
        return m.name().endsWith("_ORE") || m == Material.GLASS || m == Material.GRASS_BLOCK ||
                m == Material.MYCELIUM || m == Material.PODZOL || m == Material.STONE ||
                m == Material.COBBLESTONE;
    }

    protected boolean isMineable(Material m) {
        String n = m.name();
        return n.endsWith("_ORE") || n.endsWith("_STONE") || n.endsWith("_LOG") || n.endsWith("_WOOD") ||
                n.endsWith("_LEAVES") || m == Material.STONE || m == Material.COBBLESTONE || m == Material.DIRT ||
                m == Material.GRASS_BLOCK || m == Material.SAND || m == Material.GRAVEL || m == Material.NETHERRACK ||
                m == Material.END_STONE || m == Material.OBSIDIAN || m == Material.BASALT || m == Material.BLACKSTONE ||
                m == Material.DEEPSLATE || m == Material.CLAY || m == Material.SNOW_BLOCK || m == Material.ICE ||
                m == Material.PACKED_ICE || m == Material.BLUE_ICE || m == Material.NETHER_QUARTZ_ORE ||
                m == Material.NETHER_GOLD_ORE || m == Material.ANCIENT_DEBRIS;
    }

    protected BlockFace getFacing(Player player) {
        float pitch = player.getLocation().getPitch();
        if (pitch > 60) return BlockFace.DOWN;
        if (pitch < -60) return BlockFace.UP;
        return player.getFacing();
    }

    protected java.util.List<Block> getAreaBlocks(Block origin, BlockFace face) {
        java.util.List<Block> blocks = new java.util.ArrayList<>();
        int r = radius;
        boolean vertical = face == BlockFace.UP || face == BlockFace.DOWN;
        boolean xAxis = face == BlockFace.EAST || face == BlockFace.WEST;
        boolean zAxis = face == BlockFace.NORTH || face == BlockFace.SOUTH;
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    if (vertical) {
                        blocks.add(origin.getRelative(x, 0, z));
                    } else if (xAxis) {
                        if (y == 0 && z == 0 && x == 0) continue;
                        blocks.add(origin.getRelative(0, y, z));
                    } else if (zAxis) {
                        if (y == 0 && x == 0 && z == 0) continue;
                        blocks.add(origin.getRelative(x, y, 0));
                    }
                }
            }
        }
        return blocks;
    }

    protected static final Set<Material> SHOVELY = EnumSet.of(
            Material.DIRT, Material.GRASS_BLOCK, Material.SAND, Material.GRAVEL, Material.CLAY, Material.CLAY_BALL,
            Material.SNOW_BLOCK, Material.SNOW, Material.PACKED_ICE, Material.BLUE_ICE, Material.SOUL_SAND,
            Material.SOUL_SOIL, Material.RED_SAND, Material.COARSE_DIRT, Material.PODZOL, Material.MYCELIUM,
            Material.ROOTED_DIRT, Material.MOSS_BLOCK, Material.MUD, Material.MUDDY_MANGROVE_ROOTS);
}
