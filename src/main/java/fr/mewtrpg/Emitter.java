package fr.mewtrpg;

import fr.mewtrpg.emitter.ParticleShape;
import fr.mewtrpg.particle.ParticleData;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.Audiences;
import net.minestom.server.MinecraftServer;
import net.minestom.server.Tickable;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.PacketUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public final class Emitter implements Tickable {

    private final List<Particle> particles = new CopyOnWriteArrayList<>();

    private final Instance instance;
    private final Vec position;
    private final ParticleData particleData;
    private final int amount;
    private final EmitterMode mode;
    private final ParticleShape shape;

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
        for (int i = 0; i < amount; i++) {
            Particle particle = new Particle(particleData);
            particle.play(Objects.requireNonNull(instance.getChunkAt(position)).getViewersAsAudience(), this, shape.randomPositionInShape().add(position));
            particles.add(particle);
        }
    }

    public void destroy() {
        for (Particle particle : particles) {
            PacketUtils.sendPacket(particle.getAudience(), new DestroyEntitiesPacket(particle.getEntityId()));
        }
        particles.clear();
    }

    @Override
    public void tick(long l) {
        if(lastExecution + mode.delay < System.currentTimeMillis()) {
            emit();
            lastExecution = System.currentTimeMillis();
        }
        for (Particle particle : particles) {
            if (System.currentTimeMillis() > particle.getLifeTime()) {
                PacketUtils.sendPacket(particle.getAudience(), new DestroyEntitiesPacket(particle.getEntityId()));
                particles.remove(particle); // Safe avec CopyOnWriteArrayList
            } else {
                particle.tick(l);
            }
        }
    }

    public boolean isDead() {
        return (mode.type == EmitterType.LOOPING && System.currentTimeMillis() > mode.lifeTime);
    }

    public static class EmitterMode {
        final EmitterType type;
        long lifeTime = 0;
        long delay = 0;

        public EmitterMode(EmitterType type) {
            this.type = type;
        }

        public EmitterMode(EmitterType type, long lifeTime, long delay) {
            this.type = type;
            this.lifeTime = System.currentTimeMillis() + lifeTime;
            this.delay = delay;
        }
    }

    public enum EmitterType {
        ONCE, LOOPING
    }
}
