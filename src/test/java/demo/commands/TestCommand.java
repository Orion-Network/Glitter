package demo.commands;

import demo.utils.Samples;
import fr.mewtrpg.*;
import fr.mewtrpg.emitter.EmitterData;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.*;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.utils.FormulaVariable;
import fr.mewtrpg.utils.FormulaVec;
import fr.mewtrpg.utils.SerializableExpression;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.EntityPositionPacket;
import net.minestom.server.network.packet.server.play.SpawnEntityPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TestCommand extends Command {

        public TestCommand() {
            super("test");
            addSubcommand(new Command("emitter") {
                {
                    setDefaultExecutor((sender, context) -> {
                        sender.sendMessage("Emitter command executed!");
                    });

                    var emitterArg = new ArgumentWord("emitter_arg");
                    emitterArg.setDefaultValue("loading");
                    emitterArg.setSuggestionCallback((sender, context, suggestion) -> {
                        for(String key : Samples.samples.keySet()) {
                            suggestion.addEntry(new SuggestionEntry(key));
                        }
                    });

                    addSyntax((sender, context) -> {
                        if (sender instanceof Player player) {
                            String emitterName = context.get(emitterArg);
                            EmitterData emitterData = Samples.samples.get(emitterName);
                            if (emitterData != null) {
                                Emitter emitter = new Emitter(player.getInstance(), player.getPosition().asVec(), emitterData);
                                ParticleManager.spawnEmitter(emitter);
                                //TODO if we add the emitter multiple all will end at same time
                            } else {
                                sender.sendMessage("Emitter not found: " + emitterName);
                            }
                        } else {
                            sender.sendMessage("This command can only be used by players.");
                        }
                    }, emitterArg);
                }
            });
        }
}
