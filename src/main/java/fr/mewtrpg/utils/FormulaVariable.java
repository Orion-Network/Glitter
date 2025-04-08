package fr.mewtrpg.utils;


import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

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
