package hydrogen.module;

import hydrogen.event.EventBus;

public abstract class Module {
    private final String id;
    private final String name;
    private final ModuleCategory category;
    private boolean enabled;

    protected Module(String id, String name, ModuleCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public final void setEnabled(boolean enabled, EventBus bus) {
        if (this.enabled == enabled) return;
        this.enabled = enabled;
        if (enabled) onEnable(bus); else onDisable(bus);
    }

    protected void onEnable(EventBus bus) {}
    protected void onDisable(EventBus bus) {}

    public final boolean isEnabled() { return enabled; }
    public String id() { return id; }
    public String name() { return name; }
    public ModuleCategory category() { return category; }
}
