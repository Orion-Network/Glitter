package fr.mewtrpg.emitter;

import fr.mewtrpg.emitter.shape.ParticleShape;
import fr.mewtrpg.particle.ParticleData;

public record EmitterData(ParticleData particleData, int amount, EmitterMode mode, ParticleShape shape) {
}
