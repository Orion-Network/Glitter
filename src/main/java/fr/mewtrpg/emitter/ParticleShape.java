package fr.mewtrpg.emitter;

import lombok.Getter;
import lombok.Setter;
import net.minestom.server.coordinate.Vec;

@Getter
@Setter
public abstract class ParticleShape {
    private Vec offset = new Vec(0, 0, 0);
    private boolean surface = false;

    public abstract Vec randomPositionInShape();
}
