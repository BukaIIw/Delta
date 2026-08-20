package hydrogen.render;

import hydrogen.render.batch.ShapeBatch;
import hydrogen.render.batch.TextBatch;
import hydrogen.render.font.MsdfFont;
import hydrogen.render.gl.GlStateCache;
import hydrogen.ui.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * Immediate authoring API backed by retained CPU batches. Calls stay in order;
 * adjacent shapes and text collapse into one GPU submission per material/clip.
 */
public final class Renderer2D implements AutoCloseable {
    private enum Pipeline { NONE, SHAPE, TEXT }

    private final ShapeBatch shapes;
    private final TextBatch text;
    private final MsdfFont font;
    private final GlStateCache state;
    private final RenderStats stats;
    private final float[] clips = new float[4 * 16];
    private Pipeline pipeline = Pipeline.NONE;
    private int clipDepth;
    private float width;
    private float height;
    private int framebufferWidth;
    private int framebufferHeight;
    private float translationX;
    private float translationY;
    private float globalAlpha = 1.0f;

    public Renderer2D(GlStateCache state, RenderStats stats) {
        this.state = state;
        this.stats = stats;
        ShapeBatch createdShapes = null;
        MsdfFont createdFont = null;
        TextBatch createdText = null;
        try {
            createdShapes = new ShapeBatch();
            createdFont = MsdfFont.load(
                "/assets/hydrogen/fonts/onest_regular.json",
                "/assets/hydrogen/fonts/onest_regular.png"
            );
            createdText = new TextBatch(createdFont);
        } catch (RuntimeException | Error error) {
            if (createdText != null) {
                closeAfterFailure(error, createdText);
            } else if (createdFont != null) {
                closeAfterFailure(error, createdFont);
            }
            if (createdShapes != null) closeAfterFailure(error, createdShapes);
            throw error;
        }
        shapes = createdShapes;
        font = createdFont;
        text = createdText;
    }

    private static void closeAfterFailure(Throwable original, AutoCloseable resource) {
        try {
            resource.close();
        } catch (Throwable cleanupError) {
            original.addSuppressed(cleanupError);
        }
    }

    public void begin(float width, float height, int framebufferWidth, int framebufferHeight) {
        this.width = Math.max(width, 1.0f);
        this.height = Math.max(height, 1.0f);
        this.framebufferWidth = Math.max(framebufferWidth, 1);
        this.framebufferHeight = Math.max(framebufferHeight, 1);
        translationX = 0.0f;
        translationY = 0.0f;
        globalAlpha = 1.0f;
        clipDepth = 0;
        pipeline = Pipeline.NONE;
        shapes.begin(this.width, this.height);
        text.begin(this.width, this.height);
        glViewport(0, 0, this.framebufferWidth, this.framebufferHeight);
        glColorMask(true, true, true, true);
        state.blending(true);
        state.standardAlphaBlend();
        state.depthTest(false);
        state.culling(false);
        state.scissor(false);
    }

    public void transform(float translateX, float translateY, float alpha) {
        flush();
        this.translationX = translateX;
        this.translationY = translateY;
        this.globalAlpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }

    public void rect(float x, float y, float width, float height, float radius, int color) {
        shape(x, y, width, height, radius, radius, radius, radius,
            color, color, color, color, 0.0f, 0, 0.72f, 0.0f, 0);
    }

    public void gradient(float x, float y, float width, float height, float radius,
                         int topLeft, int topRight, int bottomRight, int bottomLeft) {
        shape(x, y, width, height, radius, radius, radius, radius,
            topLeft, topRight, bottomRight, bottomLeft, 0.0f, 0, 0.72f, 0.0f, 0);
    }

    public void bordered(float x, float y, float width, float height, float radius,
                         int fill, float borderWidth, int border) {
        shape(x, y, width, height, radius, radius, radius, radius,
            fill, fill, fill, fill, borderWidth, border, 0.72f, 0.0f, 0);
    }

    public void surface(float x, float y, float width, float height, float radius,
                        int topLeft, int topRight, int bottomRight, int bottomLeft,
                        float borderWidth, int border, float shadowSize, int shadow) {
        shape(x, y, width, height, radius, radius, radius, radius,
            topLeft, topRight, bottomRight, bottomLeft, borderWidth, border, 0.72f, shadowSize, shadow);
    }

    public void shape(float x, float y, float width, float height,
                      float topLeftRadius, float topRightRadius, float bottomRightRadius, float bottomLeftRadius,
                      int topLeft, int topRight, int bottomRight, int bottomLeft,
                      float borderWidth, int border, float softness, float shadowSize, int shadow) {
        if (width <= 0.0f || height <= 0.0f) return;
        switchTo(Pipeline.SHAPE);
        shapes.add(
            x + translationX, y + translationY, width, height,
            topLeftRadius, topRightRadius, bottomRightRadius, bottomLeftRadius,
            fade(topLeft), fade(topRight), fade(bottomRight), fade(bottomLeft),
            borderWidth, fade(border), softness, shadowSize, fade(shadow)
        );
    }

    public void text(String value, float x, float y, float size, int color) {
        text(value, x, y, size, color, Float.MAX_VALUE);
    }

    public void text(String value, float x, float y, float size, int color, float maximumWidth) {
        switchTo(Pipeline.TEXT);
        text.add(value, x + translationX, y + translationY, size, fade(color), maximumWidth);
    }

    public float textWidth(String value, float size) {
        return font.width(value, size);
    }

    public void pushClip(float x, float y, float clipWidth, float clipHeight) {
        if (clipDepth >= 16) throw new IllegalStateException("UI clip stack overflow");
        flush();
        x += translationX;
        y += translationY;
        if (clipDepth > 0) {
            int previous = (clipDepth - 1) * 4;
            float right = Math.min(x + clipWidth, clips[previous] + clips[previous + 2]);
            float bottom = Math.min(y + clipHeight, clips[previous + 1] + clips[previous + 3]);
            x = Math.max(x, clips[previous]);
            y = Math.max(y, clips[previous + 1]);
            clipWidth = Math.max(0.0f, right - x);
            clipHeight = Math.max(0.0f, bottom - y);
        }
        int offset = clipDepth++ * 4;
        clips[offset] = x;
        clips[offset + 1] = y;
        clips[offset + 2] = clipWidth;
        clips[offset + 3] = clipHeight;
        applyClip(offset);
    }

    public void popClip() {
        if (clipDepth == 0) throw new IllegalStateException("UI clip stack underflow");
        flush();
        clipDepth--;
        if (clipDepth == 0) {
            state.scissor(false);
        } else {
            applyClip((clipDepth - 1) * 4);
        }
    }

    public void flushShapes() {
        if (shapes.pendingCount() > 0) shapes.flush(state, stats);
        if (pipeline == Pipeline.SHAPE) pipeline = Pipeline.NONE;
    }

    public void flushText() {
        if (text.pendingGlyphs() > 0) text.flush(state, stats);
        if (pipeline == Pipeline.TEXT) pipeline = Pipeline.NONE;
    }

    public void flush() {
        flushShapes();
        flushText();
    }

    public void end() {
        flush();
        while (clipDepth > 0) popClip();
    }

    private void switchTo(Pipeline next) {
        if (pipeline == next) return;
        if (pipeline == Pipeline.SHAPE) flushShapes();
        if (pipeline == Pipeline.TEXT) flushText();
        pipeline = next;
    }

    private void applyClip(int offset) {
        float scaleX = framebufferWidth / width;
        float scaleY = framebufferHeight / height;
        int x = Math.max(0, (int) Math.floor(clips[offset] * scaleX));
        int y = Math.max(0, (int) Math.floor((height - clips[offset + 1] - clips[offset + 3]) * scaleY));
        int clipWidth = Math.max(0, (int) Math.ceil(clips[offset + 2] * scaleX));
        int clipHeight = Math.max(0, (int) Math.ceil(clips[offset + 3] * scaleY));
        if (x + clipWidth > framebufferWidth) clipWidth = framebufferWidth - x;
        if (y + clipHeight > framebufferHeight) clipHeight = framebufferHeight - y;
        state.scissor(true);
        glScissor(x, y, Math.max(0, clipWidth), Math.max(0, clipHeight));
    }

    private int fade(int color) {
        return Color.alpha(color, globalAlpha);
    }

    @Override
    public void close() {
        try {
            text.close(); // TextBatch owns the font.
        } finally {
            shapes.close();
        }
    }
}
