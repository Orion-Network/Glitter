package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class PointShape extends EmmiterShape {
    @Override
    public Pos randomPositionInShape(Emitter emitter) {
        return new Pos(0, 0, 0).add(getOffset(emitter));
    }
}
