package hydrogen.ui;

/**
 * Allocation-free ARGB colour helpers used by the custom UI pipeline.
 */
public final class Color {
    private Color() {
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (clamp(alpha) << 24) | (clamp(red) << 16) | (clamp(green) << 8) | clamp(blue);
    }

    public static int rgb(int red, int green, int blue) {
        return argb(255, red, green, blue);
    }

    public static int alpha(int color, float alpha) {
        return (color & 0x00FF_FFFF) | (clamp(Math.round(((color >>> 24) & 0xFF) * alpha)) << 24);
    }

    public static int mix(int from, int to, float amount) {
        float t = Math.max(0.0f, Math.min(1.0f, amount));
        return argb(
            lerp((from >>> 24) & 0xFF, (to >>> 24) & 0xFF, t),
            lerp((from >>> 16) & 0xFF, (to >>> 16) & 0xFF, t),
            lerp((from >>> 8) & 0xFF, (to >>> 8) & 0xFF, t),
            lerp(from & 0xFF, to & 0xFF, t)
        );
    }

    private static int lerp(int from, int to, float amount) {
        return Math.round(from + ((to - from) * amount));
    }

    private static int clamp(int component) {
        return Math.max(0, Math.min(255, component));
    }
}
