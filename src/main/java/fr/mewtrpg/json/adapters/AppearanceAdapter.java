package fr.mewtrpg.json.adapters;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import fr.mewtrpg.particle.appearance.Appearance;
import fr.mewtrpg.particle.appearance.ItemAppearance;
import fr.mewtrpg.particle.appearance.TextAppearance;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.Material;

public class AppearanceAdapter implements JsonSerializer<Appearance>, JsonDeserializer<Appearance> {
    @Override
    public Appearance deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        double size = jsonObject.get("size").getAsDouble();
        Appearance appearance;

        switch (type) {
            case "ItemAppearance" -> {
                Material material = Material.fromNamespaceId(jsonObject.get("material").getAsString());
                String modelId = jsonObject.get("modelId").getAsString();
                ItemDisplayMeta.DisplayContext displayContext = ItemDisplayMeta.DisplayContext.valueOf(jsonObject.get("displayContext").getAsString());
                appearance = new ItemAppearance(size, material, modelId, displayContext);
            }
            // Add other appearance types here
            default -> throw new IllegalArgumentException("Unknown appearance type: " + type);
        }

        appearance.setBillboardConstraints(AbstractDisplayMeta.BillboardConstraints.valueOf(jsonObject.get("billboardConstraints").getAsString()));
        appearance.setHasPhysics(jsonObject.get("hasPhysics").getAsBoolean());
        appearance.setEmissive(jsonObject.get("emissive").getAsBoolean());
        appearance.setSkyLight(jsonObject.get("skyLight").getAsInt());
        appearance.setBlockLight(jsonObject.get("blockLight").getAsInt());
        appearance.setViewRange(jsonObject.get("viewRange").getAsFloat());
        appearance.setHasGlow(jsonObject.get("hasGlow").getAsBoolean());
        appearance.setGlowColor(jsonObject.get("glowColor").getAsInt());

        return appearance;
    }

    @Override
    public com.google.gson.JsonElement serialize(Appearance src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getSimpleName());
        jsonObject.addProperty("size", src.getSize());
        jsonObject.addProperty("billboardConstraints", src.getBillboardConstraints().name());
        jsonObject.addProperty("hasPhysics", src.isHasPhysics());
        jsonObject.addProperty("emissive", src.isEmissive());
        jsonObject.addProperty("skyLight", src.getSkyLight());
        jsonObject.addProperty("blockLight", src.getBlockLight());
        jsonObject.addProperty("viewRange", src.getViewRange());
        jsonObject.addProperty("hasGlow", src.isHasGlow());
        jsonObject.addProperty("glowColor", src.getGlowColor());
        switch (src.getClass().getSimpleName()) {
            case "ItemAppearance" -> {
                ItemAppearance itemAppearance = (ItemAppearance) src;
                jsonObject.addProperty("material", itemAppearance.getMaterial().namespace().asString());
                jsonObject.addProperty("modelId", itemAppearance.getModelId());
                jsonObject.addProperty("displayContext", itemAppearance.getDisplayContext().name());
            }
            case "TextAppearance" -> {
                TextAppearance textAppearance = (TextAppearance) src;
                jsonObject.add("text", context.serialize(textAppearance.getText()));
            }
        }

        return jsonObject;
    }
}
