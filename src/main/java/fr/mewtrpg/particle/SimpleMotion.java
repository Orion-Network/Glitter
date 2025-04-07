package fr.mewtrpg.particle;

import fr.mewtrpg.Particle;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.utils.PacketUtils;
import org.jetbrains.annotations.NotNull;

public class SimpleMotion extends Motion {
    private final double speed;

    public SimpleMotion(MotionMode mode, Vec acceleration, MotionScale scale, double speed) {
        super(mode, acceleration, scale);
        this.speed = speed;
    }

    @NotNull
    public Vec getVec(Particle particle) {
        Vec direction;
        switch (getMode()) {
            case INWARD -> direction = particle.getEmitter().getPosition().sub(particle.getParticlePosition()).normalize();
            case OUTWARD -> direction = particle.getParticlePosition().sub(particle.getEmitter().getPosition()).normalize();
            case DIRECTION -> direction = this.getDirection().normalize();
            default -> throw new IllegalStateException("Unexpected value: " + getMode());
        }

        Vec velocity = direction.mul(speed);
        //velocity = velocity.add(acceleration);
        return velocity;
    }
}
