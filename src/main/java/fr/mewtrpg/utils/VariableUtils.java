package fr.mewtrpg.utils;

import net.objecthunter.exp4j.Expression;

public class VariableUtils {
    static void setUtilsVariables(Expression expression) {
        for (String key : expression.getVariableNames()) {
            if(key.startsWith("random")) {
                String[] parts = key.split("_");
                if (parts.length == 3) {
                    String variableName = parts[0];
                    double min = Double.parseDouble(parts[1]);
                    double max = Double.parseDouble(parts[2]);
                    double randomValue = Math.random() * (max - min) + min;
                    expression.setVariable(variableName, randomValue);
                } else {
                    System.out.println("Invalid variable name: " + key);
                    throw new IllegalArgumentException("Invalid variable name: " + key);
                }
            }
        }
    }
}
