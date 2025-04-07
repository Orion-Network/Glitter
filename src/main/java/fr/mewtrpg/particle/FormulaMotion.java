package fr.mewtrpg.particle;

import fr.mewtrpg.Particle;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

public class FormulaMotion extends Motion {
    private final VelocityFormula velocityFormula;

    public FormulaMotion(MotionMode mode, Vec acceleration, MotionScale scale, VelocityFormula velocityFormula) {
        super(mode, acceleration, scale);
        this.velocityFormula = velocityFormula;
    }

    @NotNull
    public Vec getVec(Particle particle) {
        Vec direction;
        switch (getMode()) {
            case INWARD -> direction = particle.getEmitter().getPosition().sub(particle.getParticlePosition()).normalize();
            case OUTWARD -> direction = particle.getParticlePosition().sub(particle.getEmitter().getPosition()).normalize();
            case DIRECTION -> direction = this.getDirection().normalize();
            default -> throw new IllegalStateException("Unexpected value: " + getMode());
        }

        double time = (System.currentTimeMillis() - particle.getCreationTime()) / 1000.0;
        double speed = velocityFormula.compute(time, particle);
        return direction.mul(speed);
    }
}
