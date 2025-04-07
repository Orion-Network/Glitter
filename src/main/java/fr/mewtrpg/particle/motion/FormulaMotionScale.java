package fr.mewtrpg.particle.motion;

import fr.mewtrpg.Particle;
import fr.mewtrpg.utils.FormulaVariable;

public class FormulaMotionScale {
    private final FormulaVariable sizeFormula;
    private final FormulaVariable durationFormula;
    private final FormulaVariable delayFormula;

    public FormulaMotionScale(FormulaVariable sizeFormula, FormulaVariable durationFormula, FormulaVariable delayFormula) {
        this.sizeFormula = sizeFormula;
        this.durationFormula = durationFormula;
        this.delayFormula = delayFormula;
    }

    public FormulaMotionScale(FormulaVariable sizeFormula, FormulaVariable durationFormula) {
        this(sizeFormula, durationFormula, null);
    }

    public double getSize(Motion motion) {
        return sizeFormula.getValue(motion);
    }

    public double getDuration(Motion motion) {
        return durationFormula.getValue(motion);
    }

    public double getDelay(Motion motion) {
        if (delayFormula == null) {
            return 0;
        }
        return delayFormula.getValue(motion);
    }

    public MotionScale getMotionScale(Motion motion) {
        double size = getSize(motion);
        double duration = getDuration(motion);
        double delay = getDelay(motion);
        return new MotionScale(size, duration, delay);
    }
}
