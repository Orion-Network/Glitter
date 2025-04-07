package fr.mewtrpg.particle;

import fr.mewtrpg.Particle;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityVelocityPacket;
import net.minestom.server.utils.PacketUtils;

@Getter
public class Motion {
    private final MotionMode mode;
    @Setter
    private Vec direction = new Vec(0, 0, 0);
    private final double speed;
    private final Vec acceleration;
    private final MotionScale scale;

    public Motion(MotionMode mode, double speed, Vec acceleration, MotionScale scale) {
        this.mode = mode;
        this.speed = speed;
        this.acceleration = acceleration;
        this.scale = scale;
    }

    public void apply(Particle particle) {
        Vec direction;
        switch (mode) {
            case INWARD -> direction = particle.getEmitter().position().sub(particle.getParticlePosition()).normalize();
            case OUTWARD -> direction = particle.getParticlePosition().sub(particle.getEmitter().position()).normalize();
            case DIRECTION -> direction = this.direction.normalize();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        Vec velocity = direction.mul(speed);
        //TODO Acceleration : velocity = velocity.mul(acceleration.mul(aliveTime/1000));
        particle.setVelocity(velocity);
        PacketUtils.sendPacket(particle.getAudience(), new EntityVelocityPacket(particle.getEntityId(), velocity));
        System.out.println("Velocity: " + velocity);

        AbstractDisplayMeta displayMeta = (AbstractDisplayMeta) particle.getEntityMeta();
        if (System.currentTimeMillis() > particle.getLifeTime() - scale.duration) {
            double scaleFactor = (System.currentTimeMillis() - (particle.getLifeTime() - scale.duration)) / scale.duration;
            displayMeta.setScale(new Vec(scale.size * scaleFactor, scale.size * scaleFactor, scale.size * scaleFactor));
            EntityMetaDataPacket metadataPacket = particle.getMetadataPacket();
            PacketUtils.sendPacket(particle.getAudience(), new EntityMetaDataPacket(particle.getEntityId(), metadataPacket.entries()));
        }

    }

    public record MotionScale(double size, double duration, double delay) {
        public MotionScale(double size, double duration) {
            this(size, duration, 0);
        }
    }
    public enum MotionMode {
        INWARD, OUTWARD, DIRECTION
    }
}
