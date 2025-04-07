package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import net.minestom.server.coordinate.Vec;

public class BoxShape extends ParticleShape {
    private final Vec size;

    public BoxShape(Vec size) {
        this.size = size;
    }

    @Override
    public Vec randomPositionInShape(Emitter emitter) {
        double x = Math.random() * size.x() - size.x() / 2;
        double y = Math.random() * size.y() - size.y() / 2;
        double z = Math.random() * size.z() - size.z() / 2;

        return new Vec(x, y, z).add(getOffset(emitter));
    }
}
