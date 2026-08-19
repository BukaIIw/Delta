package hydrogen.math;

public final class Easing {
    private Easing() {}

    public static float exponential(float current, float target, float speed, float deltaSeconds) {
        float factor = 1.0f - (float) Math.exp(-speed * Math.max(0.0f, deltaSeconds));
        return current + (target - current) * factor;
    }
}
