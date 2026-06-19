package it.bypasser.toolsfast.api;

import org.bukkit.Location;

public interface ParticleEffect {
    void play(Location location, int count, double offsetX, double offsetY, double offsetZ, double speed);
}
