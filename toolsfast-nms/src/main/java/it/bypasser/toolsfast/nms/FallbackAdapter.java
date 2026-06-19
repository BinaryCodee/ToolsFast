package it.bypasser.toolsfast.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FallbackAdapter implements NmsAdapter {

    private final Map<UUID, Object> bossBars = new HashMap<>();

    @Override public String version() { return "fallback"; }

    @Override
    public void sendActionBar(Player player, String message) {
        try {
            player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                    net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message));
        } catch (Throwable t) {
            player.sendMessage(message);
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } catch (Throwable t) {
            player.sendMessage(title);
        }
    }

    @Override
    public void sendBossBar(Player player, String title, double progress) {
        try {
            org.bukkit.boss.BossBar bar = (org.bukkit.boss.BossBar) bossBars.get(player.getUniqueId());
            if (bar == null) {
                bar = Bukkit.createBossBar(title, org.bukkit.boss.BarColor.PURPLE, org.bukkit.boss.BarStyle.SOLID);
                bar.addPlayer(player);
                bossBars.put(player.getUniqueId(), bar);
            } else {
                bar.setTitle(title);
            }
            bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
            bar.setVisible(true);
        } catch (Throwable ignored) {}
    }

    @Override
    public void removeBossBar(Player player) {
        Object obj = bossBars.remove(player.getUniqueId());
        if (obj instanceof org.bukkit.boss.BossBar bar) {
            bar.removeAll();
        }
    }

    @Override
    public void spawnParticle(Player player, Location location, String particle, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        try {
            org.bukkit.Particle p = org.bukkit.Particle.valueOf(particle.toUpperCase());
            player.spawnParticle(p, location, count, offsetX, offsetY, offsetZ, speed);
        } catch (Throwable ignored) {}
    }

    @Override
    public int getItemDurability(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable d) return d.getDamage();
        return 0;
    }

    @Override
    public void setItemDurability(ItemStack item, int durability) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable d) {
            d.setDamage(durability);
            item.setItemMeta(meta);
        }
    }

    @Override
    public ItemStack setCustomModelData(ItemStack item, int modelData) {
        if (item == null) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(modelData);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public ItemStack setUnbreakable(ItemStack item, boolean unbreakable) {
        if (item == null) return item;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }
        return item;
    }
}
