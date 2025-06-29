package fr.mewtrpg.particle.appearance;

import fr.mewtrpg.Particle;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;

public class TextAppearance extends Appearance {
    Component text;
    public TextAppearance(double size, Component component) {
        super(size);
        this.text = component;
    }

    @Override
    public void apply(Particle particle) {
        particle.switchEntityType(EntityType.TEXT_DISPLAY);
        TextDisplayMeta textDisplayMeta = (TextDisplayMeta) particle.getEntityMeta();
        textDisplayMeta.setText(text);

        super.apply(particle);
    }
}
