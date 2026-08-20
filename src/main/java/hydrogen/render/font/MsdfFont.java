package hydrogen.render.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

/**
 * Renderer-owned MTSDF atlas. Font metadata and pixels are read directly from
 * classpath resources and uploaded with LWJGL; Minecraft's texture manager is
 * intentionally not involved.
 */
public final class MsdfFont implements AutoCloseable {
    private final Glyph[] glyphs;
    private final float lineHeight;
    private final float ascender;
    private final float distanceRange;
    private final int atlasWidth;
    private final int atlasHeight;
    private final int texture;

    public static MsdfFont load(String metadataPath, String texturePath) {
        JsonObject root = JsonParser.parseString(readText(metadataPath)).getAsJsonObject();
        JsonObject atlas = root.getAsJsonObject("atlas");
        JsonObject metrics = root.getAsJsonObject("metrics");
        int atlasWidth = atlas.get("width").getAsInt();
        int atlasHeight = atlas.get("height").getAsInt();
        float distanceRange = atlas.get("distanceRange").getAsFloat();
        float lineHeight = metrics.get("lineHeight").getAsFloat();
        float ascender = metrics.get("ascender").getAsFloat();

        JsonArray sourceGlyphs = root.getAsJsonArray("glyphs");
        int maximumCodePoint = 0;
        for (JsonElement element : sourceGlyphs) {
            maximumCodePoint = Math.max(maximumCodePoint, element.getAsJsonObject().get("unicode").getAsInt());
        }
        Glyph[] glyphs = new Glyph[maximumCodePoint + 1];
        for (JsonElement element : sourceGlyphs) {
            JsonObject data = element.getAsJsonObject();
            int codePoint = data.get("unicode").getAsInt();
            float advance = data.get("advance").getAsFloat();
            JsonObject plane = data.has("planeBounds") ? data.getAsJsonObject("planeBounds") : null;
            JsonObject bounds = data.has("atlasBounds") ? data.getAsJsonObject("atlasBounds") : null;
            glyphs[codePoint] = new Glyph(
                advance,
                number(plane, "left"), number(plane, "bottom"),
                number(plane, "right"), number(plane, "top"),
                number(bounds, "left") / atlasWidth,
                1.0f - number(bounds, "top") / atlasHeight,
                number(bounds, "right") / atlasWidth,
                1.0f - number(bounds, "bottom") / atlasHeight,
                plane != null && bounds != null
            );
        }

        int texture = uploadTexture(texturePath, atlasWidth, atlasHeight);
        return new MsdfFont(glyphs, lineHeight, ascender, distanceRange, atlasWidth, atlasHeight, texture);
    }

    private MsdfFont(Glyph[] glyphs, float lineHeight, float ascender, float distanceRange,
                     int atlasWidth, int atlasHeight, int texture) {
        this.glyphs = glyphs;
        this.lineHeight = lineHeight;
        this.ascender = ascender;
        this.distanceRange = distanceRange;
        this.atlasWidth = atlasWidth;
        this.atlasHeight = atlasHeight;
        this.texture = texture;
    }

    private static int uploadTexture(String path, int expectedWidth, int expectedHeight) {
        byte[] encoded = readBytes(path);
        ByteBuffer source = MemoryUtil.memAlloc(encoded.length);
        source.put(encoded).flip();
        ByteBuffer pixels = null;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);
            STBImage.stbi_set_flip_vertically_on_load(false);
            pixels = STBImage.stbi_load_from_memory(source, width, height, channels, 4);
            if (pixels == null) {
                throw new IllegalStateException("Unable to decode font atlas " + path + ": " + STBImage.stbi_failure_reason());
            }
            if (width.get(0) != expectedWidth || height.get(0) != expectedHeight) {
                throw new IllegalStateException("Font atlas dimensions do not match metadata: " + path);
            }
            int texture = glGenTextures();
            try {
                glBindTexture(GL_TEXTURE_2D, texture);
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                return texture;
            } catch (RuntimeException | Error error) {
                if (texture != 0) glDeleteTextures(texture);
                throw error;
            }
        } finally {
            if (pixels != null) STBImage.stbi_image_free(pixels);
            MemoryUtil.memFree(source);
        }
    }

    private static float number(JsonObject object, String name) {
        return object == null ? 0.0f : object.get(name).getAsFloat();
    }

    private static String readText(String path) {
        return new String(readBytes(path), StandardCharsets.UTF_8);
    }

    private static byte[] readBytes(String path) {
        try (InputStream stream = MsdfFont.class.getResourceAsStream(path)) {
            if (stream == null) throw new IllegalStateException("Missing font resource: " + path);
            return stream.readAllBytes();
        } catch (IOException error) {
            throw new IllegalStateException("Could not read font resource: " + path, error);
        }
    }

    public Glyph glyph(int codePoint) {
        Glyph glyph = codePoint >= 0 && codePoint < glyphs.length ? glyphs[codePoint] : null;
        if (glyph != null || codePoint == '?') return glyph;
        return '?' < glyphs.length ? glyphs['?'] : null;
    }

    public float width(String text, float size) {
        if (text == null || text.isEmpty()) return 0.0f;
        float width = 0.0f;
        for (int offset = 0; offset < text.length();) {
            int codePoint = text.codePointAt(offset);
            Glyph glyph = glyph(codePoint);
            if (glyph != null) width += glyph.advance * size;
            offset += Character.charCount(codePoint);
        }
        return width;
    }

    public float lineHeight(float size) { return lineHeight * size; }
    public float ascender() { return ascender; }
    public float distanceRange() { return distanceRange; }
    public int atlasWidth() { return atlasWidth; }
    public int atlasHeight() { return atlasHeight; }
    public int texture() { return texture; }

    @Override
    public void close() {
        glDeleteTextures(texture);
    }

    public record Glyph(
        float advance,
        float planeLeft,
        float planeBottom,
        float planeRight,
        float planeTop,
        float u0,
        float v0,
        float u1,
        float v1,
        boolean visible
    ) {
    }
}
