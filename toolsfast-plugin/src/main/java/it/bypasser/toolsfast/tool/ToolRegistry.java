package it.bypasser.toolsfast.tool;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.ToolDefinition;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ToolRegistry {

    private final ToolsFast plugin;
    private final Map<String, ToolDefinition> tools = new LinkedHashMap<>();

    public ToolRegistry(ToolsFast plugin) {
        this.plugin = plugin;
    }

    public synchronized void register(ToolDefinition def) {
        if (def == null) return;
        tools.put(def.id().toLowerCase(), def);
    }

    public synchronized ToolDefinition get(String id) {
        if (id == null) return null;
        return tools.get(id.toLowerCase());
    }

    public synchronized java.util.Collection<ToolDefinition> all() {
        return tools.values();
    }
}
