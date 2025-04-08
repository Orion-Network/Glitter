package fr.mewtrpg.json.adapters;

import com.google.gson.*;
import fr.mewtrpg.emitter.shape.BoxShape;
import fr.mewtrpg.emitter.shape.ParticleShape;
import fr.mewtrpg.emitter.shape.PointShape;
import fr.mewtrpg.emitter.shape.SphereShape;
import net.minestom.server.coordinate.Vec;

import java.lang.reflect.Type;

public class ParticleShapeAdapter implements JsonSerializer<ParticleShape>, JsonDeserializer<ParticleShape> {
    @Override
    public ParticleShape deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();

        return switch (type) {
            case "SphereShape" -> {
                double radius = jsonObject.get("radius").getAsDouble();
                yield new SphereShape(radius);
            }
            case "BoxShape" -> {
                Vec size = context.deserialize(jsonObject.get("size"), Vec.class);
                yield new BoxShape(size);
            }
            case "PointShape" -> new PointShape();
            default -> throw new JsonParseException("Unknown particle shape type: " + type);
        };
    }

    @Override
    public JsonElement serialize(ParticleShape src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getSimpleName());
        switch (src.getClass().getSimpleName()) {
            case "SphereShape" -> jsonObject.add("radius", context.serialize(((SphereShape) src).getRadius()));
            case "BoxShape" -> {
                BoxShape boxShape = (BoxShape) src;
                jsonObject.add("size", context.serialize(boxShape.getSize()));
            }
            case "PointShape" -> {
                // Handle PointShape serialization if needed
            }
        }
        return jsonObject;
    }
}
