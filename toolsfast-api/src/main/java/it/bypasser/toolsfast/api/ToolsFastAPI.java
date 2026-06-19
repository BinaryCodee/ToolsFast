package it.bypasser.toolsfast.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ToolsFastAPI {

    static ToolsFastAPI get() {
        return ToolsFastAPIHolder.INSTANCE;
    }

    CustomItemManager customItems();

    ToolRegistry tools();

    SelfDestructManager selfDestruct();

    StatisticsManager statistics();

    ShopProviderRegistry shopProviders();

    ParticleEngine particles();

    AbilityRegistry abilities();

    EnchantRegistry enchants();

    PlaceholderRegistry placeholders();

    void registerCustomItem(CustomItem item);

    void giveTool(Player player, String toolId, int amount);

    boolean isTool(ItemStack item);

    String getToolId(ItemStack item);

    interface CustomItemManager {
        CustomItem get(String id);
        List<CustomItem> all();
        void reload();
    }

    interface ToolRegistry {
        void register(ToolDefinition tool);
        ToolDefinition get(String id);
        List<ToolDefinition> all();
    }

    interface SelfDestructManager {
        void apply(ItemStack item, long durationMillis);
        void applyDelayed(ItemStack item, long durationMillis);
        long getRemaining(ItemStack item);
        boolean isDelayed(ItemStack item);
        boolean hasSelfDestruct(ItemStack item);
    }

    interface StatisticsManager {
        long getBlocks(UUID player);
        long getMoney(UUID player);
        long getTrees(UUID player);
        long getCrops(UUID player);
        long getItemsSold(UUID player);
        void addBlocks(UUID player, long amount);
        void addMoney(UUID player, long amount);
        void addTrees(UUID player, long amount);
        void addCrops(UUID player, long amount);
        void addItemsSold(UUID player, long amount);
        Map<UUID, Long> topBlocks(int limit);
        Map<UUID, Long> topMoney(int limit);
        Map<UUID, Long> topTrees(int limit);
    }

    interface ShopProviderRegistry {
        void register(ShopProvider provider);
        ShopProvider get(String id);
        List<ShopProvider> all();
        double sellAll(Player player, ItemStack[] items);
    }

    interface ParticleEngine {
        void spawn(org.bukkit.Location location, String particleId, int count, double offsetX, double offsetY, double offsetZ, double speed);
        void registerParticle(String id, ParticleEffect effect);
    }

    interface AbilityRegistry {
        void register(Ability ability);
        Ability get(String id);
        List<Ability> all();
    }

    interface EnchantRegistry {
        void register(TokenEnchant enchant);
        TokenEnchant get(String id);
        List<TokenEnchant> all();
    }

    interface PlaceholderRegistry {
        void register(String id, PlaceholderProvider provider);
        String resolve(Player player, String id);
    }
}
