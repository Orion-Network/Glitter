package fr.mewtrpg.particle.motion;

import fr.mewtrpg.Particle;
import fr.mewtrpg.utils.VariablesHolder;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.utils.PacketUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


@Getter
public abstract class Motion{
    private final HashMap<String, Double> variables = new HashMap<>();

    public void apply(Particle particle) {
        Vec velocity = getVelocityVec(particle);
        PacketUtils.sendPacket(particle.getAudience(),
                EntityPositionPacket.getPacket(particle.getEntityId(), particle.getPosition().add(velocity), particle.getPosition(), false));

        AbstractDisplayMeta displayMeta = (AbstractDisplayMeta) particle.getEntityMeta();
        if (System.currentTimeMillis() > particle.getLifeTime() - getMotionScale(particle).duration()) {
            double startSize = particle.getParticleData().appearance().getSize();
            double endSize = getMotionScale(particle).size();
            double scaleDiff = endSize - startSize;;
            double progress = (System.currentTimeMillis() - (particle.getLifeTime() - getMotionScale(particle).duration())) / getMotionScale(particle).duration();
            progress = Math.min(1.0, Math.max(0.0, progress));
            double newSize = startSize + (scaleDiff * progress);
            displayMeta.setScale(new Vec(newSize, newSize, newSize));

            EntityMetaDataPacket metadataPacket = particle.getMetadataPacket();
            PacketUtils.sendPacket(particle.getAudience(), new EntityMetaDataPacket(particle.getEntityId(), metadataPacket.entries()));
        }

    }

    public abstract Vec getVelocityVec(Particle particle);
    public abstract MotionScale getMotionScale(Particle particle);
}
