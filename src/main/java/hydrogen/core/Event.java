package hydrogen.core;

import hydrogen.core.IEvent;

public class Event implements IEvent, Cancellable {
    private boolean a;

    protected Event() {
    }

    @Override
    public boolean a() {
        return this.a;
    }

    @Override
    public void a(boolean state) {
        this.a = state;
    }
}
