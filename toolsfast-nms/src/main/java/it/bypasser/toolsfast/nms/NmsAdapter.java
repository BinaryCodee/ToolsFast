package it.bypasser.toolsfast.nms;

public interface NmsAdapter {
    String version();
    void sendActionBar(org.bukkit.entity.Player player, String message);
    void sendTitle(org.bukkit.entity.Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut);
    void sendBossBar(org.bukkit.entity.Player player, String title, double progress);
    void removeBossBar(org.bukkit.entity.Player player);
    void spawnParticle(org.bukkit.entity.Player player, org.bukkit.Location location, String particle, int count, double offsetX, double offsetY, double offsetZ, double speed);
    int getItemDurability(org.bukkit.inventory.ItemStack item);
    void setItemDurability(org.bukkit.inventory.ItemStack item, int durability);
    org.bukkit.inventory.ItemStack setCustomModelData(org.bukkit.inventory.ItemStack item, int modelData);
    org.bukkit.inventory.ItemStack setUnbreakable(org.bukkit.inventory.ItemStack item, boolean unbreakable);
}
