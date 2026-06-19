package it.bypasser.toolsfast.shops;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.ShopProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class BuiltInShopProviders {

    private BuiltInShopProviders() {}

    public static void registerAll(it.bypasser.toolsfast.api.ToolsFastAPI.ShopProviderRegistry registry, ToolsFast plugin) {
        registry.register(new FallbackShopProvider(plugin));
        registry.register(new VaultWorthProvider(plugin));
    }

    public static final class FallbackShopProvider implements ShopProvider {

        private final ToolsFast plugin;
        private final Map<Material, Double> prices = new HashMap<>();

        public FallbackShopProvider(ToolsFast plugin) {
            this.plugin = plugin;
            loadPrices();
        }

        private void loadPrices() {
            prices.put(Material.COAL_ORE, 1.0);
            prices.put(Material.COAL, 1.5);
            prices.put(Material.IRON_ORE, 2.0);
            prices.put(Material.IRON_INGOT, 3.0);
            prices.put(Material.GOLD_ORE, 3.0);
            prices.put(Material.GOLD_INGOT, 5.0);
            prices.put(Material.DIAMOND_ORE, 8.0);
            prices.put(Material.DIAMOND, 12.0);
            prices.put(Material.EMERALD_ORE, 6.0);
            prices.put(Material.EMERALD, 10.0);
            prices.put(Material.NETHERITE_INGOT, 80.0);
            prices.put(Material.NETHER_QUARTZ_ORE, 1.5);
            prices.put(Material.QUARTZ, 2.0);
            prices.put(Material.LAPIS_ORE, 3.0);
            prices.put(Material.LAPIS_LAZULI, 4.0);
            prices.put(Material.REDSTONE_ORE, 2.0);
            prices.put(Material.REDSTONE, 2.5);
            prices.put(Material.NETHERRACK, 0.2);
            prices.put(Material.COBBLESTONE, 0.1);
            prices.put(Material.STONE, 0.2);
            prices.put(Material.DIRT, 0.05);
            prices.put(Material.SAND, 0.1);
            prices.put(Material.GRAVEL, 0.1);
            prices.put(Material.OAK_LOG, 1.0);
            prices.put(Material.BIRCH_LOG, 1.0);
            prices.put(Material.SPRUCE_LOG, 1.0);
            prices.put(Material.JUNGLE_LOG, 1.2);
            prices.put(Material.ACACIA_LOG, 1.2);
            prices.put(Material.DARK_OAK_LOG, 1.2);
            prices.put(Material.MANGROVE_LOG, 1.5);
            prices.put(Material.CHERRY_LOG, 1.5);
            prices.put(Material.CRIMSON_STEM, 1.5);
            prices.put(Material.WARPED_STEM, 1.5);
            prices.put(Material.WHEAT, 1.5);
            prices.put(Material.CARROT, 1.2);
            prices.put(Material.POTATO, 1.2);
            prices.put(Material.BEETROOT, 1.5);
            prices.put(Material.NETHER_WART, 2.0);
        }

        @Override public String id() { return "fallback"; }

        @Override
        public double priceOf(Player player, ItemStack item) {
            if (item == null) return 0.0;
            Double d = prices.get(item.getType());
            if (d != null) return d;
            double cfg = plugin.config().get().getDouble("shop.fallback-prices." + item.getType().name(), -1.0);
            return cfg >= 0 ? cfg : 0.0;
        }
    }

    public static final class VaultWorthProvider implements ShopProvider {

        private final ToolsFast plugin;
        public VaultWorthProvider(ToolsFast plugin) { this.plugin = plugin; }

        @Override public String id() { return "vaultworth"; }

        @Override
        public double priceOf(Player player, ItemStack item) {
            if (item == null) return 0.0;
            double price = plugin.config().get().getDouble("shop.worth." + item.getType().name(), -1.0);
            return price >= 0 ? price : 0.0;
        }
    }
}
