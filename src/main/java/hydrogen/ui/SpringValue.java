package hydrogen.ui;

/**
 * Stable, frame-rate-independent damped spring. It gives interactions physical
 * motion without creating a timeline or allocating keyframes every frame.
 */
public final class SpringValue {
    private float value;
    private float velocity;
    private float target;
    private float response = 22.0f;
    private float damping = 0.82f;

    public SpringValue(float initial) {
        value = initial;
        target = initial;
    }

    public SpringValue tune(float response, float damping) {
        this.response = Math.max(0.01f, response);
        this.damping = Math.max(0.0f, Math.min(1.0f, damping));
        return this;
    }

    public void target(float target) {
        this.target = target;
    }

    public void snap(float value) {
        this.value = value;
        this.target = value;
        this.velocity = 0.0f;
    }

    public void update(float deltaSeconds) {
        float remaining = Math.min(Math.max(deltaSeconds, 0.0f), 0.05f);
        // Small fixed steps make the spring deterministic after a frame hitch.
        while (remaining > 0.0f) {
            float step = Math.min(remaining, 1.0f / 120.0f);
            float acceleration = (target - value) * response * response;
            velocity += acceleration * step;
            velocity *= (float) Math.pow(damping, step * 60.0f);
            value += velocity * step;
            remaining -= step;
        }
        if (Math.abs(target - value) < 0.0001f && Math.abs(velocity) < 0.0001f) {
            value = target;
            velocity = 0.0f;
        }
    }

    public float value() {
        return value;
    }

    public float target() {
        return target;
    }
}
