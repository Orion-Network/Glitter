package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

@Getter
public class BoxShape extends EmmiterShape {
    private final Vec size;

    public BoxShape(Vec size) {
        this.size = size;
    }

    @Override
    public Pos randomPositionInShape(Emitter emitter) {
        double x = Math.random() * size.x() - size.x() / 2;
        double y = Math.random() * size.y() - size.y() / 2;
        double z = Math.random() * size.z() - size.z() / 2;

        return new Pos(x, y, z).add(getOffset(emitter));
    }
}
