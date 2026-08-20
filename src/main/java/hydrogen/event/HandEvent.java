package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class HandEvent extends Event implements IEvent {
    private final a a;

    public enum a {
        PRE,
        POST
    }

    @Generated
    public HandEvent(a phase) {
        this.a = phase;
    }

    @Generated
    public a d() {
        return this.a;
    }

    public boolean b() {
        return this.a == a.PRE;
    }

    public boolean c() {
        return this.a == a.POST;
    }
}
