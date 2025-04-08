package fr.mewtrpg.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.mewtrpg.emitter.shape.EmmiterShape;
import fr.mewtrpg.json.adapters.*;
import fr.mewtrpg.particle.appearance.Appearance;
import fr.mewtrpg.particle.motion.Motion;

public class ParticleGson {
    public static final Gson GSON;

    static {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Motion.class, new MotionAdapter());
        builder.registerTypeAdapter(Appearance.class, new AppearanceAdapter());
        builder.registerTypeAdapter(EmmiterShape.class, new ParticleShapeAdapter());
        GSON = builder.create();
    }
}
