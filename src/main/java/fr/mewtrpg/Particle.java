package fr.mewtrpg;

import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.utils.VariablesHolder;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityVelocityPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class Particle extends Entity implements VariablesHolder {
    final ParticleData particleData;
    final long lifeTime;
    private Emitter emitter;
    @Setter
    private Pos particlePosition;
    /** L'agrandissement de fin est parti : le client l'anime seul jusqu'a la mort de la particule. */
    @Setter
    private boolean scaling;

    private final HashMap<String, Double> variables = new HashMap<>();
    private final long creationTime = System.currentTimeMillis();


    public Particle(ParticleData particleData, Pos particlePosition) {
        super(EntityType.BLOCK_DISPLAY);
        this.particleData = particleData;
        this.particlePosition = particlePosition;
        this.lifeTime = particleData.lifeTime() + System.currentTimeMillis();

        particleData.appearance().apply(this);

        variables.put("time", (System.currentTimeMillis() - getCreationTime()) / 1000.0);
        variables.put("particleX", particlePosition.x());
        variables.put("particleY", particlePosition.y());
        variables.put("particleZ", particlePosition.z());
    }

    public void play(Emitter emitter) {
        this.emitter = emitter;
        emitter.getViewers().forEach(this::show);
    }

    /** Envoie la particule telle qu'elle est maintenant : a son apparition, ou a un joueur arrive ensuite. */
    public void show(Player player) {
        SpawnEntityPacket packet = this.getSpawnPacket();
        player.sendPacket(new SpawnEntityPacket(getEntityId(), UUID.randomUUID(), packet.type(),
                particlePosition, packet.headRot(), packet.data(), new Vec(0, 10 / 8000.0, 0)));
        player.sendPacket(new EntityMetaDataPacket(getEntityId(), this.getMetadataPacket().entries()));
    }

    /** Envoie un paquet a ceux qui voient la particule. */
    public void send(SendablePacket packet) {
        if (emitter != null) {
            emitter.send(packet);
        }
    }

    public void setPhysics(boolean hasPhysics) {
        this.hasPhysics = hasPhysics;
    }

    @Override
    public void tick(long time) {
        // Les formules de mouvement lisent "time" : sans cette mise a jour, il restait a 0.
        variables.put("time", (System.currentTimeMillis() - getCreationTime()) / 1000.0);
        particleData.motion().apply(this);
    }
}
