package platform.client.processors.draw.fonts;


import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class FontData {
    private AtlasData atlas;
    private MetricsData metrics;
    private List<GlyphData> glyphs;

    @SerializedName("kerning")
    private List<KerningData> kernings;

    public AtlasData atlas() {
        return this.atlas;
    }

    public MetricsData metrics() {
        return this.metrics;
    }

    public List<GlyphData> glyphs() {
        return this.glyphs;
    }

    public List<KerningData> kernings() {
        return this.kernings;
    }

    public static final class AtlasData {

        @SerializedName("distanceRange")
        private final float range;
        private final float width;
        private final float height;

        public AtlasData(float range, float width, float height) {
            this.range = range;
            this.width = width;
            this.height = height;
        }
@SerializedName("distanceRange")
        public float range() {
            return this.range;
        }

        public float width() {
            return this.width;
        }

        public float height() {
            return this.height;
        }
    }

    public static final class MetricsData {
        private float lineHeight;
        private float ascender;
        private float descender;

        public float lineHeight() {
            return this.lineHeight;
        }

        public float ascender() {
            return this.ascender;
        }

        public float descender() {
            return this.descender;
        }

        public float baselineHeight() {
            return this.lineHeight + this.descender;
        }
    }

    public static final class GlyphData {
        private final int unicode;
        private final float advance;
        private final BoundsData planeBounds;
        private final BoundsData atlasBounds;

        public GlyphData(int unicode, float advance, BoundsData planeBounds, BoundsData atlasBounds) {
            this.unicode = unicode;
            this.advance = advance;
            this.planeBounds = planeBounds;
            this.atlasBounds = atlasBounds;
        }
public int unicode() {
            return this.unicode;
        }

        public float advance() {
            return this.advance;
        }

        public BoundsData planeBounds() {
            return this.planeBounds;
        }

        public BoundsData atlasBounds() {
            return this.atlasBounds;
        }
    }

    public static final class BoundsData {
        private final float left;
        private final float top;
        private final float right;
        private final float bottom;

        public BoundsData(float left, float top, float right, float bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
public float left() {
            return this.left;
        }

        public float top() {
            return this.top;
        }

        public float right() {
            return this.right;
        }

        public float bottom() {
            return this.bottom;
        }
    }

    public static final class KerningData {

        @SerializedName("unicode1")
        private final int leftChar;

        @SerializedName("unicode2")
        private final int rightChar;
        private final float advance;

        public KerningData(int leftChar, int rightChar, float advance) {
            this.leftChar = leftChar;
            this.rightChar = rightChar;
            this.advance = advance;
        }
@SerializedName("unicode1")
        public int leftChar() {
            return this.leftChar;
        }

        @SerializedName("unicode2")
        public int rightChar() {
            return this.rightChar;
        }

        public float advance() {
            return this.advance;
        }
    }
}
