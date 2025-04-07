package fr.mewtrpg.particle;

import fr.mewtrpg.Particle;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.utils.PacketUtils;


@Getter
public abstract class Motion {
    private final MotionMode mode;
    @Setter
    private Vec direction = new Vec(0, 0, 0);
    private final Vec acceleration;
    private final MotionScale scale;

    public Motion(MotionMode mode, Vec acceleration, MotionScale scale) {
        this.mode = mode;
        this.acceleration = acceleration;
        this.scale = scale;
    }

    public void apply(Particle particle) {
        Vec velocity = getVec(particle);
        particle.setVelocity(velocity);
        PacketUtils.sendPacket(particle.getAudience(),
                EntityPositionPacket.getPacket(particle.getEntityId(), particle.getPosition().add(velocity), particle.getPosition(), false));

        AbstractDisplayMeta displayMeta = (AbstractDisplayMeta) particle.getEntityMeta();
        if (System.currentTimeMillis() > particle.getLifeTime() - scale.duration()) {
            double scaleFactor = (System.currentTimeMillis() - (particle.getLifeTime() - scale.duration())) / scale.duration();
            displayMeta.setScale(new Vec(scale.size() * scaleFactor, scale.size() * scaleFactor, scale.size() * scaleFactor));
            EntityMetaDataPacket metadataPacket = particle.getMetadataPacket();
            PacketUtils.sendPacket(particle.getAudience(), new EntityMetaDataPacket(particle.getEntityId(), metadataPacket.entries()));
        }

    }

    public abstract Vec getVec(Particle particle);



    public record MotionScale(double size, double duration, double delay) {
        public MotionScale(double size, double duration) {
            this(size, duration, 0);
        }
    }
    public enum MotionMode {
        INWARD, OUTWARD, DIRECTION
    }
}
