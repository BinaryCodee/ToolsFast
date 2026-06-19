package it.bypasser.toolsfast.listeners;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.utils.Keys;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class InteractListener implements Listener {

    private final ToolsFast plugin;
    public InteractListener(ToolsFast plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String toolId = plugin.toolRegistry().toolIdOf(item);
        if (toolId == null) return;
        Block clicked = event.getClickedBlock();
        if (clicked == null) return;

        if (plugin.selfDestruct().isDelayed(item)) {
            plugin.selfDestruct().activateIfDelayed(item);
            player.getInventory().setItemInMainHand(item);
        }

        switch (toolId) {
            case "amethyst_sell_axe", "sell_wand" -> handleSell(player, clicked, item);
            case "lightning_wand" -> handleLightning(player, clicked);
            case "ice_wand" -> handleIceWand(player, clicked);
            case "sand_wand" -> handleSandWand(player, clicked);
            case "craft_wand" -> handleCraftWand(player);
            case "amethyst_bucket" -> handleBucket(player, clicked);
            case "infinite_firework" -> handleFirework(player, event);
        }
    }

    private void handleSell(Player player, Block block, ItemStack tool) {
        if (!(block.getState() instanceof Container container)) return;
        Inventory inv = container.getInventory();
        ItemStack[] contents = inv.getContents().clone();
        double total = plugin.shopProviders().sellAll(player, contents);
        if (total > 0) {
            inv.setContents(contents);
            for (String s : plugin.messages().messages("actionbar.sell")) {
                String msg = s.replace("%money%", String.format("%.2f", total));
                if (plugin.nms() != null) plugin.nms().sendActionBar(player, msg);
                else player.sendMessage(msg);
            }
            plugin.particles().spawn(block.getLocation().add(0.5, 1, 0.5), "VILLAGER_HAPPY", 15, 0.4, 0.4, 0.4, 0.05);
            try { player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f); } catch (Exception ignored) {}
        } else {
            for (String s : plugin.messages().messages("sell-nothing")) player.sendMessage(s);
        }
    }

    private void handleLightning(Player player, Block block) {
        long cooldown = plugin.config().get().getLong("abilities.lightning-wand.cooldown-ms", 1000L);
        if (!checkCooldown(player, "lightning", cooldown)) return;
        block.getWorld().strikeLightning(block.getLocation());
        plugin.particles().spawn(block.getLocation().add(0.5, 1, 0.5), "ELECTRIC_SPARK", 30, 0.5, 1, 0.5, 0.1);
    }

    private void handleIceWand(Player player, Block block) {
        if (block.getType() == Material.ICE || block.getType() == Material.PACKED_ICE ||
                block.getType() == Material.BLUE_ICE || block.getType() == Material.FROSTED_ICE) {
            block.setType(Material.AIR, true);
            plugin.particles().spawn(block.getLocation().add(0.5, 0.5, 0.5), "SPLASH", 20, 0.3, 0.3, 0.3, 0.1);
            try { player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f); } catch (Exception ignored) {}
        }
    }

    private void handleSandWand(Player player, Block block) {
        for (int i = 1; i < 6; i++) {
            Block above = block.getRelative(org.bukkit.block.BlockFace.UP, i);
            if (above.getType().isAir()) {
                above.setType(Material.SAND, true);
            }
        }
        plugin.particles().spawn(block.getLocation().add(0.5, 1, 0.5), "CLOUD", 20, 0.5, 1, 0.5, 0.05);
    }

    private void handleCraftWand(Player player) {
        player.openWorkbench(null, true);
    }

    private void handleBucket(Player player, Block block) {
        if (block.getType() == Material.WATER || block.getType() == Material.LAVA) {
            block.setType(Material.AIR, true);
            plugin.particles().spawn(block.getLocation().add(0.5, 0.5, 0.5), "BLOCK_CRUMBLE", 20, 0.4, 0.4, 0.4, 0.05);
        }
    }

    private void handleFirework(Player player, PlayerInteractEvent event) {
        if (player.isGliding()) {
            long cooldown = plugin.config().get().getLong("abilities.infinite-firework.cooldown-ms", 500L);
            if (!checkCooldown(player, "firework", cooldown)) {
                event.setCancelled(true);
                return;
            }
            player.setVelocity(player.getLocation().getDirection().multiply(2.0));
            plugin.particles().spawn(player.getLocation(), "FIREWORKS_SPARK", 20, 0.3, 0.3, 0.3, 0.1);
            event.setCancelled(true);
        }
    }

    private final java.util.Map<java.util.UUID, java.util.Map<String, Long>> cooldowns = new java.util.concurrent.ConcurrentHashMap<>();

    private boolean checkCooldown(Player player, String key, long ms) {
        long now = System.currentTimeMillis();
        var m = cooldowns.computeIfAbsent(player.getUniqueId(), k -> new java.util.concurrent.ConcurrentHashMap<>());
        Long last = m.get(key);
        if (last != null && now - last < ms) return false;
        m.put(key, now);
        return true;
    }
}
