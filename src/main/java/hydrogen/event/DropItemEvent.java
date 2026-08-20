package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class DropItemEvent extends Event implements IEvent {
    private int a;

    @Generated
    public DropItemEvent(int slot) {
        this.a = slot;
    }

    @Generated
    public int b() {
        return this.a;
    }
}
