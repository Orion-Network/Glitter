package fr.mewtrpg.particle.motion;

import net.minestom.server.coordinate.Vec;

/**
 * Taille de fin d'une particule, atteinte en {@code duration} ms a la fin de sa vie. {@code scale}, s'il est donne,
 * remplace {@code size} par une taille par axe (un rectangle allonge dans un sens, par exemple).
 */
public record MotionScale(double size, double duration, double delay, Vec scale) {
    public MotionScale(double size, double duration, double delay) {
        this(size, duration, delay, null);
    }

    public MotionScale(double size, double duration) {
        this(size, duration, 0);
    }

    public Vec target() {
        return scale != null ? scale : new Vec(size, size, size);
    }
}
