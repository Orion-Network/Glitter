import com.google.gson.Gson;
import fr.mewtrpg.emitter.EmitterData;
import fr.mewtrpg.emitter.mode.LoopingEmitterMode;
import fr.mewtrpg.emitter.mode.OnceEmitterMode;
import fr.mewtrpg.emitter.shape.SphereShape;
import fr.mewtrpg.json.ParticleGson;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.MotionScale;
import fr.mewtrpg.particle.motion.SimpleMotion;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.Material;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmitterSerializationTest {
    Gson gson = ParticleGson.GSON;

    @Test
    public void testEmitterModeSerialization() {
        LoopingEmitterMode loopingMode = new LoopingEmitterMode(1000, 2000);
        String json = gson.toJson(loopingMode);
        LoopingEmitterMode deserializedMode = gson.fromJson(json, LoopingEmitterMode.class);
        String deserializedJson = gson.toJson(deserializedMode);
        assertEquals(json, deserializedJson, "Serialized and deserialized JSON should be equal");

        OnceEmitterMode onceMode = new OnceEmitterMode();
        json = gson.toJson(onceMode);
        OnceEmitterMode deserializedOnceMode = gson.fromJson(json, OnceEmitterMode.class);
        String deserializedOnceJson = gson.toJson(deserializedOnceMode);
        assertEquals(json, deserializedOnceJson, "Serialized and deserialized JSON should be equal");
    }

    @Test
    public void testEmitterSerialization() {
        ItemAppearance appearance = new ItemAppearance(1, Material.OAK_BOAT, "minecraft:beacon", ItemDisplayMeta.DisplayContext.GROUND);
        SimpleMotion motion = new SimpleMotion(
                SimpleMotion.MotionMode.OUTWARD,
                1, new Vec(0,0,0), new Vec(0, 0, 0),
                new MotionScale(0, 0));
        ParticleData particleData = new ParticleData(1000, appearance, motion);

        EmitterData emitterData = new EmitterData(particleData, 10, new LoopingEmitterMode( 10, 10), new SphereShape(5));

        String json = gson.toJson(emitterData);
        EmitterData deserializedEmitterData = gson.fromJson(json, EmitterData.class);
        String deserializedJson = gson.toJson(deserializedEmitterData);
        assertEquals(json, deserializedJson, "Serialized and deserialized JSON should be equal");
    }
}
