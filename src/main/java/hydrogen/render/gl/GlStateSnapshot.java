package hydrogen.render.gl;

import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/** Captures only state touched by Hydrogen and restores it at frame end. */
public final class GlStateSnapshot {
    private int program;
    private int vertexArray;
    private int arrayBuffer;
    private int activeTexture;
    private int texture0;
    private int blendSourceRgb;
    private int blendDestinationRgb;
    private int blendSourceAlpha;
    private int blendDestinationAlpha;
    private int blendEquationRgb;
    private int blendEquationAlpha;
    private int unpackAlignment;
    private int viewportX;
    private int viewportY;
    private int viewportWidth;
    private int viewportHeight;
    private boolean colorRed;
    private boolean colorGreen;
    private boolean colorBlue;
    private boolean colorAlpha;
    private boolean blend;
    private boolean depth;
    private boolean cull;
    private boolean scissor;
    private int scissorX;
    private int scissorY;
    private int scissorWidth;
    private int scissorHeight;

    public void capture() {
        program = glGetInteger(GL_CURRENT_PROGRAM);
        vertexArray = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        arrayBuffer = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        activeTexture = glGetInteger(GL_ACTIVE_TEXTURE);
        try {
            glActiveTexture(GL_TEXTURE0);
            texture0 = glGetInteger(GL_TEXTURE_BINDING_2D);
        } finally {
            glActiveTexture(activeTexture);
        }

        blend = glIsEnabled(GL_BLEND);
        depth = glIsEnabled(GL_DEPTH_TEST);
        cull = glIsEnabled(GL_CULL_FACE);
        scissor = glIsEnabled(GL_SCISSOR_TEST);
        blendSourceRgb = glGetInteger(GL_BLEND_SRC_RGB);
        blendDestinationRgb = glGetInteger(GL_BLEND_DST_RGB);
        blendSourceAlpha = glGetInteger(GL_BLEND_SRC_ALPHA);
        blendDestinationAlpha = glGetInteger(GL_BLEND_DST_ALPHA);
        blendEquationRgb = glGetInteger(GL_BLEND_EQUATION_RGB);
        blendEquationAlpha = glGetInteger(GL_BLEND_EQUATION_ALPHA);
        unpackAlignment = glGetInteger(GL_UNPACK_ALIGNMENT);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer box = stack.mallocInt(4);
            glGetIntegerv(GL_SCISSOR_BOX, box);
            scissorX = box.get(0);
            scissorY = box.get(1);
            scissorWidth = box.get(2);
            scissorHeight = box.get(3);

            IntBuffer viewport = stack.mallocInt(4);
            glGetIntegerv(GL_VIEWPORT, viewport);
            viewportX = viewport.get(0);
            viewportY = viewport.get(1);
            viewportWidth = viewport.get(2);
            viewportHeight = viewport.get(3);

            ByteBuffer colorMask = stack.malloc(4);
            glGetBooleanv(GL_COLOR_WRITEMASK, colorMask);
            colorRed = colorMask.get(0) != 0;
            colorGreen = colorMask.get(1) != 0;
            colorBlue = colorMask.get(2) != 0;
            colorAlpha = colorMask.get(3) != 0;
        }
    }

    public void restore() {
        glUseProgram(program);
        glBindVertexArray(vertexArray);
        glBindBuffer(GL_ARRAY_BUFFER, arrayBuffer);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, texture0);
        glActiveTexture(activeTexture);
        set(GL_BLEND, blend);
        set(GL_DEPTH_TEST, depth);
        set(GL_CULL_FACE, cull);
        set(GL_SCISSOR_TEST, scissor);
        glBlendFuncSeparate(blendSourceRgb, blendDestinationRgb, blendSourceAlpha, blendDestinationAlpha);
        glBlendEquationSeparate(blendEquationRgb, blendEquationAlpha);
        glPixelStorei(GL_UNPACK_ALIGNMENT, unpackAlignment);
        glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
        glColorMask(colorRed, colorGreen, colorBlue, colorAlpha);
        glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }

    private static void set(int capability, boolean enabled) {
        if (enabled) glEnable(capability); else glDisable(capability);
    }
}
