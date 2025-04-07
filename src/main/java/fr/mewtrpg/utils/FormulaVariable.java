package fr.mewtrpg.utils;


import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class FormulaVariable {
    private final Expression expression;

    public FormulaVariable(Expression expression) {
        this.expression = expression;
    }

    public void apply(VariablesHolder variables) {
        expression.setVariables(variables.getVariables());
    }

    public double getValue(VariablesHolder variables) {
        apply(variables);
        return expression.evaluate();
    }
}
