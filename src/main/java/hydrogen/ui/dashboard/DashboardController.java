package hydrogen.ui.dashboard;

import hydrogen.ui.SpringValue;
import hydrogen.ui.model.ModuleModel;
import hydrogen.ui.model.ModuleRepository;
import hydrogen.ui.model.SettingModel;
import hydrogen.ui.model.UiCategory;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Stateful, renderer-independent interaction model for the dashboard. */
public final class DashboardController {
    private static final int KEY_ESCAPE = 256;
    private static final int KEY_ENTER = 257;
    private static final int KEY_BACKSPACE = 259;
    private static final int KEY_DELETE = 261;

    private final List<ModuleModel> modules;
    private final ArrayList<ModuleModel> filtered = new ArrayList<>();
    private final Map<ModuleModel, CardMotion> motions = new IdentityHashMap<>();
    private final SpringValue entrance = new SpringValue(0.0f).tune(18.0f, 0.76f);
    private final SpringValue scroll = new SpringValue(0.0f).tune(20.0f, 0.78f);
    private final SpringValue inspectorScroll = new SpringValue(0.0f).tune(20.0f, 0.78f);
    private final SpringValue inspector = new SpringValue(0.0f).tune(20.0f, 0.76f);
    private UiCategory category = UiCategory.ALL;
    private ModuleModel selected;
    private ModuleModel hovered;
    private SettingModel draggedSetting;
    private DragMode dragMode;
    private SettingModel editingSetting;
    private String query = "";
    private String normalizedQuery = "";
    private float scrollTarget;
    private float inspectorScrollTarget;
    private boolean searchFocused;
    private boolean filterDirty = true;

    public DashboardController(ModuleRepository repository) {
        modules = List.copyOf(repository.modules());
        for (ModuleModel module : modules) motions.put(module, new CardMotion(module.enabled()));
        entrance.target(1.0f);
    }

    public void update(float deltaSeconds, DashboardLayout layout, float mouseX, float mouseY) {
        rebuildFilter();
        scrollTarget = clamp(scrollTarget, 0.0f, maximumScroll(layout));
        inspectorScrollTarget = clamp(inspectorScrollTarget, 0.0f, maximumInspectorScroll(layout));
        scroll.target(scrollTarget);
        inspectorScroll.target(inspectorScrollTarget);
        entrance.target(1.0f);
        inspector.target(selected == null ? 0.0f : 1.0f);
        hovered = moduleAt(layout, mouseX, mouseY);
        entrance.update(deltaSeconds);
        scroll.update(deltaSeconds);
        inspectorScroll.update(deltaSeconds);
        inspector.update(deltaSeconds);
        for (ModuleModel module : modules) {
            CardMotion motion = motions.get(module);
            motion.hover.target(module == hovered ? 1.0f : 0.0f);
            motion.enabled.target(module.enabled() ? 1.0f : 0.0f);
            motion.hover.update(deltaSeconds);
            motion.enabled.update(deltaSeconds);
        }
    }

    public boolean click(DashboardLayout layout, float mouseX, float mouseY, int button) {
        if (editingSetting != null && editingSetting.kind() == SettingModel.Kind.KEY_BIND && button >= 0 && button <= 7) {
            editingSetting.setKeyCode(-100 + button);
            editingSetting = null;
            return true;
        }
        if (button != 0) return false;
        if (layout.inspectorOverlay && selected != null) {
            if (layout.contains(mouseX, mouseY, layout.inspectorX + layout.inspectorWidth - 44.0f,
                layout.panelY + 18.0f, 24.0f, 24.0f)) {
                closeInspector();
                return true;
            }
            SettingModel setting = settingAt(layout, mouseX, mouseY);
            if (setting != null) return clickSetting(setting, layout, mouseX, mouseY);
            if (layout.contains(mouseX, mouseY, layout.inspectorX - 8.0f, layout.panelY + 8.0f,
                layout.inspectorWidth, layout.panelHeight - 16.0f)) {
                editingSetting = null;
                return true;
            }
        }
        if (layout.contains(mouseX, mouseY, layout.searchX, layout.searchY, layout.searchWidth, layout.searchHeight)) {
            searchFocused = true;
            editingSetting = null;
            return true;
        }
        searchFocused = false;
        UiCategory[] categories = UiCategory.values();
        for (int i = 0; i < categories.length; i++) {
            if (layout.contains(mouseX, mouseY, layout.navX(), layout.navY(i), layout.navWidth(), DashboardLayout.NAV_HEIGHT)) {
                if (layout.inspectorOverlay) closeInspector();
                selectCategory(categories[i]);
                return true;
            }
        }

        if (!layout.inspectorOverlay) {
            SettingModel setting = settingAt(layout, mouseX, mouseY);
            if (setting != null) return clickSetting(setting, layout, mouseX, mouseY);
        }

        ModuleModel module = moduleAt(layout, mouseX, mouseY);
        if (module != null) {
            editingSetting = null;
            int index = filtered.indexOf(module);
            float cardX = layout.cardX(index);
            float cardY = layout.cardY(index, scroll.value());
            if (layout.contains(mouseX, mouseY, cardX + layout.cardWidth - 51.0f, cardY + 28.0f, 35.0f, 20.0f)) {
                module.toggle();
            } else if (selected != module) {
                selected = module;
                inspectorScrollTarget = 0.0f;
                inspectorScroll.snap(0.0f);
            }
            return true;
        }

        editingSetting = null;
        return false;
    }

    private boolean clickSetting(SettingModel setting, DashboardLayout layout, float mouseX, float mouseY) {
        if (setting.kind() == SettingModel.Kind.NUMBER) {
            editingSetting = null;
            float settingY = settingY(layout, setting);
            if (mouseY >= settingY + 27.0f) beginDrag(setting, DragMode.NORMALIZED, layout, mouseX, mouseY);
        } else if (setting.kind() == SettingModel.Kind.COLOR) {
            editingSetting = null;
            float settingY = settingY(layout, setting);
            if (mouseY >= settingY + 32.0f && mouseY <= settingY + 74.0f) {
                beginDrag(setting, DragMode.COLOR_SATURATION_BRIGHTNESS, layout, mouseX, mouseY);
            } else if (mouseY >= settingY + 77.0f && mouseY <= settingY + 89.0f) {
                beginDrag(setting, DragMode.NORMALIZED, layout, mouseX, mouseY);
            } else if (mouseY >= settingY + 92.0f && mouseY <= settingY + 106.0f) {
                beginDrag(setting, DragMode.COLOR_ALPHA, layout, mouseX, mouseY);
            }
        } else if (setting.kind() == SettingModel.Kind.TEXT || setting.kind() == SettingModel.Kind.KEY_BIND) {
            editingSetting = setting;
        } else {
            editingSetting = null;
            setting.activate();
        }
        return true;
    }

    public boolean drag(DashboardLayout layout, float mouseX, float mouseY, int button) {
        if (button != 0 || draggedSetting == null) return false;
        updateDraggedSetting(layout, mouseX, mouseY);
        return true;
    }

    public void release() {
        draggedSetting = null;
        dragMode = null;
    }

    public boolean scroll(DashboardLayout layout, float mouseX, float mouseY, double amount) {
        if (layout.inspectorWidth > 0.0f && (!layout.inspectorOverlay || selected != null)
            && layout.contains(mouseX, mouseY, layout.inspectorX,
                layout.panelY + 118.0f, layout.inspectorWidth, layout.panelHeight - 144.0f)) {
            inspectorScrollTarget = clamp(inspectorScrollTarget - (float) amount * 52.0f,
                0.0f, maximumInspectorScroll(layout));
            return true;
        }
        if (layout.inspectorOverlay && selected != null
            && layout.contains(mouseX, mouseY, layout.inspectorX - 8.0f, layout.panelY + 8.0f,
                layout.inspectorWidth, layout.panelHeight - 16.0f)) return true;
        if (!layout.contains(mouseX, mouseY, layout.contentX, layout.bodyY, layout.contentWidth, layout.bodyHeight)) return false;
        scrollTarget = clamp(scrollTarget - (float) amount * 52.0f, 0.0f, maximumScroll(layout));
        return true;
    }

    public void focusSearch(DashboardLayout layout) {
        if (layout.inspectorOverlay) closeInspector();
        editingSetting = null;
        searchFocused = true;
    }

    public void append(char character) {
        if (editingSetting != null && editingSetting.kind() == SettingModel.Kind.TEXT) {
            editingSetting.appendCharacter(character);
            return;
        }
        if (!searchFocused || Character.isISOControl(character) || query.length() >= 64) return;
        query += character;
        normalizedQuery = query.toLowerCase(Locale.ROOT);
        filterDirty = true;
        scrollTarget = 0.0f;
    }

    public boolean backspace() {
        if (editingSetting != null && editingSetting.kind() == SettingModel.Kind.TEXT) {
            return editingSetting.backspaceValue();
        }
        if (!searchFocused || query.isEmpty()) return false;
        int finalCodePoint = query.codePointBefore(query.length());
        query = query.substring(0, query.length() - Character.charCount(finalCodePoint));
        normalizedQuery = query.toLowerCase(Locale.ROOT);
        filterDirty = true;
        scrollTarget = 0.0f;
        return true;
    }

    public boolean keyPressed(int keyCode) {
        if (editingSetting == null) return false;
        if (editingSetting.kind() == SettingModel.Kind.KEY_BIND) {
            if (keyCode != KEY_ESCAPE) editingSetting.setKeyCode(keyCode == KEY_BACKSPACE || keyCode == KEY_DELETE ? -1 : keyCode);
            editingSetting = null;
            return true;
        }
        if (editingSetting.kind() == SettingModel.Kind.TEXT && (keyCode == KEY_ESCAPE || keyCode == KEY_ENTER)) {
            editingSetting = null;
            return true;
        }
        return false;
    }

    public boolean textInputActive() {
        return searchFocused || (editingSetting != null && editingSetting.kind() == SettingModel.Kind.TEXT);
    }

    public void closeInspector() {
        selected = null;
        editingSetting = null;
        draggedSetting = null;
        dragMode = null;
        inspectorScrollTarget = 0.0f;
        inspectorScroll.snap(0.0f);
    }

    private void selectCategory(UiCategory value) {
        editingSetting = null;
        if (category == value) return;
        category = value;
        filterDirty = true;
        scrollTarget = 0.0f;
        scroll.snap(0.0f);
    }

    private void rebuildFilter() {
        if (!filterDirty) return;
        filtered.clear();
        for (ModuleModel module : modules) {
            if (category != UiCategory.ALL && module.category() != category) continue;
            if (!normalizedQuery.isEmpty()) {
                String name = module.name().toLowerCase(Locale.ROOT);
                String description = module.description().toLowerCase(Locale.ROOT);
                if (!name.contains(normalizedQuery) && !description.contains(normalizedQuery)) continue;
            }
            filtered.add(module);
        }
        filterDirty = false;
    }

    private ModuleModel moduleAt(DashboardLayout layout, float x, float y) {
        if (layout.inspectorOverlay && selected != null
            && layout.contains(x, y, layout.inspectorX - 8.0f, layout.panelY + 8.0f,
                layout.inspectorWidth, layout.panelHeight - 16.0f)) return null;
        if (!layout.contains(x, y, layout.contentX, layout.bodyY, layout.contentWidth, layout.bodyHeight)) return null;
        for (int i = 0; i < filtered.size(); i++) {
            float cardY = layout.cardY(i, scroll.value());
            if (cardY + DashboardLayout.CARD_HEIGHT < layout.bodyY || cardY > layout.bodyY + layout.bodyHeight) continue;
            if (layout.contains(x, y, layout.cardX(i), cardY, layout.cardWidth, DashboardLayout.CARD_HEIGHT)) return filtered.get(i);
        }
        return null;
    }

    private SettingModel settingAt(DashboardLayout layout, float x, float y) {
        if (selected == null || layout.inspectorWidth <= 0.0f
            || y < layout.panelY + 125.0f || y > layout.panelY + layout.panelHeight - 26.0f) return null;
        float rowY = layout.panelY + 125.0f - inspectorScroll.value();
        for (SettingModel setting : selected.settings()) {
            if (!setting.visible()) continue;
            float height = DashboardMetrics.settingHeight(setting);
            if (rowY > layout.panelY + layout.panelHeight - 26.0f) break;
            if (layout.contains(x, y, layout.inspectorX + 18.0f, rowY, layout.inspectorWidth - 36.0f, height)) return setting;
            rowY += DashboardMetrics.settingAdvance(setting);
        }
        return null;
    }

    private float settingY(DashboardLayout layout, SettingModel target) {
        float rowY = layout.panelY + 125.0f - inspectorScroll.value();
        for (SettingModel setting : selected.settings()) {
            if (!setting.visible()) continue;
            if (setting == target) return rowY;
            rowY += DashboardMetrics.settingAdvance(setting);
        }
        return rowY;
    }

    private void beginDrag(SettingModel setting, DragMode mode, DashboardLayout layout, float mouseX, float mouseY) {
        draggedSetting = setting;
        dragMode = mode;
        updateDraggedSetting(layout, mouseX, mouseY);
    }

    private void updateDraggedSetting(DashboardLayout layout, float mouseX, float mouseY) {
        float trackX = layout.inspectorX + 30.0f;
        float trackWidth = layout.inspectorWidth - 60.0f;
        float horizontal = (mouseX - trackX) / trackWidth;
        if (dragMode == DragMode.COLOR_SATURATION_BRIGHTNESS) {
            float rowY = settingY(layout, draggedSetting);
            draggedSetting.setColorSaturationBrightness(horizontal, 1.0f - (mouseY - rowY - 32.0f) / 42.0f);
        } else if (dragMode == DragMode.COLOR_ALPHA) {
            draggedSetting.setColorAlpha(horizontal);
        } else {
            draggedSetting.setNormalizedValue(horizontal);
        }
    }

    private float maximumScroll(DashboardLayout layout) {
        int rows = (filtered.size() + layout.columns - 1) / layout.columns;
        float contentHeight = Math.max(0.0f, rows * (DashboardLayout.CARD_HEIGHT + DashboardLayout.CARD_GAP) - DashboardLayout.CARD_GAP);
        return Math.max(0.0f, contentHeight - layout.bodyHeight);
    }

    private float maximumInspectorScroll(DashboardLayout layout) {
        if (selected == null) return 0.0f;
        float contentHeight = 0.0f;
        for (SettingModel setting : selected.settings()) {
            if (setting.visible()) contentHeight += DashboardMetrics.settingAdvance(setting);
        }
        if (contentHeight > 0.0f) contentHeight -= DashboardMetrics.SETTING_GAP;
        float availableHeight = Math.max(0.0f, layout.panelHeight - 151.0f);
        return Math.max(0.0f, contentHeight - availableHeight);
    }

    private static float clamp(float value, float minimum, float maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    public int activeCount() {
        int count = 0;
        for (ModuleModel module : modules) if (module.enabled()) count++;
        return count;
    }

    public int count(UiCategory value) {
        if (value == UiCategory.ALL) return modules.size();
        int count = 0;
        for (ModuleModel module : modules) if (module.category() == value) count++;
        return count;
    }

    public List<ModuleModel> filtered() { rebuildFilter(); return filtered; }
    public CardMotion motion(ModuleModel module) { return motions.get(module); }
    public UiCategory category() { return category; }
    public ModuleModel selected() { return selected; }
    public String query() { return query; }
    public boolean searchFocused() { return searchFocused; }
    public boolean editing(SettingModel setting) { return editingSetting == setting; }
    public float scroll() { return scroll.value(); }
    public float inspectorScroll() { return inspectorScroll.value(); }
    public float entrance() { return entrance.value(); }
    public float inspectorAmount() { return inspector.value(); }

    private enum DragMode {
        NORMALIZED,
        COLOR_SATURATION_BRIGHTNESS,
        COLOR_ALPHA
    }

    public static final class CardMotion {
        private final SpringValue hover = new SpringValue(0.0f).tune(24.0f, 0.72f);
        private final SpringValue enabled;

        private CardMotion(boolean enabled) {
            this.enabled = new SpringValue(enabled ? 1.0f : 0.0f).tune(24.0f, 0.7f);
        }

        public float hover() { return hover.value(); }
        public float enabled() { return enabled.value(); }
    }
}
