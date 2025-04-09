package fr.mewtrpg.particle.motion;

import fr.mewtrpg.utils.formula.FormulaVariable;
import fr.mewtrpg.utils.VariablesHolder;

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

    public double getSize(VariablesHolder variablesHolder) {
        return sizeFormula.getValue(variablesHolder);
    }

    public double getDuration(VariablesHolder variablesHolder) {
        return durationFormula.getValue(variablesHolder);
    }

    public double getDelay(VariablesHolder variablesHolder) {
        if (delayFormula == null) {
            return 0;
        }
        return delayFormula.getValue(variablesHolder);
    }

    public MotionScale getMotionScale(VariablesHolder variablesHolder) {
        double size = getSize(variablesHolder);
        double duration = getDuration(variablesHolder);
        double delay = getDelay(variablesHolder);
        return new MotionScale(size, duration, delay);
    }
}
