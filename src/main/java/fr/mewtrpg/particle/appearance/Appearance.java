package fr.mewtrpg.particle.appearance;

import fr.mewtrpg.Particle;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;

@Getter@Setter
public class Appearance {
    private final Mode mode;
    private final double size;
    private AbstractDisplayMeta.BillboardConstraints billboardConstraints = AbstractDisplayMeta.BillboardConstraints.CENTER;
    private boolean hasPhysics = false;
    private boolean emissive = false;
    private int skyLight = 0, blockLight = 0;
    private float view_range = 25;

    public Appearance(Mode mode, double size) {
        this.mode = mode;
        this.size = size;
    }

    public void apply(Particle particle) {
        particle.setNoGravity(true);
        particle.setPhysics(hasPhysics);

        AbstractDisplayMeta displayMeta = (AbstractDisplayMeta) particle.getEntityMeta();
        displayMeta.setPosRotInterpolationDuration(5);
        displayMeta.setTransformationInterpolationDuration(50);
        displayMeta.setScale(new Vec(size, size, size));
        displayMeta.setBillboardRenderConstraints(billboardConstraints);
        if(emissive)
            displayMeta.setBrightnessOverride((0xF) << 4 | (0xF) << 20);
        displayMeta.setBrightnessOverride((blockLight & 0xF) << 4 | (skyLight & 0xF) << 20);

        displayMeta.setViewRange(view_range);
    }

    public enum Mode {
        BLOCK,
        ITEM,
        ENTITY,
        IMAGE
    }
}
