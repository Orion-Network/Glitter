package fr.mewtrpg;

import fr.mewtrpg.emitter.EmitterMode;
import fr.mewtrpg.emitter.EmitterType;
import fr.mewtrpg.emitter.shape.SphereShape;
import fr.mewtrpg.particle.*;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.FormulaMotion;
import fr.mewtrpg.particle.motion.FormulaMotionScale;
import fr.mewtrpg.particle.motion.Motion;
import fr.mewtrpg.utils.FormulaVariable;
import fr.mewtrpg.utils.FormulaVec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
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

    public static void displayParticle(Player player) {
        ItemAppearance appearance = new ItemAppearance( 1, Material.BEACON, 0, ItemDisplayMeta.DisplayContext.GROUND);
        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        appearance.setSkyLight(15);
        appearance.setBlockLight(15);

        // Make direction going up in spiral
        FormulaVec directionFormula = new FormulaVec(
                new ExpressionBuilder("1")
                        .variable("time")
                        .build(),
                new ExpressionBuilder("1")
                        .variable("time")
                        .build(),
                new ExpressionBuilder("1")
                        .build()
        );

        FormulaVec velocityFormula = new FormulaVec(
                new ExpressionBuilder("1")
                        .build(),
                new ExpressionBuilder("1")
                        .build(),
                new ExpressionBuilder("1")
                        .build()
        );

        FormulaMotionScale scale = new FormulaMotionScale(
                new FormulaVariable(new ExpressionBuilder("cos(time)")
                        .variable("time")
                        .build()),
                new FormulaVariable(new ExpressionBuilder("0")
                        .build()),
                new FormulaVariable(new ExpressionBuilder("0")
                        .build())
        );

        Motion motion = new FormulaMotion(directionFormula, velocityFormula, scale);

        ParticleData particleData = new ParticleData(300, appearance, motion);
        SphereShape shape = new SphereShape(3);
        shape.setOffsetFormula(
                new FormulaVec(
                        new ExpressionBuilder("sin(time)*5")
                                .variable("time")
                                .build(),
                        new ExpressionBuilder("cos(time)*5")
                                .variable("time")
                                .build(),
                        new ExpressionBuilder("0")
                                .build()
                )
        );
        Emitter emitter = new Emitter(
                player.getInstance(),
                player.getPosition().asVec(),
                particleData, 100,
                new EmitterMode(EmitterType.LOOPING, 10000, 100),
                shape
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
