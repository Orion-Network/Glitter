package fr.mewtrpg.utils;

import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@Getter
public class SerializableExpression {
    public final String expressionString;
    public final String[] variables;
    public transient final Expression expression;

    public SerializableExpression(String expressionString, String... variables) {
        this.expressionString = expressionString;
        this.variables = variables;
        this.expression = new ExpressionBuilder(expressionString)
                .variables(variables)
                .build();
    }
}
