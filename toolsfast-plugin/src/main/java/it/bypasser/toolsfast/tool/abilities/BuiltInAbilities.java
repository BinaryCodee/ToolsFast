package it.bypasser.toolsfast.tool.abilities;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.api.Ability;
import it.bypasser.toolsfast.api.ToolsFastAPI;
import it.bypasser.toolsfast.utils.Keys;

import java.util.HashMap;
import java.util.Map;

public final class BuiltInAbilities {

    private BuiltInAbilities() {}

    public static void registerAll(ToolsFastAPI.AbilityRegistry registry, ToolsFast plugin) {
        registry.register(new Break3x3(plugin));
        registry.register(new Break5x5(plugin));
        registry.register(new Break7x7(plugin));
        registry.register(new AutoPickup(plugin));
        registry.register(new AutoSmelt(plugin));
        registry.register(new TreeCapitator(plugin));
        registry.register(new AutoReplant(plugin));
        registry.register(new TrenchBreak(plugin));
        registry.register(new TrayBreak(plugin));
    }

    public static Ability create(String id, ToolsFast plugin) {
        return switch (id.toUpperCase()) {
            case "BREAK_3X3" -> new Break3x3(plugin);
            case "BREAK_5X5" -> new Break5x5(plugin);
            case "BREAK_7X7" -> new Break7x7(plugin);
            case "AUTO_PICKUP" -> new AutoPickup(plugin);
            case "AUTO_SMELT" -> new AutoSmelt(plugin);
            case "TREE_CAPITATOR", "BREAK_TREE" -> new TreeCapitator(plugin);
            case "AUTO_REPLANT" -> new AutoReplant(plugin);
            case "TRENCH_BREAK" -> new TrenchBreak(plugin);
            case "TRAY_BREAK" -> new TrayBreak(plugin);
            default -> null;
        };
    }
}
