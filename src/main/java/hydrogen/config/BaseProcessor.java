package hydrogen.config;

import hydrogen.core.EventManager;

import hydrogen.core.Interface;

public abstract class BaseProcessor implements Interface {
    public abstract void setup();

    public abstract void unSetup();

    public BaseProcessor() {
        EventManager.a(this);
    }
}
