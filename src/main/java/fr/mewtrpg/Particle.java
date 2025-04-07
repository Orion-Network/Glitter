package fr.mewtrpg;

import fr.mewtrpg.particle.ParticleData;
import lombok.Getter;
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

import java.util.UUID;

@Getter
public class Particle extends Entity {
    final ParticleData particleData;
    final long lifeTime;
    private Emitter emitter;
    private Vec particlePosition;
    private Audience audience;
    public Particle(ParticleData particleData) {
        super(EntityType.BLOCK_DISPLAY);
        this.particleData = particleData;
        this.lifeTime = particleData.lifeTime() + System.currentTimeMillis();

        particleData.appearance().apply(this);
    }

    public void play(Audience audience, Emitter emitter, Vec position) {
        this.audience = audience;
        this.emitter = emitter;
        this.particlePosition = position;

        SpawnEntityPacket packet = (SpawnEntityPacket) this.getEntityType().registry().spawnType().getSpawnPacket(this);
        EntityMetaDataPacket metadataPacket = this.getMetadataPacket();

        int START_ENTITY_ID = this.getEntityId();
        SpawnEntityPacket fakePacket = new SpawnEntityPacket(START_ENTITY_ID, UUID.randomUUID(), packet.type(),
                position.asPosition(), packet.headRot(), packet.data(), (short) 0, (short) 10, (short) 0);

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
