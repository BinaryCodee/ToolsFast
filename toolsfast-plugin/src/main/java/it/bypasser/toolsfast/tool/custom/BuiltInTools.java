package it.bypasser.toolsfast.tool.custom;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.managers.ToolRegistryImpl;
import org.bukkit.Material;

import java.util.List;

public final class BuiltInTools {

    private BuiltInTools() {}

    public static void registerAll(ToolRegistryImpl registry, ToolsFast plugin) {
        registry.register(new AmethystToolDefinition(plugin, "amethyst_drill",
                Material.NETHERITE_PICKAXE, "&#C77DFF&lAMETHYST DRILL",
                List.of(
                        "&8Mining Tool", "",
                        "&fAbilita:",
                        "&d\u25B8 &fBreak 3x3",
                        "&d\u25B8 &fAuto Pickup",
                        "&d\u25B8 &fFortune Support", "",
                        "&fBlocchi Minati:",
                        "&d%blocks%", "",
                        "&fDurabilita:",
                        "&d%durability%"
                ),
                List.of("BREAK_3X3", "AUTO_PICKUP")));

        registry.register(new AmethystToolDefinition(plugin, "amethyst_hammer",
                Material.NETHERITE_PICKAXE, "&#C77DFF&lAMETHYST HAMMER",
                List.of(
                        "&8Mining Tool", "",
                        "&fAbilita:",
                        "&d\u25B8 &fBreak 5x5",
                        "&d\u25B8 &fAuto Pickup", "",
                        "&fBlocchi Minati:",
                        "&d%blocks%"
                ),
                List.of("BREAK_5X5", "AUTO_PICKUP")));

        registry.register(new AmethystToolDefinition(plugin, "amethyst_axe",
                Material.NETHERITE_AXE, "&#C77DFF&lAMETHYST AXE",
                List.of(
                        "&8Tree Capitator", "",
                        "&fAbilita:",
                        "&d\u25B8 &fTree Capitator",
                        "&d\u25B8 &fRilevamento Foglie", "",
                        "&fAlberi Abbattuti:",
                        "&d%trees%"
                ),
                List.of("TREE_CAPITATOR")));

        registry.register(new AmethystToolDefinition(plugin, "amethyst_shovel",
                Material.NETHERITE_SHOVEL, "&#C77DFF&lAMETHYST SHOVEL",
                List.of(
                        "&8Excavation Tool", "",
                        "&fAbilita:",
                        "&d\u25B8 &fBreak 3x3",
                        "&d\u25B8 &fAuto Pickup"
                ),
                List.of("BREAK_3X3", "AUTO_PICKUP")));

        registry.register(new AmethystToolDefinition(plugin, "amethyst_bucket",
                Material.BUCKET, "&#C77DFF&lAMETHYST BUCKET",
                List.of(
                        "&8Utility", "",
                        "&fCapacita:",
                        "&d27 blocchi", "",
                        "&fAssorbimento:",
                        "&d\u25B8 &fAcqua",
                        "&d\u25B8 &fLava"
                ),
                List.of()));

        registry.register(new AmethystToolDefinition(plugin, "amethyst_sell_axe",
                Material.NETHERITE_AXE, "&#C77DFF&lAMETHYST SELL AXE",
                List.of(
                        "&8Economy Tool", "",
                        "&fAbilita:",
                        "&d\u25B8 &fVende tutto il contenuto",
                        "&d\u25B8 &fClick su chest", "",
                        "&fItems Venduti:",
                        "&d%items_sold%"
                ),
                List.of("SELL")));

        registry.register(new AmethystToolDefinition(plugin, "amethyst_multi_tool",
                Material.NETHERITE_PICKAXE, "&#C77DFF&lAMETHYST MULTI TOOL",
                List.of(
                        "&8Smart Tool", "",
                        "&fSwitch automatico:",
                        "&d\u25B8 &fPickaxe",
                        "&d\u25B8 &fAxe",
                        "&d\u25B8 &fShovel",
                        "&d\u25B8 &fSword"
                ),
                List.of("BREAK_3X3", "TREE_CAPITATOR", "AUTO_PICKUP")));

        registry.register(new AmethystToolDefinition(plugin, "infinite_firework",
                Material.FIREWORK_ROCKET, "&#C77DFF&lINFINITE FIREWORK",
                List.of(
                        "&8Utility", "",
                        "&fAbilita:",
                        "&d\u25B8 &fElytra Boost",
                        "&d\u25B8 &fNessun consumo"
                ),
                List.of()));

        registry.register(new AmethystToolDefinition(plugin, "harvester_hoe",
                Material.NETHERITE_HOE, "&#C77DFF&lHARVESTER HOE",
                List.of(
                        "&8Farming Tool", "",
                        "&fAbilita:",
                        "&d\u25B8 &fRaccolta automatica",
                        "&d\u25B8 &fReplant automatico", "",
                        "&fColture Raccolte:",
                        "&d%crops%"
                ),
                List.of("AUTO_REPLANT", "AUTO_PICKUP")));

        registry.register(new AmethystToolDefinition(plugin, "trench_pickaxe",
                Material.NETHERITE_PICKAXE, "&#C77DFF&lTRENCH PICKAXE",
                List.of(
                        "&8Mining Tool", "",
                        "&fAbilita:",
                        "&d\u25B8 &fBreak 3x3 / 5x5 / 7x7"
                ),
                List.of("TRENCH_BREAK", "AUTO_PICKUP")));

        registry.register(new AmethystToolDefinition(plugin, "tray_pickaxe",
                Material.NETHERITE_PICKAXE, "&#C77DFF&lTRAY PICKAXE",
                List.of(
                        "&8Mining Tool", "",
                        "&fAbilita:",
                        "&d\u25B8 &fRompe layer orizzontali"
                ),
                List.of("TRAY_BREAK", "AUTO_PICKUP")));

        registry.register(new AmethystToolDefinition(plugin, "sand_wand",
                Material.STICK, "&#C77DFF&lSAND WAND",
                List.of(
                        "&8Utility", "",
                        "&fRiempimento automatico",
                        "&fstack di sabbia"
                ),
                List.of()));

        registry.register(new AmethystToolDefinition(plugin, "ice_wand",
                Material.STICK, "&#C77DFF&lICE WAND",
                List.of(
                        "&8Utility", "",
                        "&fRimuove automaticamente",
                        "&fghiaccio"
                ),
                List.of()));

        registry.register(new AmethystToolDefinition(plugin, "craft_wand",
                Material.STICK, "&#C77DFF&lCRAFT WAND",
                List.of(
                        "&8Utility", "",
                        "&fCrafting istantaneo"
                ),
                List.of()));

        registry.register(new AmethystToolDefinition(plugin, "sell_wand",
                Material.STICK, "&#C77DFF&lSELL WAND",
                List.of(
                        "&8Economy", "",
                        "&fVende contenuto container"
                ),
                List.of()));

        registry.register(new AmethystToolDefinition(plugin, "lightning_wand",
                Material.STICK, "&#C77DFF&lLIGHTNING WAND",
                List.of(
                        "&8Weapon", "",
                        "&fEvoca fulmini"
                ),
                List.of()));
    }
}
