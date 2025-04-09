package fr.mewtrpg.emitter.shape;

import fr.mewtrpg.Emitter;
import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

@Getter
public class SphereShape extends EmmiterShape {
    private final double radius;

    public SphereShape(double radius) {
        this.radius = radius;
    }

    @Override
    public Pos randomPositionInShape(Emitter emitter) {
        if(isSurface()) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.acos(1 - 2 * Math.random());
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            return new Pos(x, y, z).add(getOffset(emitter));
        }

        double x = Math.random() * 2 * radius - radius;
        double y = Math.random() * 2 * radius - radius;
        double z = Math.random() * 2 * radius - radius;
        double distance = Math.sqrt(x * x + y * y + z * z);
        if (distance > radius) {
            x = (x / distance) * radius;
            y = (y / distance) * radius;
            z = (z / distance) * radius;
        }
        return new Pos(x, y, z).add(getOffset(emitter));
    }
}
