package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import fr.mewtrpg.utils.formula.FormulaPos;
import fr.mewtrpg.utils.formula.FormulaVec;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;

@Setter
public abstract class EmmiterShape {
    private Pos offset = new Pos(0, 0, 0);
    private FormulaPos offsetFormula;
    @Getter
    private boolean surface = false;

    public abstract Pos randomPositionInShape(Emitter emitter);
    public Pos getOffset(Emitter emitter) {
        if (offsetFormula != null) {
            return offsetFormula.getPos(emitter);
        }
        return offset;
    }
}
