package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.api.ShopProvider;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ShopProviderRegistryImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.ShopProviderRegistry {

    private final Map<String, ShopProvider> providers = new LinkedHashMap<>();
    private ShopProvider active;

    @Override
    public synchronized void register(ShopProvider provider) {
        if (provider == null) return;
        providers.put(provider.id().toLowerCase(), provider);
        provider.onRegister();
        if (active == null) active = provider;
    }

    @Override
    public synchronized ShopProvider get(String id) {
        if (id == null) return null;
        return providers.get(id.toLowerCase());
    }

    @Override
    public synchronized List<ShopProvider> all() {
        return new ArrayList<>(providers.values());
    }

    public synchronized void setActive(String id) {
        ShopProvider p = providers.get(id.toLowerCase());
        if (p != null) active = p;
    }

    public synchronized ShopProvider active() { return active; }

    @Override
    public synchronized double sellAll(Player player, ItemStack[] items) {
        if (active == null || player == null) return 0.0;
        double total = 0.0;
        int sold = 0;
        for (int i = 0; i < items.length; i++) {
            ItemStack it = items[i];
            if (it == null || it.getType().isAir()) continue;
            double price = active.priceOf(player, it);
            if (price <= 0) continue;
            total += price * it.getAmount();
            sold += it.getAmount();
            items[i] = null;
        }
        if (total > 0) {
            it.bypasser.toolsfast.ToolsFast.get().economy().deposit(player, total);
            it.bypasser.toolsfast.ToolsFast.get().statistics().addMoney(player.getUniqueId(), (long) total);
            it.bypasser.toolsfast.ToolsFast.get().statistics().addItemsSold(player.getUniqueId(), sold);
            org.bukkit.Bukkit.getPluginManager().callEvent(new it.bypasser.toolsfast.api.events.SellEvent(player, sold, total));
        }
        return total;
    }
}
