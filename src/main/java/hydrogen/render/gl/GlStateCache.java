package hydrogen.render.gl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Write-through state cache for hot render paths. Values start unknown every
 * frame because Hydrogen shares a context with the game, then redundant state
 * transitions are removed inside the custom frame.
 */
public final class GlStateCache {
    private int program = -1;
    private int vertexArray = -1;
    private int arrayBuffer = -1;
    private int texture = -1;
    private int activeTexture = -1;
    private int blend = -1;
    private int depthTest = -1;
    private int cull = -1;
    private int scissor = -1;

    public void useProgram(int value) {
        if (program == value) return;
        glUseProgram(value);
        program = value;
    }

    public void bindVertexArray(int value) {
        if (vertexArray == value) return;
        glBindVertexArray(value);
        vertexArray = value;
    }

    public void bindArrayBuffer(int value) {
        if (arrayBuffer == value) return;
        glBindBuffer(GL_ARRAY_BUFFER, value);
        arrayBuffer = value;
    }

    public void textureUnit0(int value) {
        if (activeTexture != GL_TEXTURE0) {
            glActiveTexture(GL_TEXTURE0);
            activeTexture = GL_TEXTURE0;
            texture = -1;
        }
        if (texture == value) return;
        glBindTexture(GL_TEXTURE_2D, value);
        texture = value;
    }

    public void blending(boolean enabled) {
        blend = capability(GL_BLEND, enabled, blend);
    }

    public void depthTest(boolean enabled) {
        depthTest = capability(GL_DEPTH_TEST, enabled, depthTest);
    }

    public void culling(boolean enabled) {
        cull = capability(GL_CULL_FACE, enabled, cull);
    }

    public void scissor(boolean enabled) {
        scissor = capability(GL_SCISSOR_TEST, enabled, scissor);
    }

    public void standardAlphaBlend() {
        glBlendEquationSeparate(GL_FUNC_ADD, GL_FUNC_ADD);
        glBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    }

    private static int capability(int capability, boolean enabled, int previous) {
        int next = enabled ? 1 : 0;
        if (previous == next) return previous;
        if (enabled) glEnable(capability); else glDisable(capability);
        return next;
    }

    public void reset() {
        program = -1;
        vertexArray = -1;
        arrayBuffer = -1;
        texture = -1;
        activeTexture = -1;
        blend = -1;
        depthTest = -1;
        cull = -1;
        scissor = -1;
    }
}
