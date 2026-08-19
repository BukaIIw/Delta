package hydrogen.render.gl;

import static org.lwjgl.opengl.GL45.*;

public final class GlStateCache {
    private int program = -1;
    private int vertexArray = -1;
    private boolean blending;

    public void useProgram(int program) {
        if (this.program == program) return;
        glUseProgram(program);
        this.program = program;
    }

    public void bindVertexArray(int vertexArray) {
        if (this.vertexArray == vertexArray) return;
        glBindVertexArray(vertexArray);
        this.vertexArray = vertexArray;
    }

    public void blending(boolean enabled) {
        if (blending == enabled) return;
        if (enabled) glEnable(GL_BLEND); else glDisable(GL_BLEND);
        blending = enabled;
    }

    public void reset() {
        program = -1;
        vertexArray = -1;
        blending = false;
    }
}
