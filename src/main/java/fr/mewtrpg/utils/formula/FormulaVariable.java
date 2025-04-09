package fr.mewtrpg.utils.formula;


import fr.mewtrpg.utils.SerializableExpression;
import fr.mewtrpg.utils.VariablesHolder;

public class FormulaVariable {
    private final SerializableExpression expression;

    public FormulaVariable(SerializableExpression expression) {
        this.expression = expression;
    }

    public void apply(VariablesHolder variables) {
        expression.getExpression().setVariables(variables.getVariables());
    }

    public double getValue(VariablesHolder variables) {
        apply(variables);
        return expression.getExpression().evaluate();
    }
}
