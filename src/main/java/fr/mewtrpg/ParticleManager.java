package fr.mewtrpg;

import fr.mewtrpg.emitter.ParticleShape;
import fr.mewtrpg.emitter.SphereShape;
import fr.mewtrpg.particle.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParticleManager implements Runnable {
    private static final ConcurrentLinkedQueue<Emitter> emitters = new ConcurrentLinkedQueue<>();
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean running = new AtomicBoolean(true);

    public static void displayParticle(Player player) {
        ItemAppearance appearance = new ItemAppearance( 1, Material.BEACON, 0, ItemDisplayMeta.DisplayContext.GROUND);
        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        appearance.setSkyLight(15);
        appearance.setBlockLight(15);


        VelocityFormula formula = (time, particle) -> {
            Expression expression = new ExpressionBuilder("5*time").variable("time").build().setVariable("time", time);
            return expression.evaluate();
        };

        Motion motion = new FormulaMotion(
                Motion.MotionMode.OUTWARD,
                new Vec(0, 0, 0),
                new Motion.MotionScale(0, 0), formula);

        ParticleData particleData = new ParticleData(1000, appearance, motion);
        Emitter emitter = new Emitter(
                player.getInstance(),
                player.getPosition().asVec(),
                particleData, 1000,
                new Emitter.EmitterMode(Emitter.EmitterType.LOOPING, 1000, 1000),
                new SphereShape(5)
        );
        emitter.emit();
        emitters.add(emitter);
    }

    public static void start() {
        // Démarrer le thread de traitement
        executor.submit(() -> {
            while (running.get()) {
                try {
                    // Traitement par lots pour éviter de bloquer trop longtemps
                    for (Emitter emitter : emitters) {
                        if (emitter.isDead()) {
                            emitter.destroy();
                            emitters.remove(emitter);
                            continue;
                        }
                        emitter.tick(0);
                    }
                    Thread.sleep(50); // 20 ticks par seconde (~50ms)
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
        for (Emitter emitter : emitters) { // Iteration thread-safe
            if (emitter.isDead()) {
                emitter.destroy();
                emitters.remove(emitter); // Safe avec CopyOnWriteArrayList
                continue;
            }
            emitter.tick(0);
        }
    }
}
