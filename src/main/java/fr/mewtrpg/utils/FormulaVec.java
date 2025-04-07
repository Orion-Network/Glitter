package fr.mewtrpg.utils;

import net.minestom.server.coordinate.Vec;
import net.objecthunter.exp4j.Expression;

import java.util.HashMap;

public class FormulaVec {
    Expression x, y, z;

    public FormulaVec(Expression x, Expression y, Expression z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void apply(VariablesHolder variables) {
        x.setVariables(variables.getVariables());
        y.setVariables(variables.getVariables());
        z.setVariables(variables.getVariables());
    }

    public Vec getVec(VariablesHolder variables) {
        apply(variables);
        return new Vec(x.evaluate(), y.evaluate(), z.evaluate());
    }
}
