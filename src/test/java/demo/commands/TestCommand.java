package demo.commands;

import demo.utils.Samples;
import fr.mewtrpg.*;
import fr.mewtrpg.emitter.EmitterData;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentBoolean;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

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

                    ArgumentBoolean attachArg = new ArgumentBoolean("attach_arg");
                    attachArg.setDefaultValue(false);

                    ArgumentBoolean rotateArg = new ArgumentBoolean("rotate_arg");
                    rotateArg.setDefaultValue(false);

                    addSyntax((sender, context) -> {
                        if (sender instanceof Player player) {
                            String emitterName = context.get(emitterArg);
                            EmitterData emitterData = Samples.samples.get(emitterName);
                            if (emitterData != null) {
                                Emitter emitter = new Emitter(player.getInstance(), player.getPosition(), emitterData);
                                ParticleManager.spawnEmitter(emitter);
                                if(context.get(attachArg))
                                    emitter.setAttachedEntity(player);
                                if(context.get(rotateArg))
                                    emitter.setRotateAroundPosition(true);

                            } else {
                                sender.sendMessage("Emitter not found: " + emitterName);
                            }
                        } else {
                            sender.sendMessage("This command can only be used by players.");
                        }
                    }, emitterArg, attachArg, rotateArg);
                }
            });
        }
}
