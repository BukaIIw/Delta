package hydrogen.module;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ModuleManager {
    private final Map<String, Module> modules = new LinkedHashMap<>();

    public void register(Module module) {
        if (modules.putIfAbsent(module.id(), module) != null) {
            throw new IllegalStateException("Duplicate module: " + module.id());
        }
    }

    public Module get(String id) {
        return modules.get(id);
    }

    public Collection<Module> all() {
        return Collections.unmodifiableCollection(modules.values());
    }
}
