package fr.mewtrpg.particle.appearance;

import fr.mewtrpg.Particle;
import lombok.Builder;
import lombok.Getter;
import net.kyori.adventure.util.RGBLike;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ItemAppearance extends Appearance {
    final Material material;
    final String modelId;
    final CustomModelData customModelData;
    final ItemDisplayMeta.DisplayContext displayContext;

    public ItemAppearance(double size, Material material, String modelId, CustomModelData customModelData, ItemDisplayMeta.DisplayContext displayContext) {
        super(size);
        this.material = material;
        this.modelId = modelId;
        this.customModelData = customModelData;
        this.displayContext = displayContext;
    }

    public ItemAppearance(double size, Material material, ItemDisplayMeta.DisplayContext context) {
        this(size, material, material.namespace().asString(), CustomModelData.builder().build(), context);
    }

    @Override
    public void apply(Particle particle) {
        particle.switchEntityType(EntityType.ITEM_DISPLAY);
        ItemDisplayMeta itemDisplayMeta = (ItemDisplayMeta) particle.getEntityMeta();
        itemDisplayMeta.setItemStack(ItemStack.of(material, 1).withItemModel(modelId).withCustomModelData(customModelData.floatList, customModelData.booleansList, customModelData.stringList, customModelData.colorsList));
        itemDisplayMeta.setDisplayContext(displayContext);

        super.apply(particle);
    }

    @Builder
    public static class CustomModelData {
        ArrayList<Float> floatList;
        ArrayList<Boolean> booleansList;
        ArrayList<String> stringList;
        ArrayList<RGBLike> colorsList;

    }
}
