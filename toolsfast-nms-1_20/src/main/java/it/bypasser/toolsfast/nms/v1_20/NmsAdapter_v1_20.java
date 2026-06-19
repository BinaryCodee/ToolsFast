package it.bypasser.toolsfast.nms.v1_20;

import it.bypasser.toolsfast.nms.NmsAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NmsAdapter_v1_20 implements NmsAdapter {

    private final Map<UUID, BossBar> bossBars = new HashMap<>();

    @Override public String version() { return "v1_20"; }

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
        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendBossBar(Player player, String title, double progress) {
        BossBar bar = bossBars.get(player.getUniqueId());
        if (bar == null) {
            bar = Bukkit.createBossBar(title, BarColor.PURPLE, BarStyle.SOLID);
            bar.addPlayer(player);
            bossBars.put(player.getUniqueId(), bar);
        } else {
            bar.setTitle(title);
        }
        bar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        bar.setVisible(true);
    }

    @Override
    public void removeBossBar(Player player) {
        BossBar bar = bossBars.remove(player.getUniqueId());
        if (bar != null) bar.removeAll();
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
