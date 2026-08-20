package hydrogen;

import hydrogen.event.EventBus;
import hydrogen.module.ModuleManager;
import hydrogen.render.RenderEngine;
import hydrogen.ui.model.ModuleRepository;

/** Application facade and lifecycle owner for HydrogenDLC. */
public final class Hydrogen implements AutoCloseable {
    private static final Hydrogen INSTANCE = new Hydrogen();

    private final EventBus eventBus = new EventBus();
    private final ModuleManager moduleManager = new ModuleManager();
    private final RenderEngine renderEngine = new RenderEngine();
    private ModuleRepository moduleRepository = ModuleRepository.EMPTY;
    private boolean initialized;

    public static Hydrogen get() {
        return INSTANCE;
    }

    public synchronized void init() {
        initialized = true;
        renderEngine.init();
    }

    public synchronized void init(ModuleRepository repository) {
        moduleRepository = repository == null ? ModuleRepository.EMPTY : repository;
        init();
    }

    public EventBus eventBus() { return eventBus; }
    public ModuleManager modules() { return moduleManager; }
    public ModuleRepository moduleRepository() { return moduleRepository; }
    public RenderEngine render() { return renderEngine; }
    public boolean initialized() { return initialized; }

    @Override
    public synchronized void close() {
        if (!initialized) return;
        try {
            renderEngine.close();
        } finally {
            initialized = false;
        }
    }
}
