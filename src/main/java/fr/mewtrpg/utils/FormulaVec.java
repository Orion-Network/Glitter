package fr.mewtrpg.utils;

import net.minestom.server.coordinate.Vec;
import net.objecthunter.exp4j.Expression;

import java.util.HashMap;

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
