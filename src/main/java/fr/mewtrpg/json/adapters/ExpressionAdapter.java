package fr.mewtrpg.json.adapters;

import com.google.gson.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.lang.reflect.Type;

public class ExpressionAdapter implements JsonSerializer<Expression>, JsonDeserializer<Expression> {

    @Override
    public Expression deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String expressionString = jsonObject.get("expression").getAsString();
        return new ExpressionBuilder(expressionString)
                .variables(jsonObject.get("variables").getAsString().split(","))
                .build();
    }

    @Override
    public JsonElement serialize(Expression expression, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("expression", expression.toString());
        jsonObject.addProperty("variables", expression.getVariableNames().toString());
        return jsonObject;
    }
}
