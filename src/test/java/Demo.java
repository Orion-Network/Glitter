import fr.mewtrpg.ParticleManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.monitoring.TickMonitor;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.MathUtils;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

public class Demo {
    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        // Create the instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setChunkSupplier(LightingChunk::new);
        // Set the ChunkGenerator
        instanceContainer.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 42, 0));
            player.setGameMode(GameMode.CREATIVE);
        });

        AtomicReference<TickMonitor> lastTick = new AtomicReference<>();
        globalEventHandler.addListener(ServerTickMonitorEvent.class, event -> lastTick.set(event.getTickMonitor()));

        // Header/footer
        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
            if (players.isEmpty()) return;

            final Runtime runtime = Runtime.getRuntime();
            final TickMonitor tickMonitor = lastTick.get();
            final long ramUsage = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;

            final Component header = Component.newline()
                    .append(Component.text("RAM USAGE: " + ramUsage + " MB", NamedTextColor.GRAY).append(Component.newline())
                            .append(Component.text("TICK TIME: " + MathUtils.round(tickMonitor.getTickTime(), 2) + "ms", NamedTextColor.GRAY))).append(Component.newline());

            final Component footer = Component.newline()
                    .append(Component.text("          WorldSeed Entity Engine          ")
                            .color(TextColor.color(57, 200, 73))
                            .append(Component.newline()));

            Audiences.players().sendPlayerListHeaderAndFooter(header, footer);
        }, TaskSchedule.tick(10), TaskSchedule.tick(10));

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);

        MinecraftServer.getCommandManager().register(new commands.TestCommand());

        ParticleManager.start();
    }
}
