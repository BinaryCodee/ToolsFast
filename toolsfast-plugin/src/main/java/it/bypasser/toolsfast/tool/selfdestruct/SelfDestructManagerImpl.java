package it.bypasser.toolsfast.tool.selfdestruct;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.utils.Keys;
import it.bypasser.toolsfast.utils.TimeParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class SelfDestructManagerImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.SelfDestructManager {

    private final ToolsFast plugin;

    public SelfDestructManagerImpl(ToolsFast plugin) {
        this.plugin = plugin;
    }

    @Override
    public void apply(ItemStack item, long durationMillis) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        long end = System.currentTimeMillis() + durationMillis;
        Keys.setLong(meta, Keys.SELF_DESTRUCT_END, end);
        Keys.setLong(meta, Keys.SELF_DESTRUCT_DURATION, durationMillis);
        Keys.remove(meta, Keys.SELF_DESTRUCT_DELAYED);
        Keys.remove(meta, Keys.SELF_DESTRUCT_STARTED);
        item.setItemMeta(meta);
        updateLore(item);
    }

    @Override
    public void applyDelayed(ItemStack item, long durationMillis) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        Keys.setLong(meta, Keys.SELF_DESTRUCT_DURATION, durationMillis);
        Keys.setString(meta, Keys.SELF_DESTRUCT_DELAYED, "1");
        Keys.remove(meta, Keys.SELF_DESTRUCT_END);
        Keys.remove(meta, Keys.SELF_DESTRUCT_STARTED);
        item.setItemMeta(meta);
        updateLore(item);
    }

    @Override
    public long getRemaining(ItemStack item) {
        if (item == null) return -1;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return -1;
        if (Keys.has(meta, Keys.SELF_DESTRUCT_DELAYED)) return Keys.getLong(meta, Keys.SELF_DESTRUCT_DURATION);
        long end = Keys.getLong(meta, Keys.SELF_DESTRUCT_END);
        if (end <= 0) return -1;
        return Math.max(0, end - System.currentTimeMillis());
    }

    @Override
    public boolean isDelayed(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return Keys.has(meta, Keys.SELF_DESTRUCT_DELAYED) && !Keys.has(meta, Keys.SELF_DESTRUCT_STARTED);
    }

    @Override
    public boolean hasSelfDestruct(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return Keys.has(meta, Keys.SELF_DESTRUCT_END) ||
                (Keys.has(meta, Keys.SELF_DESTRUCT_DELAYED) && !Keys.has(meta, Keys.SELF_DESTRUCT_STARTED));
    }

    public boolean activateIfDelayed(ItemStack item) {
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        if (!Keys.has(meta, Keys.SELF_DESTRUCT_DELAYED) || Keys.has(meta, Keys.SELF_DESTRUCT_STARTED)) return false;
        long duration = Keys.getLong(meta, Keys.SELF_DESTRUCT_DURATION);
        long end = System.currentTimeMillis() + duration;
        Keys.setLong(meta, Keys.SELF_DESTRUCT_END, end);
        Keys.setLong(meta, Keys.SELF_DESTRUCT_STARTED, System.currentTimeMillis());
        item.setItemMeta(meta);
        updateLore(item);
        return true;
    }

    public void updateLore(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        List<String> lore = meta.hasLore() ? new java.util.ArrayList<>(meta.getLore()) : new java.util.ArrayList<>();
        for (int i = lore.size() - 1; i >= 0; i--) {
            String stripped = it.bypasser.toolsfast.utils.Colors.strip(lore.get(i));
            if (stripped.contains("SELF DESTRUCT") || stripped.contains("Tempo Rimanente") || stripped.contains("Scadenza") || stripped.contains("distrutto") || stripped.contains("rimasta") || stripped.contains("In attesa")) {
                lore.remove(i);
            }
        }
        while (lore.size() > 0 && lore.get(lore.size() - 1) != null && lore.get(lore.size() - 1).isEmpty()) {
            lore.remove(lore.size() - 1);
        }
        if (lore.size() > 0) lore.add("");
        if (isDelayed(item)) {
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&c&lSELF DESTRUCT"));
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&7In attesa di utilizzo."));
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&7Si attivera al primo uso."));
            lore.add("");
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&fDurata: &c" + TimeParser.formatRemaining(Keys.getLong(meta, Keys.SELF_DESTRUCT_DURATION))));
        } else if (hasSelfDestruct(item)) {
            long remaining = getRemaining(item);
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&c&lSELF DESTRUCT"));
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&7Questo oggetto verra"));
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&7distrutto automaticamente."));
            lore.add("");
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&fTempo Rimanente:"));
            lore.add(it.bypasser.toolsfast.utils.Colors.color("&c" + TimeParser.formatRemaining(remaining)));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void removeSelfDestruct(ItemStack item) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        Keys.remove(meta, Keys.SELF_DESTRUCT_END);
        Keys.remove(meta, Keys.SELF_DESTRUCT_DURATION);
        Keys.remove(meta, Keys.SELF_DESTRUCT_DELAYED);
        Keys.remove(meta, Keys.SELF_DESTRUCT_STARTED);
        item.setItemMeta(meta);
        updateLore(item);
    }
}
