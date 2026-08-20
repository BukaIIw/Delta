package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class InputEvent extends Event implements IEvent {
    private float a;
    private float b;
    private boolean c;
    private boolean d;

    @Generated
    public void a(float forward) {
        this.a = forward;
    }

    @Generated
    public void b(float strafe) {
        this.b = strafe;
    }

    @Generated
    public void b(boolean jump) {
        this.c = jump;
    }

    @Generated
    public void c(boolean sneak) {
        this.d = sneak;
    }

    @Generated
    public float b() {
        return this.a;
    }

    @Generated
    public float c() {
        return this.b;
    }

    @Generated
    public boolean d() {
        return this.c;
    }

    @Generated
    public boolean e() {
        return this.d;
    }

    public InputEvent(float forward, float strafe, boolean jump, boolean sneak) {
        this.a = forward;
        this.b = strafe;
        this.c = jump;
        this.d = sneak;
    }
}
