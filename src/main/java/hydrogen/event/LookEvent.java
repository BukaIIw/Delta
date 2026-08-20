package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class LookEvent extends Event implements IEvent {
    public double a;
    public double b;

    @Generated
    public LookEvent(double yaw, double pitch) {
        this.a = yaw;
        this.b = pitch;
    }

    @Generated
    public double b() {
        return this.a;
    }

    @Generated
    public double c() {
        return this.b;
    }
}
