package fr.mewtrpg.emitter;

import lombok.Getter;

@Getter
public class EmitterMode {
    final EmitterType type;
    long lifeTime = 0;
    long delay = 0;

    public EmitterMode(EmitterType type) {
        this.type = type;
    }

    public EmitterMode(EmitterType type, long lifeTime, long delay) {
        this.type = type;
        this.lifeTime = System.currentTimeMillis() + lifeTime;
        this.delay = delay;
    }
}
