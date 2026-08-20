package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class PortalEvent extends Event implements IEvent {
    private boolean a;

    @Generated
    public void b(boolean inPortal) {
        this.a = inPortal;
    }

    @Generated
    public PortalEvent(boolean inPortal) {
        this.a = inPortal;
    }

    @Generated
    public boolean b() {
        return this.a;
    }
}
