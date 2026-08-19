package hydrogen.render;

import hydrogen.render.gl.GlStateCache;
import org.lwjgl.opengl.GL;

public final class RenderEngine {
    private final GlStateCache stateCache = new GlStateCache();
    private boolean initialized;

    public void init() {
        if (initialized) return;
        GL.createCapabilities();
        initialized = true;
    }

    public void beginFrame() {
        ensureInitialized();
        stateCache.reset();
    }

    public void endFrame() {
        ensureInitialized();
    }

    public GlStateCache stateCache() {
        return stateCache;
    }

    public boolean initialized() {
        return initialized;
    }

    private void ensureInitialized() {
        if (!initialized) throw new IllegalStateException("RenderEngine is not initialized");
    }
}
