package aethereal.config;

import aethereal.core.EventManager;

import aethereal.core.Interface;

public abstract class BaseProcessor implements Interface {
    public abstract void setup();

    public abstract void unSetup();

    public BaseProcessor() {
        EventManager.a(this);
    }
}
