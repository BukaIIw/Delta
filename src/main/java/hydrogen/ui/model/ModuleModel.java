package hydrogen.ui.model;

import java.util.List;

/** UI-facing module contract; game-specific classes stay behind an adapter. */
public interface ModuleModel {
    String id();
    String name();
    String description();
    UiCategory category();
    boolean enabled();
    void toggle();
    List<SettingModel> settings();
}
