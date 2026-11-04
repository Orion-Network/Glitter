package fr.mewtrpg.particle.motion;

import fr.mewtrpg.Particle;
import fr.mewtrpg.utils.formula.FormulaPos;
import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;

/**
 * Particule accrochee a son emetteur : a chaque tick, elle est a la position de l'emetteur plus {@code offset}, une
 * formule qui peut dependre de {@code time}. Un emetteur attache a une entite donne une orbite qui la suit quand elle
 * bouge ({@code offset = sin(time*2)*1.5, 1.2, cos(time*2)*1.5}).
 */
@Getter
public class AnchoredMotion extends Motion {
    private final FormulaPos offset;
    private final MotionScale scale;

    public AnchoredMotion(FormulaPos offset, MotionScale scale) {
        this.offset = offset;
        this.scale = scale;
    }

    @Override
    protected Pos nextPosition(Particle particle) {
        return particle.getEmitter().getPosition().add(offset.getPos(particle));
    }

    @NotNull
    @Override
    public Vec getVelocityVec(Particle particle) {
        return Vec.ZERO;
    }

    @Override
    public MotionScale getMotionScale(Particle particle) {
        return scale;
    }
}
