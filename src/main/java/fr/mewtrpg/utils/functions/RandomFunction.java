package fr.mewtrpg.utils.functions;

import net.objecthunter.exp4j.function.Function;

public class RandomFunction extends Function {
    public RandomFunction() {
        super("random", 2);
    }

    @Override
    public double apply(double... args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Random function requires two arguments");
        }
        double min = args[0];
        double max = args[1];
        return Math.random() * (max - min) + min;
    }
}
