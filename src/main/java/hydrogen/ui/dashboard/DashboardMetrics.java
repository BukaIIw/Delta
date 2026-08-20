package hydrogen.ui.dashboard;

import hydrogen.ui.model.SettingModel;

/** Shared inspector geometry used by both hit testing and rendering. */
final class DashboardMetrics {
    static final float SETTING_GAP = 7.0f;
    static final float SETTING_HEIGHT = 44.0f;
    static final float COLOR_SETTING_HEIGHT = 112.0f;

    private DashboardMetrics() {
    }

    static float settingHeight(SettingModel setting) {
        return setting.kind() == SettingModel.Kind.COLOR ? COLOR_SETTING_HEIGHT : SETTING_HEIGHT;
    }

    static float settingAdvance(SettingModel setting) {
        return settingHeight(setting) + SETTING_GAP;
    }
}
