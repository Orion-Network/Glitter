package fr.mewtrpg.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.mewtrpg.json.adapters.*;
import fr.mewtrpg.particle.appearance.Appearance;
import fr.mewtrpg.particle.motion.Motion;

public class ParticleGson {
    public  final Gson GSON;

    public ParticleGson() {

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Motion.class, new MotionAdapter());
        builder.registerTypeAdapter(Appearance.class, new AppearanceAdapter());
        GSON = builder.create();
    }
}
