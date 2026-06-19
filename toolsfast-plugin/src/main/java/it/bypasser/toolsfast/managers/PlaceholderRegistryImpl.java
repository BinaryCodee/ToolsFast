package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.api.PlaceholderProvider;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class PlaceholderRegistryImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.PlaceholderRegistry {

    private final Map<String, PlaceholderProvider> map = new LinkedHashMap<>();

    @Override
    public synchronized void register(String id, PlaceholderProvider provider) {
        if (id == null || provider == null) return;
        map.put(id.toLowerCase(), provider);
    }

    @Override
    public synchronized String resolve(Player player, String id) {
        if (id == null) return "";
        PlaceholderProvider p = map.get(id.toLowerCase());
        return p == null ? "" : p.resolve(player);
    }

    public synchronized Collection<Map.Entry<String, PlaceholderProvider>> entries() {
        return map.entrySet();
    }
}
