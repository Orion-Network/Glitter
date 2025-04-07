package fr.mewtrpg.particle;

import fr.mewtrpg.particle.appearance.Appearance;
import fr.mewtrpg.particle.motion.Motion;

public record ParticleData(long lifeTime, Appearance appearance, Motion motion) {
}
