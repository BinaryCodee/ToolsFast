package it.bypasser.toolsfast;

import it.bypasser.toolsfast.api.ToolsFastAPI;
import it.bypasser.toolsfast.api.ToolsFastAPIHolder;
import it.bypasser.toolsfast.commands.ToolsFastCommand;
import it.bypasser.toolsfast.configuration.ConfigFile;
import it.bypasser.toolsfast.database.Database;
import it.bypasser.toolsfast.economy.EconomyManager;
import it.bypasser.toolsfast.gui.GuiManager;
import it.bypasser.toolsfast.hooks.HookManager;
import it.bypasser.toolsfast.listeners.BlockBreakListener;
import it.bypasser.toolsfast.listeners.InteractListener;
import it.bypasser.toolsfast.listeners.InventoryListener;
import it.bypasser.toolsfast.listeners.JoinQuitListener;
import it.bypasser.toolsfast.managers.APIImpl;
import it.bypasser.toolsfast.managers.AbilityRegistryImpl;
import it.bypasser.toolsfast.managers.CustomItemManagerImpl;
import it.bypasser.toolsfast.managers.EnchantRegistryImpl;
import it.bypasser.toolsfast.particles.ParticleEngineImpl;
import it.bypasser.toolsfast.managers.PlaceholderRegistryImpl;
import it.bypasser.toolsfast.tool.selfdestruct.SelfDestructManagerImpl;
import it.bypasser.toolsfast.managers.ShopProviderRegistryImpl;
import it.bypasser.toolsfast.managers.StatisticsManagerImpl;
import it.bypasser.toolsfast.managers.ToolRegistryImpl;
import it.bypasser.toolsfast.nms.NmsAdapter;
import it.bypasser.toolsfast.nms.NmsFactory;
import it.bypasser.toolsfast.particles.ParticleTask;
import it.bypasser.toolsfast.placeholders.ToolsFastExpansion;
import it.bypasser.toolsfast.shops.BuiltInShopProviders;
import it.bypasser.toolsfast.tasks.SelfDestructTask;
import it.bypasser.toolsfast.tool.ToolRegistry;
import it.bypasser.toolsfast.tool.abilities.BuiltInAbilities;
import it.bypasser.toolsfast.tool.custom.BuiltInTools;
import it.bypasser.toolsfast.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ToolsFast extends JavaPlugin {

    private static ToolsFast instance;

    private ConfigFile configFile;
    private ConfigFile messagesFile;
    private ConfigFile guiFile;
    private ConfigFile toolsFile;

    private Database database;
    private NmsAdapter nms;

    private ToolRegistry toolRegistry;
    private CustomItemManagerImpl customItemManager;
    private ToolRegistryImpl toolRegistryImpl;
    private SelfDestructManagerImpl selfDestructManager;
    private StatisticsManagerImpl statisticsManager;
    private ShopProviderRegistryImpl shopProviderRegistry;
    private ParticleEngineImpl particleEngine;
    private AbilityRegistryImpl abilityRegistry;
    private EnchantRegistryImpl enchantRegistry;
    private PlaceholderRegistryImpl placeholderRegistry;

    private EconomyManager economyManager;
    private HookManager hookManager;
    private GuiManager guiManager;
    private APIImpl api;

    private SelfDestructTask selfDestructTask;
    private ParticleTask particleTask;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        configFile = new ConfigFile(this, "config.yml");
        messagesFile = new ConfigFile(this, "messages.yml");
        guiFile = new ConfigFile(this, "gui.yml");
        toolsFile = new ConfigFile(this, "tools.yml");

        try {
            database = new Database(this);
            database.connect();
            getLogger().info("Database connected.");
        } catch (Exception e) {
            getLogger().severe("Database connection failed: " + e.getMessage());
        }

        try {
            nms = NmsFactory.get();
            getLogger().info("NMS adapter: " + nms.version());
        } catch (Throwable t) {
            getLogger().warning("NMS adapter not available, using fallback: " + t.getMessage());
        }

        abilityRegistry = new AbilityRegistryImpl();
        enchantRegistry = new EnchantRegistryImpl();
        placeholderRegistry = new PlaceholderRegistryImpl();
        particleEngine = new ParticleEngineImpl(this);
        statisticsManager = new StatisticsManagerImpl(this, database);
        shopProviderRegistry = new ShopProviderRegistryImpl();
        customItemManager = new CustomItemManagerImpl(this);
        toolRegistryImpl = new ToolRegistryImpl(this);
        selfDestructManager = new SelfDestructManagerImpl(this);

        economyManager = new EconomyManager(this);
        hookManager = new HookManager(this);
        hookManager.hookAll();

        BuiltInAbilities.registerAll(abilityRegistry, this);
        BuiltInTools.registerAll(toolRegistryImpl, this);
        BuiltInShopProviders.registerAll(shopProviderRegistry, this);

        customItemManager.load();

        guiManager = new GuiManager(this);

        api = new APIImpl(
                customItemManager, toolRegistryImpl, selfDestructManager,
                statisticsManager, shopProviderRegistry, particleEngine,
                abilityRegistry, enchantRegistry, placeholderRegistry, this
        );
        ToolsFastAPIHolder.set(api);

        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(guiManager, this);

        ToolsFastCommand command = new ToolsFastCommand(this);
        getCommand("toolsfast").setExecutor(command);
        getCommand("toolsfast").setTabCompleter(command);

        selfDestructTask = new SelfDestructTask(this);
        selfDestructTask.start();

        particleTask = new ParticleTask(this);
        particleTask.start();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ToolsFastExpansion(this).register();
            getLogger().info("PlaceholderAPI hooked.");
        }

        getLogger().info(Colors.color("&8[&#C77DFF&lToolsFast&8] &aEnabled &7v" + getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        if (selfDestructTask != null) selfDestructTask.stop();
        if (particleTask != null) particleTask.stop();
        if (database != null) database.close();
        if (nms != null) {
            for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
                try { nms.removeBossBar(p); } catch (Throwable ignored) {}
            }
        }
    }

    public void reloadAll() {
        reloadConfig();
        configFile.reload();
        messagesFile.reload();
        guiFile.reload();
        toolsFile.reload();
        customItemManager.load();
        toolRegistryImpl.reload();
    }

    public static ToolsFast get() { return instance; }

    public ConfigFile config() { return configFile; }
    public ConfigFile messages() { return messagesFile; }
    public ConfigFile guiConfig() { return guiFile; }
    public ConfigFile tools() { return toolsFile; }

    public Database database() { return database; }
    public NmsAdapter nms() { return nms; }

    public ToolRegistryImpl toolRegistry() { return toolRegistryImpl; }
    public CustomItemManagerImpl customItems() { return customItemManager; }
    public SelfDestructManagerImpl selfDestruct() { return selfDestructManager; }
    public StatisticsManagerImpl statistics() { return statisticsManager; }
    public ShopProviderRegistryImpl shopProviders() { return shopProviderRegistry; }
    public ParticleEngineImpl particles() { return particleEngine; }
    public AbilityRegistryImpl abilityRegistry() { return abilityRegistry; }
    public EnchantRegistryImpl enchantRegistry() { return enchantRegistry; }
    public PlaceholderRegistryImpl placeholderRegistry() { return placeholderRegistry; }

    public EconomyManager economy() { return economyManager; }
    public HookManager hooks() { return hookManager; }
    public GuiManager gui() { return guiManager; }

    public ToolsFastAPI api() { return api; }
}
