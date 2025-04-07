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

import java.util.HashMap;


@Getter
public abstract class Motion implements VariablesHolder {
    @Setter
    private Vec direction = new Vec(0, 0, 0);

    private final HashMap<String, Double> variables = new HashMap<>();

    public void apply(Particle particle) {
        variables.put("time", (System.currentTimeMillis() - particle.getCreationTime()) / 1000.0);
        Vec velocity = getVelocityVec(particle);
        PacketUtils.sendPacket(particle.getAudience(),
                EntityPositionPacket.getPacket(particle.getEntityId(), particle.getPosition().add(velocity), particle.getPosition(), false));

        AbstractDisplayMeta displayMeta = (AbstractDisplayMeta) particle.getEntityMeta();
        if (System.currentTimeMillis() > particle.getLifeTime() - getMotionScale(particle).duration()) {
            double scaleFactor = (System.currentTimeMillis() - (particle.getLifeTime() - getMotionScale(particle).duration())) / getMotionScale(particle).duration();
            displayMeta.setScale(new Vec(getMotionScale(particle).size() * scaleFactor, getMotionScale(particle).size() * scaleFactor, getMotionScale(particle).size() * scaleFactor));
            EntityMetaDataPacket metadataPacket = particle.getMetadataPacket();
            PacketUtils.sendPacket(particle.getAudience(), new EntityMetaDataPacket(particle.getEntityId(), metadataPacket.entries()));
        }

    }

    public abstract Vec getVelocityVec(Particle particle);
    public abstract MotionScale getMotionScale(Particle particle);

    @Override
    public HashMap<String, Double> getVariables() {
        return variables;
    }
}
