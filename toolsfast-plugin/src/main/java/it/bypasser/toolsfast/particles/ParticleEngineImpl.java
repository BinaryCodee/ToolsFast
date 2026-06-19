package it.bypasser.toolsfast.particles;

import it.bypasser.toolsfast.api.ParticleEffect;
import org.bukkit.Location;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ParticleEngineImpl implements it.bypasser.toolsfast.api.ToolsFastAPI.ParticleEngine {

    private final Map<String, ParticleEffect> map = new LinkedHashMap<>();
    private final it.bypasser.toolsfast.ToolsFast plugin;

    public ParticleEngineImpl(it.bypasser.toolsfast.ToolsFast plugin) {
        this.plugin = plugin;
    }

    @Override
    public void spawn(Location location, String particleId, int count, double offsetX, double offsetY, double offsetZ, double speed) {
        ParticleEffect effect = map.get(particleId.toLowerCase());
        if (effect != null) {
            effect.play(location, count, offsetX, offsetY, offsetZ, speed);
            return;
        }
        if (plugin.nms() != null && location.getWorld() != null) {
            for (org.bukkit.entity.Player p : location.getWorld().getPlayers()) {
                if (p.getLocation().distanceSquared(location) < 64 * 64) {
                    plugin.nms().spawnParticle(p, location, particleId, count, offsetX, offsetY, offsetZ, speed);
                }
            }
        }
    }

    @Override
    public synchronized void registerParticle(String id, ParticleEffect effect) {
        if (id == null || effect == null) return;
        map.put(id.toLowerCase(), effect);
    }

    public Collection<ParticleEffect> effects() { return map.values(); }
}
