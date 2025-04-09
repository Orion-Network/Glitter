package fr.mewtrpg.utils.formula;

import fr.mewtrpg.utils.SerializableExpression;
import fr.mewtrpg.utils.VariablesHolder;
import net.minestom.server.coordinate.Vec;

public class FormulaVec {
    SerializableExpression x, y, z;

    public FormulaVec(SerializableExpression x, SerializableExpression y, SerializableExpression z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void apply(VariablesHolder variables) {
        x.getExpression().setVariables(variables.getVariables());
        y.getExpression().setVariables(variables.getVariables());
        z.getExpression().setVariables(variables.getVariables());
    }

    public Vec getVec(VariablesHolder variables) {
        apply(variables);
        return new Vec(x.getExpression().evaluate(), y.getExpression().evaluate(), z.getExpression().evaluate());
    }
}
