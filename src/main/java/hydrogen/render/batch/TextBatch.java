package hydrogen.render.batch;

import hydrogen.render.RenderStats;
import hydrogen.render.font.MsdfFont;
import hydrogen.render.font.MsdfFont.Glyph;
import hydrogen.render.gl.GlStateCache;
import hydrogen.render.gl.ShaderProgram;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/** One streaming draw for every contiguous run of MTSDF text. */
public final class TextBatch implements AutoCloseable {
    private static final int VERTEX_STRIDE = 20;
    private static final int VERTICES_PER_GLYPH = 6;
    private static final int BYTES_PER_GLYPH = VERTEX_STRIDE * VERTICES_PER_GLYPH;

    private ShaderProgram shader;
    private MsdfFont font;
    private int vertexArray;
    private int vertexBuffer;
    private ByteBuffer vertices;
    private int glyphCount;
    private float resolutionWidth;
    private float resolutionHeight;

    public TextBatch(MsdfFont font) {
        this.font = font;
        try {
            shader = ShaderProgram.fromResources(
                "/assets/hydrogen/shaders/ui_text.vert",
                "/assets/hydrogen/shaders/ui_text.frag"
            );
            vertexArray = glGenVertexArrays();
            vertexBuffer = glGenBuffers();
            vertices = MemoryUtil.memAlloc(BYTES_PER_GLYPH * 1024).order(ByteOrder.nativeOrder());

            glBindVertexArray(vertexArray);
            glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
            glBufferData(GL_ARRAY_BUFFER, vertices.capacity(), GL_STREAM_DRAW);
            attribute("aPosition", 2, GL_FLOAT, false, 0L);
            attribute("aUv", 2, GL_FLOAT, false, 8L);
            attribute("aColor", 4, GL_UNSIGNED_BYTE, true, 16L);
            glBindVertexArray(0);
        } catch (RuntimeException | Error error) {
            try {
                closeGpuResources();
            } catch (RuntimeException | Error cleanupError) {
                error.addSuppressed(cleanupError);
            }
            throw error;
        }
    }

    public void begin(float width, float height) {
        resolutionWidth = width;
        resolutionHeight = height;
        vertices.clear();
        glyphCount = 0;
    }

    public void add(String text, float x, float y, float size, int color, float maximumWidth) {
        if (text == null || text.isEmpty() || size <= 0.0f || maximumWidth <= 0.0f) return;
        boolean ellipsize = maximumWidth < Float.MAX_VALUE && font.width(text, size) > maximumWidth;
        Glyph dot = font.glyph('.');
        float ellipsisWidth = dot == null ? 0.0f : dot.advance() * size * 3.0f;
        float contentLimit = ellipsize ? Math.max(0.0f, maximumWidth - ellipsisWidth) : maximumWidth;
        float cursor = x;
        float baseline = y + font.ascender() * size;

        for (int offset = 0; offset < text.length();) {
            int codePoint = text.codePointAt(offset);
            Glyph glyph = font.glyph(codePoint);
            offset += Character.charCount(codePoint);
            if (glyph == null) continue;
            float advance = glyph.advance() * size;
            if (cursor + advance - x > contentLimit) break;
            appendGlyph(glyph, cursor, baseline, size, color);
            cursor += advance;
        }
        if (ellipsize && dot != null && cursor + ellipsisWidth - x <= maximumWidth + 0.001f) {
            for (int i = 0; i < 3; i++) {
                appendGlyph(dot, cursor, baseline, size, color);
                cursor += dot.advance() * size;
            }
        }
    }

    private void appendGlyph(Glyph glyph, float cursor, float baseline, float size, int color) {
        if (!glyph.visible()) return;
        ensureCapacity(BYTES_PER_GLYPH);
        float x0 = cursor + glyph.planeLeft() * size;
        float y0 = baseline - glyph.planeTop() * size;
        float x1 = cursor + glyph.planeRight() * size;
        float y1 = baseline - glyph.planeBottom() * size;
        vertex(x0, y0, glyph.u0(), glyph.v0(), color);
        vertex(x0, y1, glyph.u0(), glyph.v1(), color);
        vertex(x1, y1, glyph.u1(), glyph.v1(), color);
        vertex(x0, y0, glyph.u0(), glyph.v0(), color);
        vertex(x1, y1, glyph.u1(), glyph.v1(), color);
        vertex(x1, y0, glyph.u1(), glyph.v0(), color);
        glyphCount++;
    }

    public void flush(GlStateCache state, RenderStats stats) {
        if (glyphCount == 0) return;
        vertices.flip();
        state.bindArrayBuffer(vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, Math.max(vertices.capacity(), vertices.remaining()), GL_STREAM_DRAW);
        glBufferSubData(GL_ARRAY_BUFFER, 0L, vertices);
        state.useProgram(shader.id());
        shader.setVec2("uResolution", resolutionWidth, resolutionHeight);
        shader.setInt("uAtlas", 0);
        shader.setVec2("uAtlasSize", font.atlasWidth(), font.atlasHeight());
        shader.setFloat("uDistanceRange", font.distanceRange());
        state.textureUnit0(font.texture());
        state.bindVertexArray(vertexArray);
        glDrawArrays(GL_TRIANGLES, 0, glyphCount * VERTICES_PER_GLYPH);
        stats.textDraw(glyphCount, glyphCount * BYTES_PER_GLYPH);
        glyphCount = 0;
        vertices.clear();
    }

    private void attribute(String name, int components, int type, boolean normalized, long offset) {
        int location = shader.attribute(name);
        glEnableVertexAttribArray(location);
        glVertexAttribPointer(location, components, type, normalized, VERTEX_STRIDE, offset);
    }

    private void vertex(float x, float y, float u, float v, int argb) {
        vertices.putFloat(x).putFloat(y).putFloat(u).putFloat(v);
        vertices.put((byte) ((argb >>> 16) & 0xFF));
        vertices.put((byte) ((argb >>> 8) & 0xFF));
        vertices.put((byte) (argb & 0xFF));
        vertices.put((byte) ((argb >>> 24) & 0xFF));
    }

    private void ensureCapacity(int bytes) {
        if (vertices.remaining() >= bytes) return;
        int position = vertices.position();
        int required = position + bytes;
        int capacity = vertices.capacity();
        while (capacity < required) capacity <<= 1;
        vertices = MemoryUtil.memRealloc(vertices, capacity).order(ByteOrder.nativeOrder());
        vertices.limit(capacity).position(position);
    }

    public int pendingGlyphs() {
        return glyphCount;
    }

    @Override
    public void close() {
        try {
            closeGpuResources();
        } finally {
            if (font != null) {
                font.close();
                font = null;
            }
        }
    }

    private void closeGpuResources() {
        if (vertices != null) {
            MemoryUtil.memFree(vertices);
            vertices = null;
        }
        if (vertexBuffer != 0) {
            glDeleteBuffers(vertexBuffer);
            vertexBuffer = 0;
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
