package demo.commands;

import fr.mewtrpg.*;
import fr.mewtrpg.emitter.EmitterMode;
import fr.mewtrpg.emitter.EmitterType;
import fr.mewtrpg.emitter.shape.SphereShape;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.*;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.utils.FormulaVariable;
import fr.mewtrpg.utils.FormulaVec;
import fr.mewtrpg.utils.SerializableExpression;
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
                            1,new Vec(0, 1, 0), new Vec(0, 0, 0),
                            new MotionScale(0, 0));

                    ParticleData particleData = new ParticleData(5*1000, appearance, motion);
                    Particle COPY_ENTITY = new Particle(particleData, player.getPosition().asVec());
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
                    setDefaultExecutor((sender, _) -> {
                        if ((sender instanceof Player player)) {
                            ItemAppearance appearance = new ItemAppearance( 1, Material.NETHER_BRICKS, 0, ItemDisplayMeta.DisplayContext.GROUND);
                            appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
                            appearance.setSkyLight(15);
                            appearance.setBlockLight(0);

                            // Make direction going up in spiral
                            FormulaVec directionFormula = new FormulaVec(
                                    new SerializableExpression("particleX-(emitterX+offsetX)", "particleX", "emitterX", "offsetX"),
                                    new SerializableExpression("particleY-(emitterY+offsetY)", "particleY", "emitterY", "offsetY"),
                                    new SerializableExpression("particleZ-(emitterZ+offsetZ)", "particleZ", "emitterZ", "offsetZ")
                            );

                            FormulaVec velocityFormula = new FormulaVec(
                                    new SerializableExpression("1"),
                                    new SerializableExpression("1"),
                                    new SerializableExpression("1")
                            );

                            FormulaMotionScale scale = new FormulaMotionScale(
                                    new FormulaVariable(new SerializableExpression("cos(time)", "time")),
                                    new FormulaVariable(new SerializableExpression("0")),
                                    new FormulaVariable(new SerializableExpression("0"))
                            );

                            Motion motion = new FormulaMotion(directionFormula, velocityFormula, scale);

                            ParticleData particleData = new ParticleData(300, appearance, motion);
                            SphereShape shape = new SphereShape(3);
                            shape.setOffsetFormula(
                                    new FormulaVec(
                                            new SerializableExpression("sin(time)*5", "time"),
                                            new SerializableExpression("cos(time)*5", "time"),
                                            new SerializableExpression("0")
                                    )
                            );
                            Emitter emitter = new Emitter(
                                    player.getInstance(),
                                    player.getPosition().asVec(),
                                    particleData, 100,
                                    new EmitterMode(EmitterType.LOOPING, 10000, 50),
                                    shape
                            );
                            ParticleManager.spawnEmitter(emitter);
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
                                    1, new Vec(0, 10, 0), new Vec(0, 0, 0),
                                    new MotionScale(0, 0));

                            ParticleData particleData = new ParticleData(1000, appearance, motion);
                            Particle particle = new Particle(particleData, player.getPosition().asVec());

                            ParticleManager.spawnParticle(particle, player.getInstance());
                        } else {
                            sender.sendMessage("This command can only be used by players.");
                        }
                    });
                }
            });
        }
}
