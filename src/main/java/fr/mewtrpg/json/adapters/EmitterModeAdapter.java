package fr.mewtrpg.json.adapters;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import fr.mewtrpg.emitter.mode.EmitterMode;
import fr.mewtrpg.emitter.mode.LoopingEmitterMode;
import fr.mewtrpg.emitter.mode.OnceEmitterMode;

public class EmitterModeAdapter implements JsonSerializer<EmitterMode>, JsonDeserializer<EmitterMode> {

    @Override
    public EmitterMode deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();

        switch (type) {
            case "OnceEmitterMode":
                return new OnceEmitterMode();
            case "LoopingEmitterMode":
                long lifeTime = jsonObject.get("lifeTime").getAsLong();
                long delay = jsonObject.get("delay").getAsLong();
                return new LoopingEmitterMode(lifeTime, delay);
            // Add cases for other EmitterMode implementations as needed
            default:
                throw new IllegalArgumentException("Unknown EmitterMode type: " + type);
        }
    }

    @Override
    public com.google.gson.JsonElement serialize(EmitterMode src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getSimpleName());
        switch (src.getClass().getSimpleName()) {
            case "OnceEmitterMode":
                break;
            case "LoopingEmitterMode":
                jsonObject.addProperty("lifeTime", src.lifeTime());
                jsonObject.addProperty("delay", src.delay());
                break;
            // Add cases for other EmitterMode implementations as needed
            default:
                throw new IllegalArgumentException("Unknown EmitterMode type: " + src.getClass().getSimpleName());
        }
        return jsonObject;
    }
}
