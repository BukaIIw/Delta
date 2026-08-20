package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class ClickEvent extends Event implements IEvent {
    private final a a;
    private final double b;
    private final double c;
    private final int d;

    public enum a {
        PRESS,
        RELEASE,
        DRAG
    }

    @Generated
    public a e() {
        return this.a;
    }

    @Generated
    public double f() {
        return this.b;
    }

    @Generated
    public double g() {
        return this.c;
    }

    @Generated
    public int h() {
        return this.d;
    }

    public ClickEvent(double mouseX, double mouseY, int button, a type) {
        this.b = mouseX;
        this.c = mouseY;
        this.d = button;
        this.a = type;
    }

    public boolean b() {
        return this.a == a.PRESS;
    }

    public boolean c() {
        return this.a == a.RELEASE;
    }

    public boolean d() {
        return this.a == a.DRAG;
    }
}
