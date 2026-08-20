package hydrogen.render.batch;

import hydrogen.render.RenderStats;
import hydrogen.render.gl.GlStateCache;
import hydrogen.render.gl.ShaderProgram;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.ARBInstancedArrays.glVertexAttribDivisorARB;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

/**
 * Instanced SDF rectangle batch. A static six-vertex quad is reused for every
 * shape and all visual parameters are streamed as compact instance data.
 */
public final class ShapeBatch implements AutoCloseable {
    private static final int INSTANCE_STRIDE = 72;
    private static final int INITIAL_INSTANCES = 512;

    private ShaderProgram shader;
    private int vertexArray;
    private final boolean coreInstanceDivisors;
    private int quadBuffer;
    private int instanceBuffer;
    private ByteBuffer instances;
    private int count;
    private float resolutionWidth;
    private float resolutionHeight;

    public ShapeBatch() {
        coreInstanceDivisors = GL.getCapabilities().OpenGL33;
        try {
            shader = ShaderProgram.fromResources(
                "/assets/hydrogen/shaders/ui_shape.vert",
                "/assets/hydrogen/shaders/ui_shape.frag"
            );
            vertexArray = glGenVertexArrays();
            quadBuffer = glGenBuffers();
            instanceBuffer = glGenBuffers();
            instances = MemoryUtil.memAlloc(INITIAL_INSTANCES * INSTANCE_STRIDE).order(ByteOrder.nativeOrder());

            glBindVertexArray(vertexArray);
            glBindBuffer(GL_ARRAY_BUFFER, quadBuffer);
            FloatBuffer quad = MemoryUtil.memAllocFloat(12);
            try {
                quad.put(new float[] {0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0}).flip();
                glBufferData(GL_ARRAY_BUFFER, quad, GL_STATIC_DRAW);
            } finally {
                MemoryUtil.memFree(quad);
            }
            int unitPosition = shader.attribute("aUnitPosition");
            glEnableVertexAttribArray(unitPosition);
            glVertexAttribPointer(unitPosition, 2, GL_FLOAT, false, 8, 0L);

            glBindBuffer(GL_ARRAY_BUFFER, instanceBuffer);
            glBufferData(GL_ARRAY_BUFFER, instances.capacity(), GL_STREAM_DRAW);
            instanceFloatAttribute("aRect", 4, 0);
            instanceFloatAttribute("aRadii", 4, 16);
            instanceColorAttribute("aColorTL", 32);
            instanceColorAttribute("aColorTR", 36);
            instanceColorAttribute("aColorBR", 40);
            instanceColorAttribute("aColorBL", 44);
            instanceColorAttribute("aStrokeColor", 48);
            instanceFloatAttribute("aEffects", 4, 52);
            instanceColorAttribute("aShadowColor", 68);
            glBindVertexArray(0);
        } catch (RuntimeException | Error error) {
            try {
                close();
            } catch (RuntimeException | Error cleanupError) {
                error.addSuppressed(cleanupError);
            }
            throw error;
        }
    }

    public void begin(float width, float height) {
        resolutionWidth = width;
        resolutionHeight = height;
        count = 0;
        instances.clear();
    }

    public void add(float x, float y, float width, float height,
                    float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius,
                    int topLeft, int topRight, int bottomRight, int bottomLeft,
                    float strokeWidth, int strokeColor, float softness,
                    float shadowSize, int shadowColor) {
        ensureCapacity(INSTANCE_STRIDE);
        instances.putFloat(x).putFloat(y).putFloat(width).putFloat(height);
        instances.putFloat(topLeftRadius).putFloat(topRightRadius).putFloat(bottomRightRadius).putFloat(bottomLeftRadius);
        putColor(topLeft);
        putColor(topRight);
        putColor(bottomRight);
        putColor(bottomLeft);
        putColor(strokeColor);
        instances.putFloat(softness).putFloat(strokeWidth).putFloat(Math.max(0.0f, shadowSize)).putFloat(0.0f);
        putColor(shadowColor);
        count++;
    }

    public void flush(GlStateCache state, RenderStats stats) {
        if (count == 0) return;
        instances.flip();
        state.bindArrayBuffer(instanceBuffer);
        // Orphaning prevents synchronization against commands consuming the
        // previous frame's storage, then one contiguous upload feeds the GPU.
        glBufferData(GL_ARRAY_BUFFER, Math.max(instances.capacity(), instances.remaining()), GL_STREAM_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0L, instances);
        state.useProgram(shader.id());
        shader.setVec2("uResolution", resolutionWidth, resolutionHeight);
        state.bindVertexArray(vertexArray);
        glDrawArraysInstanced(GL_TRIANGLES, 0, 6, count);
        stats.draw(count, count * INSTANCE_STRIDE);
        count = 0;
        instances.clear();
    }

    private void instanceFloatAttribute(String name, int components, long offset) {
        int location = shader.attribute(name);
        glEnableVertexAttribArray(location);
        glVertexAttribPointer(location, components, GL_FLOAT, false, INSTANCE_STRIDE, offset);
        instanceDivisor(location);
    }

    private void instanceColorAttribute(String name, long offset) {
        int location = shader.attribute(name);
        glEnableVertexAttribArray(location);
        glVertexAttribPointer(location, 4, GL_UNSIGNED_BYTE, true, INSTANCE_STRIDE, offset);
        instanceDivisor(location);
    }

    private void instanceDivisor(int location) {
        if (coreInstanceDivisors) {
            glVertexAttribDivisor(location, 1);
        } else {
            glVertexAttribDivisorARB(location, 1);
        }
    }

    private void putColor(int argb) {
        instances.put((byte) ((argb >>> 16) & 0xFF));
        instances.put((byte) ((argb >>> 8) & 0xFF));
        instances.put((byte) (argb & 0xFF));
        instances.put((byte) ((argb >>> 24) & 0xFF));
    }

    private void ensureCapacity(int bytes) {
        if (instances.remaining() >= bytes) return;
        int position = instances.position();
        int required = position + bytes;
        int capacity = instances.capacity();
        while (capacity < required) capacity <<= 1;
        instances = MemoryUtil.memRealloc(instances, capacity).order(ByteOrder.nativeOrder());
        instances.limit(capacity).position(position);
    }

    public int pendingCount() {
        return count;
    }

    @Override
    public void close() {
        if (instances != null) {
            MemoryUtil.memFree(instances);
            instances = null;
        }
        if (instanceBuffer != 0) {
            glDeleteBuffers(instanceBuffer);
            instanceBuffer = 0;
        }
        if (quadBuffer != 0) {
            glDeleteBuffers(quadBuffer);
            quadBuffer = 0;
        }
        if (vertexArray != 0) {
            glDeleteVertexArrays(vertexArray);
            vertexArray = 0;
        }
        if (shader != null) {
            shader.close();
            shader = null;
        }
    }
}
