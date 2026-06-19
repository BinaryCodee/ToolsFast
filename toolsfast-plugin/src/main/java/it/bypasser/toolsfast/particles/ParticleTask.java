package it.bypasser.toolsfast.particles;

import it.bypasser.toolsfast.ToolsFast;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class ParticleTask {

    private final ToolsFast plugin;
    private BukkitTask task;
    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    public ParticleTask(ToolsFast plugin) {
        this.plugin = plugin;
    }

    public void submit(Runnable r) {
        queue.add(r);
    }

    public void start() {
        task = org.bukkit.Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Runnable r;
            int processed = 0;
            while ((r = queue.poll()) != null && processed < 200) {
                try { r.run(); } catch (Throwable ignored) {}
                processed++;
            }
        }, 5L, 2L);
    }

    public void stop() {
        if (task != null) task.cancel();
        queue.clear();
    }
}
