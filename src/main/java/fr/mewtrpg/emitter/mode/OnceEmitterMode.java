package fr.mewtrpg.emitter.mode;

public class OnceEmitterMode implements EmitterMode {

    @Override
    public long lifeTime() {
        return 0;
    }

    @Override
    public long delay() {
        return Long.MAX_VALUE;
    }
}
