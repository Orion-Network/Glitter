import com.google.gson.Gson;
import fr.mewtrpg.json.ParticleGson;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.*;
import fr.mewtrpg.utils.formula.FormulaVariable;
import fr.mewtrpg.utils.formula.FormulaVec;
import fr.mewtrpg.utils.SerializableExpression;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.Material;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParticleSerializationTest {
    Gson gson = ParticleGson.GSON;
    @Test
    public void testSimpleParticleSerialization() {
        ItemAppearance appearance = new ItemAppearance(1, Material.OAK_BOAT, "minecraft:beacon", ItemDisplayMeta.DisplayContext.GROUND);
        SimpleMotion motion = new SimpleMotion(
                SimpleMotion.MotionMode.OUTWARD,
                1, new Vec(0,0,0), new Vec(0, 0, 0),
                new MotionScale(0, 0));
        ParticleData particleData = new ParticleData(1000, appearance, motion);

        String json = gson.toJson(particleData);
        ParticleData deserializedParticleData = gson.fromJson(json, ParticleData.class);
        String deserializedJson = gson.toJson(deserializedParticleData);
        assertEquals(json, deserializedJson, "Serialized and deserialized JSON should be equal");
    }

    @Test
    public void testFormulaParticleSerialization() {
        ItemAppearance appearance = new ItemAppearance( 1, Material.BEACON, "minecraft:beacon", ItemDisplayMeta.DisplayContext.GROUND);
        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        appearance.setSkyLight(15);
        appearance.setBlockLight(15);

        FormulaVec directionFormula = new FormulaVec(
                new SerializableExpression("particleX-(emitterX+offsetX)", "particleX", "emitterX", "offsetX"),
                new SerializableExpression("particleY-(emitterY+offsetY)", "particleY", "emitterY", "offsetY"),
                new SerializableExpression("particleZ-(emitterZ+offsetZ)", "particleZ", "emitterZ", "offsetZ")
        );

        FormulaVec velocityFormula = new FormulaVec(
                new SerializableExpression("cos(time)", "time"),
                new SerializableExpression("sin(time)", "time"),
                new SerializableExpression("0")
        );

        FormulaMotionScale scale = new FormulaMotionScale(
                new FormulaVariable(new SerializableExpression("cos(time)", "time")),
                new FormulaVariable(new SerializableExpression("0")),
                new FormulaVariable(new SerializableExpression("0"))
        );

        Motion motion = new FormulaMotion(directionFormula, velocityFormula, scale);

        ParticleData particleData = new ParticleData(300, appearance, motion);

        String json = gson.toJson(particleData);
        ParticleData deserializedParticleData = gson.fromJson(json, ParticleData.class);
        String deserializedJson = gson.toJson(deserializedParticleData);
        assertEquals(json, deserializedJson, "Serialized and deserialized JSON should be equal");


    }
}
