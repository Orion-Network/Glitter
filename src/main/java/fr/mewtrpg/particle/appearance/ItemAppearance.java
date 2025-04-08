package fr.mewtrpg.particle.appearance;

import fr.mewtrpg.Particle;
import lombok.Getter;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.ItemDisplayMeta;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

@Getter
public class ItemAppearance extends Appearance {
    final Material material;
    final int customModelData;
    final ItemDisplayMeta.DisplayContext displayContext;

    public ItemAppearance(double size, Material material, int customModelData, ItemDisplayMeta.DisplayContext displayContext) {
        super(size);
        this.material = material;
        this.customModelData = customModelData;
        this.displayContext = displayContext;
    }

    @Override
    public void apply(Particle particle) {
        particle.switchEntityType(EntityType.ITEM_DISPLAY);
        ItemDisplayMeta itemDisplayMeta = (ItemDisplayMeta) particle.getEntityMeta();
        itemDisplayMeta.setItemStack(ItemStack.of(material, 1).withCustomModelData(customModelData));
        itemDisplayMeta.setDisplayContext(displayContext);

        super.apply(particle);
    }
}
