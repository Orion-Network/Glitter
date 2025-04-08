package demo.utils;

import fr.mewtrpg.emitter.EmitterData;
import fr.mewtrpg.emitter.EmitterMode;
import fr.mewtrpg.emitter.EmitterType;
import fr.mewtrpg.emitter.shape.BoxShape;
import fr.mewtrpg.emitter.shape.EmmiterShape;
import fr.mewtrpg.emitter.shape.PointShape;
import fr.mewtrpg.particle.ParticleData;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.motion.*;
import fr.mewtrpg.utils.FormulaVariable;
import fr.mewtrpg.utils.FormulaVec;
import fr.mewtrpg.utils.SerializableExpression;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.Material;

import java.util.HashMap;

public class Samples {
    public static HashMap<String, EmitterData> samples = new HashMap<>();

    static {
        addLoadingEmitter();
        addRainEmitter();
        addFireEmitter();
    }

    static void addLoadingEmitter() {
        ItemAppearance appearance = new ItemAppearance( 1, Material.BEACON, 0, ItemDisplayMeta.DisplayContext.HEAD);
        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        appearance.setSkyLight(15);
        appearance.setBlockLight(15);

        SimpleMotion simpleMotion = new SimpleMotion(SimpleMotion.MotionMode.DIRECTION, 0, new Vec(0, 0, 0), new Vec(0, 0, 0), new MotionScale(0, 2050));

        ParticleData particleData = new ParticleData(2300, appearance, simpleMotion);
        EmmiterShape shape = new PointShape();
        shape.setOffsetFormula(
                new FormulaVec(
                        new SerializableExpression("sin(time*3)*5", "time"),
                        new SerializableExpression("cos(time*3)*5", "time"),
                        new SerializableExpression("0")
                )
        );

        samples.put("loading", new EmitterData(
                particleData,
                50,
                new EmitterMode(EmitterType.LOOPING, 10*2050, 50),
                shape
        ));
    }

    public static void addRainEmitter() {
        ItemAppearance appearance = new ItemAppearance( 0.5, Material.WIND_CHARGE, 0, ItemDisplayMeta.DisplayContext.FIXED);
        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.FIXED);
        appearance.setSkyLight(15);
        appearance.setBlockLight(15);

        SimpleMotion simpleMotion = new SimpleMotion(SimpleMotion.MotionMode.DIRECTION, 1, new Vec(0, -1, 0), new Vec(0, 0, 0), new MotionScale(0, 0));

        ParticleData particleData = new ParticleData(2300, appearance, simpleMotion);
        EmmiterShape shape = new BoxShape(new Vec(100,100,100));
        shape.setOffsetFormula(
                new FormulaVec(
                        new SerializableExpression("sin(time)*5", "time"),
                        new SerializableExpression("sin(time)*5", "time"),
                        new SerializableExpression("sin(time)*5", "time")
                )
        );

        samples.put("rain", new EmitterData(
                particleData,
                50,
                new EmitterMode(EmitterType.LOOPING, 10*2050, 200),
                shape
        ));
    }

    public static void addFireEmitter() {
        ItemAppearance appearance = new ItemAppearance( 0.5, Material.FIRE_CHARGE, 0, ItemDisplayMeta.DisplayContext.FIXED);
        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
        appearance.setSkyLight(15);
        appearance.setBlockLight(15);

        FormulaMotionScale scale = new FormulaMotionScale(
                new FormulaVariable(new SerializableExpression("0")),
                new FormulaVariable(new SerializableExpression("0")),
                new FormulaVariable(new SerializableExpression("0"))
        );

        Motion motion = new FormulaMotion(
                new FormulaVec(
                        new SerializableExpression("cos(random(0,1)*3.14)"),
                        new SerializableExpression("1"),
                        new SerializableExpression("cos(random(0,1)*3.14)")
                ),
                new FormulaVec(
                        new SerializableExpression("0.1"),
                        new SerializableExpression("random(0.05,0.1)"),
                        new SerializableExpression("0.1")
                ),
                scale
        );
        ParticleData particleData = new ParticleData(2300, appearance, motion);
        EmmiterShape shape = new PointShape();

        samples.put("fire", new EmitterData(
                particleData,
                50,
                new EmitterMode(EmitterType.LOOPING, 10*2050, 200),
                shape
        ));
    }
}
