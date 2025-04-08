package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import net.minestom.server.coordinate.Vec;

public class PointShape extends ParticleShape {
    @Override
    public Vec randomPositionInShape(Emitter emitter) {
        return new Vec(0, 0, 0);
    }
}
