package fr.mewtrpg.particle;

import fr.mewtrpg.Particle;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.network.packet.server.play.EntityVelocityPacket;
import net.minestom.server.utils.PacketUtils;
import org.jetbrains.annotations.NotNull;

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
        Vec velocity = getVec(particle);
        particle.setVelocity(velocity);
        PacketUtils.sendPacket(particle.getAudience(),
                EntityPositionPacket.getPacket(particle.getEntityId(), particle.getPosition().add(velocity), particle.getPosition(), false));

        AbstractDisplayMeta displayMeta = (AbstractDisplayMeta) particle.getEntityMeta();
        if (System.currentTimeMillis() > particle.getLifeTime() - scale.duration) {
            double scaleFactor = (System.currentTimeMillis() - (particle.getLifeTime() - scale.duration)) / scale.duration;
            displayMeta.setScale(new Vec(scale.size * scaleFactor, scale.size * scaleFactor, scale.size * scaleFactor));
            EntityMetaDataPacket metadataPacket = particle.getMetadataPacket();
            PacketUtils.sendPacket(particle.getAudience(), new EntityMetaDataPacket(particle.getEntityId(), metadataPacket.entries()));
        }

    }

    private @NotNull Vec getVec(Particle particle) {
        Vec direction;
        switch (mode) {
            case INWARD -> direction = particle.getEmitter().getPosition().sub(particle.getParticlePosition()).normalize();
            case OUTWARD -> direction = particle.getParticlePosition().sub(particle.getEmitter().getPosition()).normalize();
            case DIRECTION -> direction = this.direction.normalize();
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        }

        Vec velocity = direction.mul(speed);
        //velocity = velocity.add(acceleration);
        return velocity;
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
