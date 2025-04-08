package fr.mewtrpg.particle.motion;

import fr.mewtrpg.Particle;
import fr.mewtrpg.utils.FormulaVec;
import lombok.Getter;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

@Getter
public class FormulaMotion extends Motion {
    private final FormulaVec directionFormula, velocityFormula;
    private final FormulaMotionScale scale;

    public FormulaMotion(FormulaVec directionFormula, FormulaVec velocityFormula, FormulaMotionScale scale) {
        this.directionFormula = directionFormula;
        this.velocityFormula = velocityFormula;
        this.scale = scale;
    }

    @NotNull
    public Vec getVelocityVec(Particle particle) {
        Vec direction = directionFormula.getVec(particle);
        Vec speed = velocityFormula.getVec(particle);
        return direction.mul(speed);
    }

    @Override
    public MotionScale getMotionScale(Particle particle) {
        return scale.getMotionScale(particle);
    }
}
