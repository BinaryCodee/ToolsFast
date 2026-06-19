package it.bypasser.toolsfast.hooks;

import it.bypasser.toolsfast.ToolsFast;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class HookManager {

    private final ToolsFast plugin;
    private boolean vault;
    private boolean worldGuard;
    private boolean placeholderApi;
    private boolean shopGuiPlus;
    private boolean economyShopGui;

    public HookManager(ToolsFast plugin) {
        this.plugin = plugin;
    }

    public void hookAll() {
        vault = Bukkit.getPluginManager().getPlugin("Vault") != null;
        worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        placeholderApi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        shopGuiPlus = Bukkit.getPluginManager().getPlugin("ShopGUIPlus") != null;
        economyShopGui = Bukkit.getPluginManager().getPlugin("EconomyShopGUI") != null;
        plugin.getLogger().info("Hooks: Vault=" + vault + " WorldGuard=" + worldGuard + " PAPI=" + placeholderApi + " ShopGUIPlus=" + shopGuiPlus + " EconomyShopGUI=" + economyShopGui);
    }

    public boolean vault() { return vault; }
    public boolean worldGuard() { return worldGuard; }
    public boolean placeholderApi() { return placeholderApi; }
    public boolean shopGuiPlus() { return shopGuiPlus; }
    public boolean economyShopGui() { return economyShopGui; }

    public boolean canBuild(Player player, Location location) {
        if (!worldGuard) return true;
        try {
            Class<?> wgClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Method getInstance = wgClass.getMethod("getInstance");
            Object wg = getInstance.invoke(null);
            Method getFlagRegistry = wg.getClass().getMethod("getFlagRegistry");
            Object flagRegistry = getFlagRegistry.invoke(wg);
            Method getFlag = flagRegistry.getClass().getMethod("get", String.class, Class.class);
            Class<?> stateFlagClass = Class.forName("com.sk89q.worldguard.protection.flags.StateFlag");
            Object blockBreakFlag = getFlag.invoke(flagRegistry, "block-break", stateFlagClass);
            if (blockBreakFlag == null) return true;
            Method getPlatform = wg.getClass().getMethod("getPlatform");
            Object platform = getPlatform.invoke(wg);
            Method getRegionContainer = platform.getClass().getMethod("getRegionContainer");
            Object container = getRegionContainer.invoke(platform);
            Method createQuery = container.getClass().getMethod("createQuery");
            Object query = createQuery.invoke(container);
            Class<?> worldEditLocationClass = Class.forName("com.sk89q.worldedit.util.Location");
            Class<?> worldClass = Class.forName("com.sk89q.worldedit.world.World");
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Method adaptWorld = bukkitAdapterClass.getMethod("adapt", org.bukkit.World.class);
            Object weWorld = adaptWorld.invoke(null, location.getWorld());
            Class<?> vector3Class = Class.forName("com.sk89q.worldedit.math.BlockVector3");
            Method at = vector3Class.getMethod("at", double.class, double.class, double.class);
            Object pos = at.invoke(null, location.getX(), location.getY(), location.getZ());
            Class<?> vector3at = Class.forName("com.sk89q.worldedit.math.BlockVector3");
            java.lang.reflect.Constructor<?> locCtor = worldEditLocationClass.getConstructor(worldClass, vector3Class);
            Object weLoc = locCtor.newInstance(weWorld, pos);
            Class<?> wgPluginClass = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
            Method wgPluginInst = wgPluginClass.getMethod("inst");
            Object wgPlugin = wgPluginInst.invoke(null);
            Method wrapPlayer = wgPlugin.getClass().getMethod("wrapPlayer", Player.class);
            Object wrappedPlayer = wrapPlayer.invoke(wgPlugin, player);
            Method testState = query.getClass().getMethod("testState", worldEditLocationClass, Class.forName("com.sk89q.worldguard.LocalPlayer"), stateFlagClass);
            Object result = testState.invoke(query, weLoc, wrappedPlayer, blockBreakFlag);
            return Boolean.TRUE.equals(result);
        } catch (Throwable t) {
            return true;
        }
    }
}
