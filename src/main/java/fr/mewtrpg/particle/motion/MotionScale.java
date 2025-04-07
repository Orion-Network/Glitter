package fr.mewtrpg.particle.motion;

public record MotionScale(double size, double duration, double delay) {
    public MotionScale(double size, double duration) {
        this(size, duration, 0);
    }
}
