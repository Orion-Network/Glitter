package fr.mewtrpg.emitter;

import net.minestom.server.coordinate.Vec;

public class SphereShape extends ParticleShape {
    private final double radius;

    public SphereShape(double radius) {
        this.radius = radius;
    }

    @Override
    public Vec randomPositionInShape() {
        double theta = Math.random() * 2 * Math.PI;
        double phi = Math.acos(1 - 2 * Math.random());
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);

        return new Vec(x, y, z).add(getOffset());
    }
}
