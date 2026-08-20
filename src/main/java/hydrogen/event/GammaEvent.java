package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class GammaEvent extends Event implements IEvent {
    private double a;

    @Generated
    public void a(double gamma) {
        this.a = gamma;
    }

    @Generated
    public GammaEvent(double gamma) {
        this.a = gamma;
    }

    @Generated
    public double b() {
        return this.a;
    }
}
