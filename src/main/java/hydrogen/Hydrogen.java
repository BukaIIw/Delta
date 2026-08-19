package hydrogen;

import hydrogen.event.EventBus;
import hydrogen.module.ModuleManager;
import hydrogen.render.RenderEngine;

public final class Hydrogen {
    private final EventBus eventBus = new EventBus();
    private final ModuleManager moduleManager = new ModuleManager();
    private final RenderEngine renderEngine = new RenderEngine();

    public void init() {
        renderEngine.init();
    }

    public EventBus eventBus() {
        return eventBus;
    }

    public ModuleManager modules() {
        return moduleManager;
    }

    public RenderEngine render() {
        return renderEngine;
    }
}
