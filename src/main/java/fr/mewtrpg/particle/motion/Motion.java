package fr.mewtrpg.particle.motion;

import fr.mewtrpg.Particle;
import lombok.Getter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.network.packet.server.play.EntityTeleportPacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@Getter
public abstract class Motion{
    /** Un deplacement relatif ne depasse pas 8 blocs : au-dela, on teleporte. */
    private static final double MAX_RELATIVE_MOVE = 7.5;
    /** Delai avant de lancer l'agrandissement : le client doit avoir affiche la particule pour l'interpoler. */
    private static final long SCALE_START_DELAY_MILLIS = 50;

    private final HashMap<String, Double> variables = new HashMap<>();

    public void apply(Particle particle) {
        moveTo(particle, nextPosition(particle));

        // L'agrandissement part en un seul paquet, avec une interpolation qui couvre le temps restant : le client
        // l'anime en douceur. Le renvoyer a chaque tick, avec une interpolation plus longue que le tick, le faisait
        // trainer derriere sa vraie taille.
        MotionScale scale = getMotionScale(particle);
        long now = System.currentTimeMillis();
        if (particle.isScaling()
                || scale.duration() <= 0
                || now < particle.getLifeTime() - scale.duration()
                || now < particle.getCreationTime() + SCALE_START_DELAY_MILLIS) {
            return;
        }
        particle.setScaling(true);
        AbstractDisplayMeta displayMeta = (AbstractDisplayMeta) particle.getEntityMeta();
        displayMeta.setTransformationInterpolationStartDelta(0);
        displayMeta.setTransformationInterpolationDuration((int) Math.max(1, (particle.getLifeTime() - now) / 50));
        displayMeta.setScale(scale.target());
        particle.send(new EntityMetaDataPacket(particle.getEntityId(), particle.getMetadataPacket().entries()));
    }

    /** Ou sera la particule au prochain tick : par defaut, avancee de sa vitesse. */
    protected Pos nextPosition(Particle particle) {
        Vec velocity = getVelocityVec(particle);
        // Une direction nulle normalisee donne NaN : la particule reste ou elle est.
        if (!Double.isFinite(velocity.x()) || !Double.isFinite(velocity.y()) || !Double.isFinite(velocity.z())) {
            return particle.getParticlePosition();
        }
        return particle.getParticlePosition().add(velocity);
    }

    private static void moveTo(Particle particle, Pos target) {
        Pos current = particle.getParticlePosition();
        if (target.samePoint(current)) {
            return;
        }
        if (target.distance(current) > MAX_RELATIVE_MOVE) {
            particle.send(new EntityTeleportPacket(particle.getEntityId(), target, Vec.ZERO, 0, false));
        } else {
            particle.send(EntityPositionPacket.getPacket(particle.getEntityId(), target, current, false));
        }
        particle.setParticlePosition(target);
    }

    @NotNull
    public abstract Vec getVelocityVec(Particle particle);
    public abstract MotionScale getMotionScale(Particle particle);
}
