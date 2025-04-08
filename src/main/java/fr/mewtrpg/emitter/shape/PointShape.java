package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import net.minestom.server.coordinate.Vec;

public class PointShape extends EmmiterShape {
    @Override
    public Vec randomPositionInShape(Emitter emitter) {
        return new Vec(0, 0, 0).add(getOffset(emitter));
    }
}
