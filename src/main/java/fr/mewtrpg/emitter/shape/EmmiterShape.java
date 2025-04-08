package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import fr.mewtrpg.utils.FormulaVec;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;

@Setter
public abstract class EmmiterShape {
    private Vec offset = new Vec(0, 0, 0);
    private FormulaVec offsetFormula;
    @Getter
    private boolean surface = false;

    public abstract Vec randomPositionInShape(Emitter emitter);
    public Vec getOffset(Emitter emitter) {
        if (offsetFormula != null) {
            return offsetFormula.getVec(emitter);
        }
        return offset;
    }
}
