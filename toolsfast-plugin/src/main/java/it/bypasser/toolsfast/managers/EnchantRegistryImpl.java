package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.api.TokenEnchant;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EnchantRegistryImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.EnchantRegistry {

    private final Map<String, TokenEnchant> map = new LinkedHashMap<>();

    @Override
    public synchronized void register(TokenEnchant enchant) {
        if (enchant == null) return;
        map.put(enchant.id().toUpperCase(), enchant);
    }

    @Override
    public synchronized TokenEnchant get(String id) {
        if (id == null) return null;
        return map.get(id.toUpperCase());
    }

    @Override
    public synchronized java.util.List<TokenEnchant> all() {
        return new java.util.ArrayList<>(map.values());
    }
}
