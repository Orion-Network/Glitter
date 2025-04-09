package fr.mewtrpg;

import fr.mewtrpg.emitter.EmitterData;
import fr.mewtrpg.emitter.EmitterMode;
import fr.mewtrpg.emitter.EmitterType;
import fr.mewtrpg.emitter.shape.EmmiterShape;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.utils.VariablesHolder;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.Tickable;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.utils.PacketUtils;

import java.util.*;
import java.util.concurrent.*;

@Getter
public final class Emitter implements Tickable, VariablesHolder {

    private final ConcurrentLinkedQueue<Particle> particles = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Instance instance;
    private final Pos position;
    @Setter
    private Entity attachedEntity;
    private final EmitterData emitterData;
    private final long lifeTime;

    private final UUID uuid = UUID.randomUUID();
    private final HashMap<String, Double> variables = new HashMap<>();
    private final long creationTime = System.currentTimeMillis();
    private long lastExecution = System.currentTimeMillis();

    public Emitter(Instance instance, Pos position, EmitterData emitterData) {
        this.instance = instance;
        this.position = position;
        this.emitterData = emitterData;
        this.lifeTime = emitterData.mode().getLifeTime() + System.currentTimeMillis();
    }

    public Emitter(Instance instance, Pos position, ParticleData particleData, int amount, EmitterMode mode, EmmiterShape shape) {
        this(instance, position, new EmitterData(particleData, amount, mode, shape));
    }


    public void emit() {
        variables.put("time", (System.currentTimeMillis() - creationTime) / 1000.0);
        for (int i = 0; i < emitterData.amount(); i++) {
            Vec offset = emitterData.shape().getOffset(this);
            Vec particlePosition = emitterData.shape().randomPositionInShape(this).add(getPosition());
            Particle particle = new Particle(emitterData.particleData(), particlePosition);
            particle.getVariables().put("emitterX", getPosition().x());
            particle.getVariables().put("emitterY", getPosition().y());
            particle.getVariables().put("emitterZ", getPosition().z());

            particle.getVariables().put("offsetX", offset.x());
            particle.getVariables().put("offsetY", offset.y());
            particle.getVariables().put("offsetZ", offset.z());

            particle.play(Objects.requireNonNull(instance.getChunkAt(getPosition())).getViewersAsAudience(), this );
            particles.add(particle);
        }
    }

    public void destroy() {
        scheduler.shutdownNow();
        particles.forEach(particle ->
                PacketUtils.sendPacket(particle.getAudience(), new DestroyEntitiesPacket(particle.getEntityId())));
        particles.clear();
    }

    @Override
    public void tick(long l) {
        if (lastExecution + emitterData.mode().getDelay() < System.currentTimeMillis()) {
            emit();
            lastExecution = System.currentTimeMillis();
        }

        particles.removeIf(particle -> {
            if (System.currentTimeMillis() > particle.getLifeTime()) {
                PacketUtils.sendPacket(particle.getAudience(), new DestroyEntitiesPacket(particle.getEntityId()));
                return true;
            }
            particle.tick(l);
            return false;
        });
    }

    public boolean isDead() {
        return (emitterData.mode().getType() == EmitterType.LOOPING && System.currentTimeMillis() > this.getLifeTime());
    }

    public Instance getInstance() {
        return (attachedEntity == null ? instance : attachedEntity.getInstance());
    }

    public Pos getPosition() {
        return (attachedEntity == null ? position : attachedEntity.getPosition());
    }
}
