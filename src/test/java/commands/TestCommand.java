package commands;

import fr.mewtrpg.Emitter;
import fr.mewtrpg.Particle;
import fr.mewtrpg.ParticleManager;
import fr.mewtrpg.emitter.SphereShape;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.Motion;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.particle.motion.MotionScale;
import fr.mewtrpg.particle.motion.SimpleMotion;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;

import java.util.UUID;

public class TestCommand extends Command {

        public TestCommand() {
            super("test");
            setDefaultExecutor((sender, context) -> {
                sender.sendMessage("Test command executed!");

                if (sender instanceof Player player) {
                    ItemAppearance appearance = new ItemAppearance( 1, Material.ACACIA_BOAT, 0, ItemDisplayMeta.DisplayContext.GROUND);
                    appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);

                    Motion motion = new SimpleMotion(
                            SimpleMotion.MotionMode.OUTWARD,
                            1, new Vec(0, 0, 0),
                            new MotionScale(0, 0));
                    motion.setDirection(new Vec(0, 1, 0));

                    ParticleData particleData = new ParticleData(5*1000, appearance, motion);
                    Particle COPY_ENTITY = new Particle(particleData);
                    int START_ENTITY_ID = COPY_ENTITY.getEntityId() + 1;
                    SpawnEntityPacket packet = (SpawnEntityPacket) COPY_ENTITY.getEntityType().registry().spawnType().getSpawnPacket(COPY_ENTITY);
                    EntityMetaDataPacket metadataPacket = COPY_ENTITY.getMetadataPacket();

                    Pos position = player.getPosition();
                    SpawnEntityPacket fakePacket = new SpawnEntityPacket(START_ENTITY_ID, UUID.randomUUID(), packet.type(),
                            position, packet.headRot(), packet.data(), (short) 0, (short) 0, (short) 0);

                    EntityMetaDataPacket fakeMetadataPacket = new EntityMetaDataPacket(START_ENTITY_ID, metadataPacket.entries());


                    player.sendPacket(fakePacket);
                    player.sendPacket(fakeMetadataPacket);
                    player.sendPacket(EntityPositionPacket.getPacket(START_ENTITY_ID, position.add(0,-10,0), position, false));
                }
            });

            addSubcommand(new Command("emitter") {
                {
                    setDefaultExecutor((sender, context) -> {
                        if ((sender instanceof Player player)) {
                            ParticleManager.displayParticle(player);
                        } else {
                            sender.sendMessage("This command can only be used by players.");
                        }
                    });
                }
            });

            addSubcommand(new Command("particle") {
                {
                    setDefaultExecutor((sender, context) -> {
                        if ((sender instanceof Player player)) {
                            ItemAppearance appearance = new ItemAppearance( 1, Material.ACACIA_BOAT, 0, ItemDisplayMeta.DisplayContext.GROUND);
                            appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);

                            Motion motion = new SimpleMotion(
                                    SimpleMotion.MotionMode.DIRECTION,
                                    1, new Vec(0, 0, 0),
                                    new MotionScale(0, 0));
                            motion.setDirection(new Vec(0, 10, 0));

                            ParticleData particleData = new ParticleData(5*1000, appearance, motion);
                            Particle particle = new Particle(particleData);

                            Emitter emitter = new Emitter(
                                    player.getInstance(),
                                    player.getPosition().asVec(),
                                    particleData, 100,
                                    new Emitter.EmitterMode(Emitter.EmitterType.ONCE, 1000, 0),
                                    new SphereShape(10)
                            );

                            particle.play(player, emitter, player.getPosition().asVec());
                            particle.tick(0);
                        } else {
                            sender.sendMessage("This command can only be used by players.");
                        }
                    });
                }
            });
        }
}
