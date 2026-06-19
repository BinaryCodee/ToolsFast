package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import it.bypasser.toolsfast.api.CustomItem;
import it.bypasser.toolsfast.api.ParticleEffect;
import it.bypasser.toolsfast.api.PlaceholderProvider;
import it.bypasser.toolsfast.api.ShopProvider;
import it.bypasser.toolsfast.api.TokenEnchant;
import it.bypasser.toolsfast.api.ToolsFastAPI;
import it.bypasser.toolsfast.api.events.ToolReceiveEvent;
import it.bypasser.toolsfast.particles.ParticleEngineImpl;
import it.bypasser.toolsfast.tool.selfdestruct.SelfDestructManagerImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class APIImpl implements ToolsFastAPI {

    private final CustomItemManagerImpl customItems;
    private final ToolRegistryImpl tools;
    private final SelfDestructManagerImpl selfDestruct;
    private final StatisticsManagerImpl statistics;
    private final ShopProviderRegistryImpl shopProviders;
    private final ParticleEngineImpl particles;
    private final AbilityRegistryImpl abilities;
    private final EnchantRegistryImpl enchants;
    private final PlaceholderRegistryImpl placeholders;
    private final ToolsFast plugin;

    public APIImpl(CustomItemManagerImpl customItems, ToolRegistryImpl tools,
                   SelfDestructManagerImpl selfDestruct, StatisticsManagerImpl statistics,
                   ShopProviderRegistryImpl shopProviders, ParticleEngineImpl particles,
                   AbilityRegistryImpl abilities, EnchantRegistryImpl enchants,
                   PlaceholderRegistryImpl placeholders, ToolsFast plugin) {
        this.customItems = customItems;
        this.tools = tools;
        this.selfDestruct = selfDestruct;
        this.statistics = statistics;
        this.shopProviders = shopProviders;
        this.particles = particles;
        this.abilities = abilities;
        this.enchants = enchants;
        this.placeholders = placeholders;
        this.plugin = plugin;
    }

    @Override public CustomItemManager customItems() { return customItems; }
    @Override public ToolRegistry tools() { return tools; }
    @Override public SelfDestructManager selfDestruct() { return selfDestruct; }
    @Override public StatisticsManager statistics() { return statistics; }
    @Override public ShopProviderRegistry shopProviders() { return shopProviders; }
    @Override public ParticleEngine particles() { return particles; }
    @Override public AbilityRegistry abilities() { return abilities; }
    @Override public EnchantRegistry enchants() { return enchants; }
    @Override public PlaceholderRegistry placeholders() { return placeholders; }

    @Override
    public void registerCustomItem(CustomItem item) {
        customItems.register(item);
    }

    @Override
    public void giveTool(Player player, String toolId, int amount) {
        var def = tools.get(toolId);
        if (def == null) {
            plugin.getLogger().warning("Unknown tool id: " + toolId);
            return;
        }
        ItemStack item = def.build(player);
        item.setAmount(Math.max(1, amount));
        ToolReceiveEvent event = new ToolReceiveEvent(player, toolId, item);
        org.bukkit.Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        player.getInventory().addItem(event.item()).values().forEach(left ->
                player.getWorld().dropItemNaturally(player.getLocation(), left));
        if (def.onReceive() != null) def.onReceive().accept(player);
    }

    @Override
    public boolean isTool(ItemStack item) {
        return tools.matches(item);
    }

    @Override
    public String getToolId(ItemStack item) {
        return tools.toolIdOf(item);
    }
}
