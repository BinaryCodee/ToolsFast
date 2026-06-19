package it.bypasser.toolsfast.tasks;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.events.SelfDestructExpireEvent;
import it.bypasser.toolsfast.utils.Keys;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public final class SelfDestructTask {

    private final ToolsFast plugin;
    private BukkitTask task;
    private BukkitTask loreTask;

    public SelfDestructTask(ToolsFast plugin) {
        this.plugin = plugin;
    }

    public void start() {
        long interval = plugin.config().get().getLong("self-destruct.tick-interval-ticks", 20L);
        long loreInterval = plugin.config().get().getLong("self-destruct.lore-update-ticks", 10L);
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 20L, Math.max(5L, interval));
        loreTask = Bukkit.getScheduler().runTaskTimer(plugin, this::tickLore, 20L, Math.max(5L, loreInterval));
    }

    public void stop() {
        if (task != null) task.cancel();
        if (loreTask != null) loreTask.cancel();
    }

    private void tick() {
        long now = System.currentTimeMillis();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack[] contents = p.getInventory().getContents();
            boolean changed = false;
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item == null || item.getType().isAir()) continue;
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;
                if (!Keys.has(meta, Keys.SELF_DESTRUCT_END)) continue;
                long end = Keys.getLong(meta, Keys.SELF_DESTRUCT_END);
                if (end > now) continue;
                String toolId = plugin.toolRegistry().toolIdOf(item);
                SelfDestructExpireEvent event = new SelfDestructExpireEvent(p, item, toolId);
                Bukkit.getPluginManager().callEvent(event);
                contents[i] = null;
                changed = true;
                playExpireEffects(p, item);
                sendExpireMessage(p, item);
            }
            if (changed) {
                p.getInventory().setContents(contents);
                p.updateInventory();
            }
        }
    }

    private void tickLore() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ItemStack[] contents = p.getInventory().getContents();
            boolean changed = false;
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item == null || item.getType().isAir()) continue;
                ItemMeta meta = item.getItemMeta();
                if (meta == null) continue;
                if (!Keys.has(meta, Keys.SELF_DESTRUCT_END) && !Keys.has(meta, Keys.SELF_DESTRUCT_DELAYED)) continue;
                ItemStack copy = item.clone();
                plugin.selfDestruct().updateLore(copy);
                if (!java.util.Objects.equals(copy.getItemMeta().getLore(), meta.getLore())) {
                    contents[i] = copy;
                    changed = true;
                }
            }
            if (changed) {
                p.getInventory().setContents(contents);
                p.updateInventory();
            }
        }
    }

    private void playExpireEffects(Player p, ItemStack item) {
        String sound = plugin.config().get().getString("self-destruct.expire.sound", "BLOCK_GLASS_BREAK");
        String particle = plugin.config().get().getString("self-destruct.expire.particle", "END_ROD");
        int count = plugin.config().get().getInt("self-destruct.expire.particle-count", 30);
        try {
            org.bukkit.Sound s = org.bukkit.Sound.valueOf(sound.toUpperCase());
            p.playSound(p.getLocation(), s, 1.0f, 1.0f);
        } catch (Exception ignored) {}
        plugin.particles().spawn(p.getLocation().add(0, 1, 0), particle, count, 0.4, 0.6, 0.4, 0.05);
    }

    private void sendExpireMessage(Player p, ItemStack item) {
        String name = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name();
        for (String s : plugin.messages().messages("item-expired")) {
            p.sendMessage(s.replace("%item%", name));
        }
        String title = it.bypasser.toolsfast.utils.Colors.color(plugin.messages().message("self-destruct-expire-title.title"));
        String subtitle = it.bypasser.toolsfast.utils.Colors.color(plugin.messages().message("self-destruct-expire-title.subtitle"));
        if (!title.isEmpty() || !subtitle.isEmpty()) {
            if (plugin.nms() != null) {
                plugin.nms().sendTitle(p, title, subtitle, 10, 40, 10);
            } else {
                p.sendTitle(title, subtitle, 10, 40, 10);
            }
        }
    }
}
