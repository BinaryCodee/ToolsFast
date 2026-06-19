package it.bypasser.toolsfast.placeholders;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.utils.Keys;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ToolsFastExpansion extends PlaceholderExpansion {

    private final ToolsFast plugin;

    public ToolsFastExpansion(ToolsFast plugin) {
        this.plugin = plugin;
    }

    @Override public @NotNull String getIdentifier() { return "toolsfast"; }
    @Override public @NotNull String getAuthor() { return "bypasser"; }
    @Override public @NotNull String getVersion() { return "1.0.0"; }
    @Override public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null || !offlinePlayer.isOnline()) return "";
        Player player = offlinePlayer.getPlayer();
        if (player == null) return "";

        if (params.startsWith("top_")) {
            String[] parts = params.split("_");
            if (parts.length >= 3) {
                String type = parts[1];
                int position = parseSafe(parts[2], 1);
                return resolveTop(type, position);
            }
        }

        ItemStack item = player.getInventory().getItemInMainHand();

        return switch (params) {
            case "blocks" -> String.valueOf(plugin.statistics().getBlocks(player.getUniqueId()));
            case "money" -> String.valueOf(plugin.statistics().getMoney(player.getUniqueId()));
            case "trees" -> String.valueOf(plugin.statistics().getTrees(player.getUniqueId()));
            case "crops" -> String.valueOf(plugin.statistics().getCrops(player.getUniqueId()));
            case "items_sold" -> String.valueOf(plugin.statistics().getItemsSold(player.getUniqueId()));
            case "tool" -> {
                String id = plugin.toolRegistry().toolIdOf(item);
                yield id == null ? "Nessuno" : id;
            }
            case "tool_level" -> {
                String id = plugin.toolRegistry().toolIdOf(item);
                var def = id == null ? null : plugin.toolRegistry().get(id);
                yield def == null ? "0" : "1";
            }
            case "tool_uses" -> {
                if (item == null) yield "0";
                ItemMeta meta = item.getItemMeta();
                if (meta == null) yield "0";
                yield String.valueOf(Keys.getLong(meta, Keys.TOOL_USES));
            }
            case "remaining" -> {
                long r = plugin.selfDestruct().getRemaining(item);
                yield r < 0 ? "" : it.bypasser.toolsfast.utils.TimeParser.formatRemaining(r);
            }
            case "expired" -> {
                yield plugin.selfDestruct().hasSelfDestruct(item) ? "false" : "true";
            }
            default -> {
                String custom = plugin.placeholderRegistry().resolve(player, params);
                yield custom == null || custom.isEmpty() ? "" : custom;
            }
        };
    }

    private String resolveTop(String type, int position) {
        Map<java.util.UUID, Long> top;
        top = switch (type) {
            case "blocks" -> plugin.statistics().topBlocks(position);
            case "money" -> plugin.statistics().topMoney(position);
            case "trees" -> plugin.statistics().topTrees(position);
            default -> java.util.Collections.emptyMap();
        };
        if (top.isEmpty()) return "N/D";
        int i = 1;
        for (var e : top.entrySet()) {
            if (i == position) {
                String name = org.bukkit.Bukkit.getOfflinePlayer(e.getKey()).getName();
                if (name == null) name = e.getKey().toString().substring(0, 8);
                return name + " - " + e.getValue();
            }
            i++;
        }
        return "N/D";
    }

    private int parseSafe(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
