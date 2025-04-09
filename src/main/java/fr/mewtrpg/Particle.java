package fr.mewtrpg;

import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.utils.VariablesHolder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityVelocityPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import net.minestom.server.utils.PacketUtils;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class Particle extends Entity implements VariablesHolder {
    final ParticleData particleData;
    final long lifeTime;
    private Emitter emitter;
    @Setter
    private Pos particlePosition;
    private Audience audience;

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

    public void play(Audience audience, Emitter emitter) {
        this.audience = audience;
        this.emitter = emitter;

        SpawnEntityPacket packet = (SpawnEntityPacket) this.getEntityType().registry().spawnType().getSpawnPacket(this);
        EntityMetaDataPacket metadataPacket = this.getMetadataPacket();

        int START_ENTITY_ID = this.getEntityId();
        SpawnEntityPacket fakePacket = new SpawnEntityPacket(START_ENTITY_ID, UUID.randomUUID(), packet.type(),
                particlePosition, packet.headRot(), packet.data(), (short) 0, (short) 10, (short) 0);

        EntityMetaDataPacket fakeMetadataPacket = new EntityMetaDataPacket(START_ENTITY_ID, metadataPacket.entries());

        PacketUtils.sendPacket(audience, fakePacket);
        PacketUtils.sendPacket(audience, fakeMetadataPacket);
    }

    public void setPhysics(boolean hasPhysics) {
        this.hasPhysics = hasPhysics;
    }

    @Override
    public void tick(long time) {
        particleData.motion().apply(this);
    }
}
