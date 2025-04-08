package fr.mewtrpg;

import fr.mewtrpg.emitter.EmitterMode;
import fr.mewtrpg.emitter.EmitterType;
import fr.mewtrpg.emitter.shape.ParticleShape;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.utils.VariablesHolder;
import lombok.Getter;
import net.minestom.server.Tickable;
import net.minestom.server.coordinate.Vec;
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
    private final Vec position;
    private final ParticleData particleData;
    private final int amount;
    private final EmitterMode mode;
    private final ParticleShape shape;

    private final HashMap<String, Double> variables = new HashMap<>();
    private final long creationTime = System.currentTimeMillis();
    private long lastExecution = System.currentTimeMillis();

    public Emitter(Instance instance, Vec position, ParticleData particleData, int amount, EmitterMode mode, ParticleShape shape) {
        this.instance = instance;
        this.position = position;
        this.particleData = particleData;
        this.amount = amount;
        this.mode = mode;
        this.shape = shape;

    }


    public void emit() {
        variables.put("time", (System.currentTimeMillis() - creationTime) / 1000.0);
        for (int i = 0; i < amount; i++) {
            Vec offset = shape.getOffset(this);
            Vec particlePosition = shape.randomPositionInShape(this).add(position);
            Particle particle = new Particle(particleData, particlePosition);
            particle.getVariables().put("emitterX", position.x());
            particle.getVariables().put("emitterY", position.y());
            particle.getVariables().put("emitterZ", position.z());

            particle.getVariables().put("offsetX", offset.x());
            particle.getVariables().put("offsetY", offset.y());
            particle.getVariables().put("offsetZ", offset.z());

            particle.play(Objects.requireNonNull(instance.getChunkAt(position)).getViewersAsAudience(), this );
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
        if (lastExecution + mode.getDelay() < System.currentTimeMillis()) {
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
        return (mode.getType() == EmitterType.LOOPING && System.currentTimeMillis() > mode.getLifeTime());
    }

}
