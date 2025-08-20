package fr.mewtrpg;

import fr.mewtrpg.emitter.EmitterData;
import fr.mewtrpg.emitter.mode.EmitterMode;
import fr.mewtrpg.emitter.mode.LoopingEmitterMode;
import fr.mewtrpg.emitter.shape.EmmiterShape;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.utils.VariablesHolder;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.minestom.server.Tickable;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.utils.PacketSendingUtils;

import java.util.*;
import java.util.concurrent.*;

@Getter
public final class Emitter implements Tickable, VariablesHolder {

    private final ConcurrentLinkedQueue<Particle> particles = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private final Instance instance;
    private final Pos position;
    @Setter
    private boolean rotateAroundPosition = false;
    @Setter
    private Entity attachedEntity;
    private final EmitterData emitterData;
    private final long lifeTime;

    private final UUID uuid = UUID.randomUUID();
    private final HashMap<String, Double> variables = new HashMap<>();
    private final long creationTime = System.currentTimeMillis();
    private long lastExecution = System.currentTimeMillis();

    public Emitter(Instance instance, Pos position, EmitterData emitterData) {
        this.instance = instance;
        this.position = position;
        this.emitterData = emitterData;
        if(emitterData.mode() instanceof LoopingEmitterMode)
            this.lifeTime = emitterData.mode().lifeTime() + System.currentTimeMillis();
        else
            this.lifeTime = emitterData.particleData().lifeTime() + System.currentTimeMillis();
    }

    public Emitter(Instance instance, Pos position, ParticleData particleData, int amount, EmitterMode mode, EmmiterShape shape) {
        this(instance, position, new EmitterData(particleData, amount, mode, shape));
    }


    public void emit() {
        variables.put("time", (System.currentTimeMillis() - creationTime) / 1000.0);
        for (int i = 0; i < emitterData.amount(); i++) {
            Pos offset = emitterData.shape().getOffset(this);
            Pos particlePosition = emitterData.shape().randomPositionInShape(this).add(getPosition());
            Particle particle = new Particle(emitterData.particleData(), particlePosition);
            if(rotateAroundPosition)
                particle.setParticlePosition(rotatePosition(particle.getParticlePosition(), getPosition(), getPosition().yaw(), getPosition().pitch()));

            particle.getVariables().put("emitterX", getPosition().x());
            particle.getVariables().put("emitterY", getPosition().y());
            particle.getVariables().put("emitterZ", getPosition().z());
            particle.getVariables().put("emitterYaw", (double) getPosition().yaw());
            particle.getVariables().put("emitterPitch", (double) getPosition().pitch());

            particle.getVariables().put("offsetX", offset.x());
            particle.getVariables().put("offsetY", offset.y());
            particle.getVariables().put("offsetZ", offset.z());

            Audience audience = Objects.requireNonNull(instance.getChunkAt(getPosition())).getViewersAsAudience();

            particle.play(audience, this );
            particles.add(particle);
        }
    }

    private Pos rotatePosition(Pos position, Pos center, double yaw, double pitch) {
        double cosYaw = Math.cos(Math.toRadians(yaw));
        double sinYaw = Math.sin(Math.toRadians(yaw));
        double cosPitch = Math.cos(Math.toRadians(pitch));
        double sinPitch = Math.sin(Math.toRadians(pitch));

        double x = position.x() - center.x();
        double y = position.y() - center.y();
        double z = position.z() - center.z();

        double newX = x * cosYaw - z * sinYaw;
        double newY = y * cosPitch - (x * sinYaw + z * cosYaw) * sinPitch;
        double newZ = x * sinYaw + z * cosYaw;

        return new Pos(newX + center.x(), newY + center.y(), newZ + center.z());
    }

    public void destroy() {
        scheduler.shutdownNow();
        particles.forEach(particle ->
                PacketSendingUtils.sendPacket(particle.getAudience(), new DestroyEntitiesPacket(particle.getEntityId())));
        particles.clear();
    }

    @Override
    public void tick(long l) {
        if (emitterData.mode().delay() < System.currentTimeMillis() - lastExecution) {
            emit();
            lastExecution = System.currentTimeMillis();
        }

        particles.removeIf(particle -> {
            if (System.currentTimeMillis() > particle.getLifeTime()) {
                PacketSendingUtils.sendPacket(particle.getAudience(), new DestroyEntitiesPacket(particle.getEntityId()));
                return true;
            }
            particle.tick(l);
            return false;
        });
    }

    public boolean isDead() {
        return (System.currentTimeMillis() > this.getLifeTime());
    }

    public Instance getInstance() {
        return (attachedEntity == null ? instance : attachedEntity.getInstance());
    }

    public Pos getPosition() {
        return (attachedEntity == null ? position : attachedEntity.getPosition());
    }
}
