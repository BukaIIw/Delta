package hydrogen.ui.model;

import java.util.List;

@FunctionalInterface
public interface ModuleRepository {
    ModuleRepository EMPTY = List::of;

    List<ModuleModel> modules();
}
