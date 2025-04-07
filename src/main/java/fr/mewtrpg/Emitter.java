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
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public final class Emitter implements Tickable {

    ArrayList<Particle> particles = new ArrayList<>();

    private final Instance instance;
    private final Vec position;
    private final ParticleData particleData;
    private final int amount;
    private final EmitterMode mode;
    private final ParticleShape shape;

    public Emitter(Instance instance, Vec position, ParticleData particleData, int amount, EmitterMode mode, ParticleShape shape) {
        this.instance = instance;
        this.position = position;
        this.particleData = particleData;
        this.amount = amount;
        this.mode = mode;
        this.shape = shape;
    }


    private void emit() {
        for (int i = 0; i < amount; i++) {
            Particle particle = new Particle(particleData);
            particle.play(Objects.requireNonNull(instance.getChunkAt(position)).getViewersAsAudience(), this, shape.randomPositionInShape().add(position));
            particles.add(particle);
        }
    }

    public void run() {
        if (mode.type == EmitterType.ONCE) {
            emit();
        }

        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (mode.type == EmitterType.LOOPING) {
                            emit();
                        }
                    }
                },
                0, 100
        );
    }

    @Override
    public void tick(long l) {
        for (Particle particle : particles) {
            particle.tick(l);
        }
    }

    public Instance instance() {
        return instance;
    }

    public Vec position() {
        return position;
    }

    public ParticleData particleData() {
        return particleData;
    }

    public int amount() {
        return amount;
    }

    public EmitterMode mode() {
        return mode;
    }

    public ParticleShape shape() {
        return shape;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Emitter) obj;
        return Objects.equals(this.instance, that.instance) &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.particleData, that.particleData) &&
                this.amount == that.amount &&
                Objects.equals(this.mode, that.mode) &&
                Objects.equals(this.shape, that.shape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, position, particleData, amount, mode, shape);
    }

    @Override
    public String toString() {
        return "Emitter[" +
                "instance=" + instance + ", " +
                "position=" + position + ", " +
                "particleData=" + particleData + ", " +
                "amount=" + amount + ", " +
                "mode=" + mode + ", " +
                "shape=" + shape + ']';
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
