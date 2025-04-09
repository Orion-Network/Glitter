package fr.mewtrpg;

import fr.mewtrpg.emitter.EmitterMode;
import fr.mewtrpg.emitter.EmitterType;
import fr.mewtrpg.emitter.shape.EmmiterShape;
import fr.mewtrpg.emitter.shape.SphereShape;
import fr.mewtrpg.particle.*;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class ParticleManager implements Runnable {
    private static final ConcurrentLinkedQueue<Emitter> emitters = new ConcurrentLinkedQueue<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void spawnEmitter(Emitter emitter) {
        if (!running.get()) {
            throw new IllegalStateException("ParticleManager is not running");
        }
        emitter.emit();
        emitters.add(emitter);
    }

    public static void spawnEmitter(Instance instance, Vec position, ParticleData particleData, int amount, EmitterMode mode, EmmiterShape shape) {
        Emitter emitter = new Emitter(
                instance,
                position.asPosition(),
                particleData, amount,
                mode,
                shape
        );
        spawnEmitter(emitter);
    }

    public static void spawnParticle(Particle particle, Instance instance) {
        Emitter emitter = new Emitter(
                instance,
                particle.getParticlePosition().asPosition(),
                particle.getParticleData(), 1,
                new EmitterMode(EmitterType.LOOPING, particle.getLifeTime()-System.currentTimeMillis(), particle.getLifeTime()-System.currentTimeMillis()+10),
                new SphereShape(0)
        );
        spawnEmitter(emitter);
    }

    public static void start() {
        executor.submit(() -> {
            while (running.get()) {
                try {
                    for (Emitter emitter : emitters) {
                        if (emitter.isDead()) {
                            emitter.destroy();
                            emitters.remove(emitter);
                            continue;
                        }
                        emitter.tick(0);
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    public static void stop() {
        running.set(false);
        executor.shutdownNow();
        emitters.forEach(Emitter::destroy);
        emitters.clear();
    }

    @Override
    public void run() {
        for (Emitter emitter : emitters) {
            if (emitter.isDead()) {
                emitter.destroy();
                emitters.remove(emitter);
                continue;
            }
            emitter.tick(0);
        }
    }
}
