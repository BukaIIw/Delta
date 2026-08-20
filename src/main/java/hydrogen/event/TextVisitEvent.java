package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;

public class TextVisitEvent extends Event implements IEvent {
    private String a;

    @Generated
    public void a(String text) {
        this.a = text;
    }

    @Generated
    public TextVisitEvent(String text) {
        this.a = text;
    }

    @Generated
    public String b() {
        return this.a;
    }
}
