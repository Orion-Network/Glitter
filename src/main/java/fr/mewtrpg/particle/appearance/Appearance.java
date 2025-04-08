package fr.mewtrpg.particle.appearance;

import fr.mewtrpg.Particle;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;

@Getter@Setter
public abstract class Appearance {
    private final double size;
    private AbstractDisplayMeta.BillboardConstraints billboardConstraints = AbstractDisplayMeta.BillboardConstraints.CENTER;
    private boolean hasPhysics = false;
    private boolean emissive = false;
    private int skyLight = 0, blockLight = 0;
    private float viewRange = 25;
    private boolean hasGlow = false;
    private int glowColor = 0xFFFFFF;

    public Appearance(double size) {
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

        displayMeta.setViewRange(viewRange);

        if(hasGlow) {
            displayMeta.setHasGlowingEffect(true);
            displayMeta.setGlowColorOverride(glowColor);
        }
    }
}
