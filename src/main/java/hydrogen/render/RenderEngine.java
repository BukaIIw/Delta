package hydrogen.render;

import hydrogen.render.gl.GlStateCache;
import hydrogen.render.gl.GlStateSnapshot;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

/**
 * Standalone OpenGL UI renderer. Minecraft provides the window/context only;
 * every shader, texture, vertex layout, batch and state transition below is
 * owned by Hydrogen.
 */
public final class RenderEngine implements AutoCloseable {
    private final GlStateCache stateCache = new GlStateCache();
    private final GlStateSnapshot stateSnapshot = new GlStateSnapshot();
    private final RenderStats stats = new RenderStats();
    private Renderer2D renderer;
    private Thread renderThread;
    private boolean initialized;
    private boolean inFrame;
    private long frameStarted;

    /** Initialization is intentionally lazy so an active OpenGL context exists. */
    public void init() {
        // Kept as a lifecycle API. GPU initialization occurs at beginFrame.
    }

    public Renderer2D beginFrame(int logicalWidth, int logicalHeight, int framebufferWidth, int framebufferHeight) {
        if (inFrame) throw new IllegalStateException("A Hydrogen render frame is already active");
        assertRenderThread();
        boolean stateCaptured = false;
        try {
            stateSnapshot.capture();
            stateCaptured = true;
            initializeGpuObjects();
            stateCache.reset();
            stats.reset();
            frameStarted = System.nanoTime();
            renderer.begin(logicalWidth, logicalHeight, framebufferWidth, framebufferHeight);
            inFrame = true;
            return renderer;
        } catch (RuntimeException | Error error) {
            if (stateCaptured) {
                try {
                    stateSnapshot.restore();
                } catch (RuntimeException | Error restoreError) {
                    error.addSuppressed(restoreError);
                }
            }
            stateCache.reset();
            throw error;
        }
    }

    /** Compatibility overload for integrations that render in framebuffer pixels. */
    public void beginFrame() {
        beginFrame(1, 1, 1, 1);
    }

    public void endFrame() {
        if (!inFrame) throw new IllegalStateException("No Hydrogen render frame is active");
        try {
            renderer.end();
            stats.finish(frameStarted);
        } finally {
            inFrame = false;
            try {
                stateSnapshot.restore();
            } finally {
                stateCache.reset();
            }
        }
    }

    private void initializeGpuObjects() {
        if (initialized) return;
        GLCapabilities capabilities = GL.getCapabilities();
        if (!capabilities.OpenGL32 || (!capabilities.OpenGL33 && !capabilities.GL_ARB_instanced_arrays)) {
            throw new IllegalStateException("Hydrogen requires OpenGL 3.2 with instanced-array support");
        }
        renderer = new Renderer2D(stateCache, stats);
        initialized = true;
    }

    private void assertRenderThread() {
        Thread current = Thread.currentThread();
        if (renderThread == null) {
            renderThread = current;
        } else if (renderThread != current) {
            throw new IllegalStateException("RenderEngine accessed from a non-render thread");
        }
    }

    public GlStateCache stateCache() { return stateCache; }
    public RenderStats stats() { return stats; }
    public boolean initialized() { return initialized; }

    @Override
    public void close() {
        if (!initialized) return;
        assertRenderThread();
        if (inFrame) throw new IllegalStateException("Cannot close RenderEngine during a frame");
        try {
            renderer.close();
        } finally {
            renderer = null;
            initialized = false;
            renderThread = null;
            stateCache.reset();
        }
    }
}
