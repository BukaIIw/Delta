package hydrogen.ui;

import hydrogen.math.Easing;

public final class Animation {
    private float value;
    private float target;
    private float speed = 18.0f;

    public Animation(float initial) {
        value = initial;
        target = initial;
    }

    public void target(float target) { this.target = target; }
    public void speed(float speed) { this.speed = speed; }
    public void update(float deltaSeconds) { value = Easing.exponential(value, target, speed, deltaSeconds); }
    public float value() { return value; }
    public float target() { return target; }
}
