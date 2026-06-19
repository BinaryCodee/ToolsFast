package it.bypasser.toolsfast.managers;

import it.bypasser.toolsfast.api.Ability;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AbilityRegistryImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.AbilityRegistry {

    private final Map<String, Ability> map = new LinkedHashMap<>();

    @Override
    public synchronized void register(Ability ability) {
        if (ability == null) return;
        ability.onLoad();
        map.put(ability.id().toUpperCase(), ability);
    }

    @Override
    public synchronized Ability get(String id) {
        if (id == null) return null;
        return map.get(id.toUpperCase());
    }

    @Override
    public synchronized java.util.List<Ability> all() {
        return new java.util.ArrayList<>(map.values());
    }
}
