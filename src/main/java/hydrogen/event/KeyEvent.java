package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class KeyEvent extends Event implements IEvent {
    private final int a;
    private final int b;
    private final int c;
    private final int d;

    @Generated
    public int b() {
        return this.a;
    }

    @Generated
    public int c() {
        return this.b;
    }

    @Generated
    public int d() {
        return this.c;
    }

    @Generated
    public int e() {
        return this.d;
    }

    public KeyEvent(int key, int scanCode, int action, int modifiers) {
        this.a = key;
        this.b = scanCode;
        this.c = action;
        this.d = modifiers;
    }
}
