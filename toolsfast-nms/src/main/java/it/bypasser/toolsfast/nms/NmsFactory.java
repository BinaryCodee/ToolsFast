package it.bypasser.toolsfast.nms;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NmsFactory {

    private static NmsAdapter cached;
    private static final Logger LOG = Logger.getLogger("ToolsFast-NMS");

    private NmsFactory() {}

    public static NmsAdapter get() {
        if (cached != null) return cached;
        String version = detectVersion();
        ServiceLoader<NmsAdapter> loader = ServiceLoader.load(NmsAdapter.class);
        NmsAdapter best = null;
        for (NmsAdapter adapter : loader) {
            if (adapter.version().equals(version)) {
                best = adapter;
                break;
            }
            if (best == null) best = adapter;
        }
        if (best == null) {
            LOG.log(Level.SEVERE, "No NmsAdapter found, falling back to no-op");
            best = new FallbackAdapter();
        }
        cached = best;
        return cached;
    }

    public static String detectVersion() {
        String pkg = org.bukkit.Bukkit.getServer().getClass().getPackage().getName();
        String v = pkg.substring(pkg.lastIndexOf('.') + 1);
        if (v.startsWith("v")) return v;
        try {
            String bukkitVersion = org.bukkit.Bukkit.getServer().getBukkitVersion();
            if (bukkitVersion.startsWith("1.21")) return "v1_21";
            if (bukkitVersion.startsWith("1.20")) return "v1_20";
        } catch (Throwable ignored) {}
        return v;
    }
}
