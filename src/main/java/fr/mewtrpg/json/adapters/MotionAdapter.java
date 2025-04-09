package fr.mewtrpg.json.adapters;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import fr.mewtrpg.particle.motion.*;
import fr.mewtrpg.utils.formula.FormulaVec;
import net.minestom.server.coordinate.Vec;

public class MotionAdapter implements JsonSerializer<Motion>, JsonDeserializer<Motion> {
    @Override
    public Motion deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        Motion motion;
        switch (type) {
            case "SimpleMotion" -> motion = new SimpleMotion(
                    context.deserialize(jsonObject.get("mode"), SimpleMotion.MotionMode.class),
                    jsonObject.get("speed").getAsDouble(),
                    context.deserialize(jsonObject.get("direction"), Vec.class),
                    context.deserialize(jsonObject.get("acceleration"), Vec.class),
                    context.deserialize(jsonObject.get("scale"), MotionScale.class)
            );
            case "FormulaMotion" -> motion = new FormulaMotion(
                    context.deserialize(jsonObject.get("direction"), FormulaVec.class),
                    context.deserialize(jsonObject.get("velocity"), FormulaVec.class),
                    context.deserialize(jsonObject.get("scale"), FormulaMotionScale.class)
            );
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        return motion;
    }

    @Override
    public com.google.gson.JsonElement serialize(Motion src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getSimpleName());
        jsonObject.add("variables", context.serialize(src.getVariables()));
        switch (src.getClass().getSimpleName()) {
            case "SimpleMotion" -> {
                SimpleMotion simpleMotion = (SimpleMotion) src;
                jsonObject.add("mode", context.serialize(simpleMotion.getMode()));
                jsonObject.add("speed", context.serialize(simpleMotion.getSpeed()));
                jsonObject.add("direction", context.serialize(simpleMotion.getDirection()));
                jsonObject.add("acceleration", context.serialize(simpleMotion.getAcceleration()));
                jsonObject.add("scale", context.serialize(simpleMotion.getScale()));
            }
            case "FormulaMotion" -> {
                FormulaMotion formulaMotion = (FormulaMotion) src;
                jsonObject.add("direction", context.serialize(formulaMotion.getDirectionFormula()));
                jsonObject.add("velocity", context.serialize(formulaMotion.getVelocityFormula()));
                jsonObject.add("scale", context.serialize(formulaMotion.getScale()));
            }
        }
        return jsonObject;
    }
}
