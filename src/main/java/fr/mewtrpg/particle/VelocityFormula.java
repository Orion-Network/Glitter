package fr.mewtrpg.particle;

import fr.mewtrpg.Particle;

public interface VelocityFormula {
    double compute(double time, Particle particle);
}
