package fr.mewtrpg;

import fr.mewtrpg.emitter.ParticleShape;
import fr.mewtrpg.emitter.SphereShape;
import fr.mewtrpg.particle.ItemAppearance;
import fr.mewtrpg.particle.Motion;
import fr.mewtrpg.particle.ParticleData;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;

public class ParticleManager implements Runnable {
    static ArrayList<Emitter> emitters = new ArrayList<>();
    public static void displayParticle(Player player) {
        ItemAppearance appearance = new ItemAppearance( 1, Material.ACACIA_BOAT, 0);
        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);

        Motion motion = new Motion(
                Motion.MotionMode.OUTWARD,
                1, new Vec(0, 0, 0),
                new Motion.MotionScale(0, 0));
        motion.setDirection(new Vec(0, 1, 0));

        ParticleData particleData = new ParticleData(5*1000, appearance, motion);
        Emitter emitter = new Emitter(
                player.getInstance(),
                player.getPosition().asVec(),
                particleData, 100,
                new Emitter.EmitterMode(Emitter.EmitterType.ONCE, 1000, 0),
                new SphereShape(10)
        );
        emitter.run();
        emitters.add(emitter);
    }

    public static void start() {
        ParticleManager particleManager = new ParticleManager();
        MinecraftServer.getSchedulerManager().buildTask(particleManager)
                .repeat(TaskSchedule.tick(20))
                .schedule();
    }

    @Override
    public void run() {
        for (Emitter emitter : emitters) {
            emitter.tick(0);
        }
    }
}
