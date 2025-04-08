package fr.mewtrpg;

import fr.mewtrpg.emitter.EmitterMode;
import fr.mewtrpg.emitter.EmitterType;
import fr.mewtrpg.emitter.shape.ParticleShape;
import fr.mewtrpg.emitter.shape.PointShape;
import fr.mewtrpg.emitter.shape.SphereShape;
import fr.mewtrpg.particle.*;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.FormulaMotion;
import fr.mewtrpg.particle.motion.FormulaMotionScale;
import fr.mewtrpg.particle.motion.Motion;
import fr.mewtrpg.utils.FormulaVariable;
import fr.mewtrpg.utils.FormulaVec;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.Material;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;


public class ParticleManager implements Runnable {
    private static final ConcurrentLinkedQueue<Emitter> emitters = new ConcurrentLinkedQueue<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void spawnEmitter(Emitter emitter) {
        emitter.emit();
        emitters.add(emitter);
    }

    public static void spawnEmitter(Instance instance, Vec position, ParticleData particleData, int amount, EmitterMode mode, ParticleShape shape) {
        Emitter emitter = new Emitter(
                instance,
                position,
                particleData, amount,
                mode,
                shape
        );
        spawnEmitter(emitter);
    }

    public static void spawnParticle(Particle particle, Instance instance) {
        Emitter emitter = new Emitter(
                instance,
                particle.getParticlePosition(),
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
