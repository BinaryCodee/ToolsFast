package it.bypasser.toolsfast.configuration;

import it.bypasser.toolsfast.ToolsFast;
import it.bypasser.toolsfast.utils.Colors;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ConfigFile {

    private final ToolsFast plugin;
    private final String name;
    private File file;
    private FileConfiguration config;

    public ConfigFile(ToolsFast plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        reload();
    }

    public void reload() {
        file = new File(plugin.getDataFolder(), name);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            try (InputStream in = plugin.getResource(name)) {
                if (in != null) {
                    plugin.saveResource(name, false);
                } else {
                    file.createNewFile();
                }
            } catch (IOException e) {
                plugin.getLogger().warning("Cannot create " + name + ": " + e.getMessage());
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        try (InputStream in = plugin.getResource(name)) {
            if (in != null) {
                YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8));
                config.setDefaults(defaults);
            }
        } catch (IOException ignored) {}
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Cannot save " + name + ": " + e.getMessage());
        }
    }

    public FileConfiguration get() { return config; }

    public String prefix() {
        return Colors.color(config.getString("prefix", "&8[&#C77DFF&lToolsFast&8] &7"));
    }

    public String message(String path) {
        return Colors.color(config.getString(path, ""));
    }

    public List<String> messages(String path) {
        List<String> out = new ArrayList<>();
        List<String> raw = config.getStringList(path);
        if (raw.isEmpty()) {
            String single = config.getString(path);
            if (single != null) raw.add(single);
        }
        for (String s : raw) out.add(Colors.color(s));
        return out;
    }

    public String name() { return name; }
}
