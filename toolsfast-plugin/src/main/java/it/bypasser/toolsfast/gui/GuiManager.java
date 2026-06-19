package it.bypasser.toolsfast.gui;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.utils.Colors;
import it.bypasser.toolsfast.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class GuiManager implements Listener {

    private final ToolsFast plugin;
    private final Map<UUID, GuiSession> sessions = new HashMap<>();

    public GuiManager(ToolsFast plugin) {
        this.plugin = plugin;
    }

    public void openMain(Player player) {
        ConfigurationSection cfg = plugin.guiConfig().get().getConfigurationSection("main");
        if (cfg == null) {
            player.sendMessage(Colors.color("&cGUI config missing."));
            return;
        }
        String title = Colors.color(cfg.getString("title", "&#C77DFFToolsFast"));
        int size = cfg.getInt("size", 27);
        Inventory inv = Bukkit.createInventory(new GuiHolder("main"), size, title);
        fillBackground(inv, cfg);

        applyItem(inv, cfg, "tools", 11, Material.NETHERITE_PICKAXE, "&#C77DFF&lTOOLS",
                List.of("&8Gestione", "", "&fVisualizza tutti gli", "&ftools disponibili.", "", "&d\u25AA &fClick per aprire"));
        applyItem(inv, cfg, "custom-items", 13, Material.ENCHANTED_BOOK, "&#C77DFF&lCUSTOM ITEMS",
                List.of("&8Gestione", "", "&fVisualizza tutti gli", "&fitem personalizzati.", "", "&d\u25AA &fClick per aprire"));
        applyItem(inv, cfg, "stats", 15, Material.PLAYER_HEAD, "&#C77DFF&lSTATISTICHE",
                List.of("&8Informazioni", "",
                        "&fBlocchi Minati:", "&d" + plugin.statistics().getBlocks(player.getUniqueId()),
                        "",
                        "&fAlberi Abbattuti:", "&d" + plugin.statistics().getTrees(player.getUniqueId()),
                        "",
                        "&fSoldi Generati:", "&a$" + plugin.statistics().getMoney(player.getUniqueId())));
        applyItem(inv, cfg, "leaderboard", 22, Material.GOLDEN_AXE, "&#C77DFF&lCLASSIFICHE",
                List.of("&8Top players", "", "&d\u25AA &fClick per aprire"));
        applyItem(inv, cfg, "reload", 4, Material.PAPER, "&#C77DFF&lRELOAD",
                List.of("&8Admin", "", "&fRicarica configurazione", "", "&d\u25AA &fClick per ricaricare"));
        applyItem(inv, cfg, "settings", 26, Material.REPEATER, "&#C77DFF&lIMPOSTAZIONI",
                List.of("&8Presto disponibile"));

        player.openInventory(inv);
        sessions.put(player.getUniqueId(), new GuiSession("main", 0));
    }

    public void openCustomItems(Player player, int page) {
        ConfigurationSection cfg = plugin.guiConfig().get().getConfigurationSection("custom-items");
        if (cfg == null) {
            player.sendMessage(Colors.color("&cGUI config missing."));
            return;
        }
        String title = Colors.color(cfg.getString("title", "&#C77DFFCustom Items"));
        int size = 54;
        Inventory inv = Bukkit.createInventory(new GuiHolder("custom_items:" + page), size, title);
        var items = new ArrayList<>(plugin.customItems().all());
        int perPage = 45;
        int start = page * perPage;
        int end = Math.min(items.size(), start + perPage);
        for (int i = start; i < end; i++) {
            var it = items.get(i);
            ItemStack built = it.build(player, 1);
            inv.setItem(i - start, built);
        }
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
        }
        if (page > 0) {
            inv.setItem(45, new ItemBuilder(Material.ARROW).name(Colors.color("&d\u25C0 &fPrecedente")).build());
        }
        if (end < items.size()) {
            inv.setItem(53, new ItemBuilder(Material.ARROW).name(Colors.color("&d\u25B6 &fSuccessivo")).build());
        }
        inv.setItem(49, new ItemBuilder(Material.BARRIER).name(Colors.color("&c\u2715 &fChiudi")).build());
        player.openInventory(inv);
        sessions.put(player.getUniqueId(), new GuiSession("custom_items", page));
    }

    public void openTools(Player player) {
        ConfigurationSection cfg = plugin.guiConfig().get().getConfigurationSection("tools");
        if (cfg == null) {
            player.sendMessage(Colors.color("&cGUI config missing."));
            return;
        }
        String title = Colors.color(cfg.getString("title", "&#C77DFFTools"));
        int size = 54;
        Inventory inv = Bukkit.createInventory(new GuiHolder("tools"), size, title);
        int slot = 0;
        for (var def : plugin.toolRegistry().all()) {
            if (slot >= 45) break;
            ItemStack built = def.build(player);
            inv.setItem(slot++, built);
        }
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
        }
        inv.setItem(49, new ItemBuilder(Material.BARRIER).name(Colors.color("&c\u2715 &fChiudi")).build());
        player.openInventory(inv);
        sessions.put(player.getUniqueId(), new GuiSession("tools", 0));
    }

    public void openLeaderboard(Player player) {
        ConfigurationSection cfg = plugin.guiConfig().get().getConfigurationSection("leaderboard");
        if (cfg == null) {
            player.sendMessage(Colors.color("&cGUI config missing."));
            return;
        }
        String title = Colors.color(cfg.getString("title", "&#C77DFFClassifiche"));
        int size = 27;
        Inventory inv = Bukkit.createInventory(new GuiHolder("leaderboard"), size, title);
        for (int i = 0; i < size; i++) {
            inv.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
        }
        inv.setItem(11, buildLeaderboardPanel(Material.DIAMOND_PICKAXE, "&#C77DFF&lTOP BLOCKS", "blocks"));
        inv.setItem(13, buildLeaderboardPanel(Material.GOLD_INGOT, "&#C77DFF&lTOP MONEY", "money"));
        inv.setItem(15, buildLeaderboardPanel(Material.OAK_LOG, "&#C77DFF&lTOP TREES", "trees"));
        player.openInventory(inv);
        sessions.put(player.getUniqueId(), new GuiSession("leaderboard", 0));
    }

    private ItemStack buildLeaderboardPanel(Material material, String title, String column) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        java.util.Map<java.util.UUID, Long> top;
        if (column.equals("blocks")) top = plugin.statistics().topBlocks(5);
        else if (column.equals("money")) top = plugin.statistics().topMoney(5);
        else top = plugin.statistics().topTrees(5);
        int i = 1;
        for (var e : top.entrySet()) {
            String name = Bukkit.getOfflinePlayer(e.getKey()).getName();
            if (name == null) name = e.getKey().toString().substring(0, 8);
            lore.add(Colors.color("&d#" + i + " &f" + name + " &7- &d" + e.getValue()));
            i++;
        }
        return new ItemBuilder(material).name(Colors.color(title)).lore(lore).build();
    }

    private void applyItem(Inventory inv, ConfigurationSection cfg, String key, int defaultSlot,
                           Material material, String defaultName, List<String> defaultLore) {
        ConfigurationSection sec = cfg.getConfigurationSection("items." + key);
        int slot = sec != null ? sec.getInt("slot", defaultSlot) : defaultSlot;
        Material mat = material;
        if (sec != null) {
            String m = sec.getString("material");
            if (m != null) { try { mat = Material.valueOf(m.toUpperCase()); } catch (Exception ignored) {} }
        }
        String name = sec != null ? sec.getString("name", defaultName) : defaultName;
        List<String> lore = sec != null && sec.contains("lore") ? sec.getStringList("lore") : defaultLore;
        ItemStack item = new ItemBuilder(mat).name(name).lore(lore).build();
        if (slot >= 0 && slot < inv.getSize()) inv.setItem(slot, item);
    }

    private void fillBackground(Inventory inv, ConfigurationSection cfg) {
        String bgMat = cfg.getString("background", "GRAY_STAINED_GLASS_PANE");
        Material m;
        try { m = Material.valueOf(bgMat.toUpperCase()); } catch (Exception e) { m = Material.GRAY_STAINED_GLASS_PANE; }
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, new ItemBuilder(m).name(" ").build());
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof GuiHolder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;
        int slot = event.getRawSlot();
        String type = holder.type;

        switch (type) {
            case "main" -> {
                if (slot == 11) openTools(player);
                else if (slot == 13) openCustomItems(player, 0);
                else if (slot == 15) openMain(player);
                else if (slot == 22) openLeaderboard(player);
                else if (slot == 4 && player.hasPermission("toolsfast.admin")) {
                    plugin.reloadAll();
                    player.sendMessage(plugin.messages().prefix() + Colors.color("&aRicaricato."));
                }
            }
            case "tools", "custom_items:0", "custom_items:1" -> {
                if (slot >= 45) {
                    if (slot == 49) player.closeInventory();
                    else if (slot == 45 && type.contains("custom_items")) openCustomItems(player, Math.max(0, getPage(player) - 1));
                    else if (slot == 53 && type.contains("custom_items")) openCustomItems(player, getPage(player) + 1);
                    return;
                }
                String toolId = plugin.toolRegistry().toolIdOf(clicked);
                if (toolId == null) {
                    var meta = clicked.getItemMeta();
                    if (meta != null) toolId = it.bypasser.toolsfast.utils.Keys.getString(meta, it.bypasser.toolsfast.utils.Keys.CUSTOM_ITEM_ID);
                }
                if (toolId != null) {
                    var def = plugin.toolRegistry().get(toolId);
                    if (def != null) plugin.api().giveTool(player, toolId, 1);
                    else {
                        var custom = plugin.customItems().get(toolId);
                        if (custom != null) {
                            ItemStack built = custom.build(player, 1);
                            player.getInventory().addItem(built).values().forEach(left ->
                                    player.getWorld().dropItemNaturally(player.getLocation(), left));
                        }
                    }
                }
            }
            case "leaderboard" -> {
                if (slot == 11 || slot == 13 || slot == 15) {
                    player.sendMessage(plugin.messages().prefix() + Colors.color("&7Apri dal pannello dettagliato."));
                }
            }
        }
    }

    private int getPage(Player p) {
        GuiSession s = sessions.get(p.getUniqueId());
        return s == null ? 0 : s.page;
    }

    public record GuiSession(String type, int page) {}

    public static final class GuiHolder implements InventoryHolder {
        private final String type;
        public GuiHolder(String type) { this.type = type; }
        public String type() { return type; }
        @Override public @NotNull Inventory getInventory() { return null; }
    }
}
