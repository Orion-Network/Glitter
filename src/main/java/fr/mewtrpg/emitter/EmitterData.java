package fr.mewtrpg.emitter;

import fr.mewtrpg.emitter.shape.EmmiterShape;
import fr.mewtrpg.particle.ParticleData;

public record EmitterData(ParticleData particleData, int amount, EmitterMode mode, EmmiterShape shape) {
}
