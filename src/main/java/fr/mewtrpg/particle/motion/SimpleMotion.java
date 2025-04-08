package fr.mewtrpg.particle.motion;

import fr.mewtrpg.Particle;
import lombok.Getter;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

@Getter
public class SimpleMotion extends Motion {
    private final MotionMode mode;
    private final double speed;
    private final Vec direction, acceleration;
    private final MotionScale scale;

    public SimpleMotion(MotionMode mode, double speed, Vec direction, Vec acceleration, MotionScale scale) {
        this.mode = mode;
        this.direction = direction;
        this.speed = speed;
        this.acceleration = acceleration;
        this.scale = scale;
    }

    @NotNull
    public Vec getVelocityVec(Particle particle) {
        Vec direction;
        switch (getMode()) {
            case INWARD -> direction = particle.getEmitter().getPosition().sub(particle.getParticlePosition()).normalize();
            case OUTWARD -> direction = particle.getParticlePosition().sub(particle.getEmitter().getPosition()).normalize();
            case DIRECTION -> direction = this.getDirection().normalize();
            default -> throw new IllegalStateException("Unexpected value: " + getMode());
        }

        Vec velocity = direction.mul(speed);
        //velocity = velocity.add(acceleration);
        return velocity;
    }

    @Override
    public MotionScale getMotionScale(Particle particle) {
        return scale;
    }


    public enum MotionMode {
        INWARD, OUTWARD, DIRECTION
    }
}
