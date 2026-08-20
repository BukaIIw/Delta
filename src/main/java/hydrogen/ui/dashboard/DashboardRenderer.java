package hydrogen.ui.dashboard;

import hydrogen.render.RenderStats;
import hydrogen.render.Renderer2D;
import hydrogen.ui.Color;
import hydrogen.ui.dashboard.DashboardController.CardMotion;
import hydrogen.ui.model.ModuleModel;
import hydrogen.ui.model.SettingModel;
import hydrogen.ui.model.UiCategory;

import java.util.List;

import static hydrogen.ui.dashboard.DashboardPalette.*;

/** Pure custom-renderer view for the Hydrogen control center. */
public final class DashboardRenderer {
    public void render(Renderer2D draw, DashboardController controller, DashboardLayout layout, RenderStats previousStats) {
        draw.gradient(0.0f, 0.0f, layout.screenWidth, layout.screenHeight, 0.0f,
            BACKDROP_TL, BACKDROP_TR, BACKDROP_BR, BACKDROP_BL);
        draw.surface(layout.screenWidth * 0.16f, layout.screenHeight * 0.08f, 150.0f, 150.0f, 75.0f,
            Color.argb(32, 139, 124, 255), Color.argb(18, 139, 124, 255),
            Color.argb(0, 139, 124, 255), Color.argb(14, 139, 124, 255),
            0.0f, 0, 48.0f, Color.argb(68, 92, 72, 220));
        draw.surface(layout.screenWidth * 0.76f, layout.screenHeight * 0.64f, 190.0f, 190.0f, 95.0f,
            Color.argb(20, 92, 214, 224), Color.argb(10, 92, 214, 224),
            Color.argb(0, 92, 214, 224), Color.argb(10, 92, 214, 224),
            0.0f, 0, 58.0f, Color.argb(52, 42, 166, 183));

        float entrance = clamp(controller.entrance());
        float eased = 1.0f - (float) Math.pow(1.0f - entrance, 3.0);
        draw.transform(0.0f, (1.0f - eased) * 18.0f, eased);
        draw.surface(layout.panelX, layout.panelY, layout.panelWidth, layout.panelHeight, 18.0f,
            PANEL_ALT, PANEL, PANEL, PANEL_ALT, 1.0f, BORDER_SOFT, 28.0f, SHADOW);
        draw.shape(layout.panelX, layout.panelY, layout.sidebarWidth, layout.panelHeight,
            18.0f, 0.0f, 0.0f, 18.0f,
            SIDEBAR_TOP, SIDEBAR_TOP, SIDEBAR_BOTTOM, SIDEBAR_BOTTOM,
            0.0f, 0, 0.72f, 0.0f, 0);
        draw.gradient(layout.panelX + layout.sidebarWidth - 1.0f, layout.panelY + 16.0f, 1.0f,
            layout.panelHeight - 32.0f, 0.5f, Color.alpha(BORDER, 0.15f), Color.alpha(BORDER, 0.5f),
            Color.alpha(BORDER, 0.08f), Color.alpha(BORDER, 0.25f));
        draw.gradient(layout.panelX + 1.0f, layout.panelY + 1.0f, layout.panelWidth - 2.0f, 2.0f, 1.0f,
            Color.alpha(ACCENT, 0.1f), Color.alpha(ACCENT_BRIGHT, 0.85f),
            Color.alpha(CYAN, 0.25f), Color.alpha(CYAN, 0.05f));

        draw.surface(layout.panelX + 16.0f, layout.panelY + 18.0f, 38.0f, 38.0f, 11.0f,
            ACCENT_BRIGHT, ACCENT, Color.mix(ACCENT, CYAN, 0.18f), ACCENT,
            1.0f, Color.alpha(ACCENT_BRIGHT, 0.8f), 13.0f, Color.alpha(ACCENT, 0.38f));
        draw.bordered(layout.searchX, layout.searchY, layout.searchWidth, layout.searchHeight, 10.0f,
            FIELD, 1.0f, controller.searchFocused() ? Color.alpha(ACCENT, 0.75f) : BORDER_SOFT);

        drawNavShapes(draw, controller, layout);
        drawHeaderShapes(draw, controller, layout);
        if (!layout.inspectorOverlay) drawInspectorShapes(draw, controller, layout);

        draw.pushClip(layout.contentX, layout.bodyY, layout.contentWidth, layout.bodyHeight);
        drawCardShapes(draw, controller, layout);
        draw.flushShapes();
        drawCardText(draw, controller, layout);
        draw.popClip();

        drawGlobalText(draw, controller, layout, previousStats);
        if (layout.inspectorOverlay) drawInspectorShapes(draw, controller, layout);
        drawInspectorText(draw, controller, layout);
        draw.transform(0.0f, 0.0f, 1.0f);
    }

    private void drawNavShapes(Renderer2D draw, DashboardController controller, DashboardLayout layout) {
        UiCategory[] categories = UiCategory.values();
        for (int i = 0; i < categories.length; i++) {
            UiCategory category = categories[i];
            float y = layout.navY(i);
            if (category == controller.category()) {
                draw.gradient(layout.navX(), y, layout.navWidth(), DashboardLayout.NAV_HEIGHT, 10.0f,
                    Color.argb(48, 139, 124, 255), Color.argb(26, 139, 124, 255),
                    Color.argb(18, 92, 214, 224), Color.argb(32, 139, 124, 255));
                draw.rect(layout.navX() + 5.0f, y + 9.0f, 2.0f, 20.0f, 1.0f, ACCENT_BRIGHT);
            }
        }
        float statusY = layout.panelY + layout.panelHeight - 66.0f;
        draw.bordered(layout.panelX + 14.0f, statusY, layout.sidebarWidth - 28.0f, 46.0f,
            12.0f, Color.argb(150, 12, 15, 24), 1.0f, BORDER_SOFT);
        draw.rect(layout.panelX + 26.0f, statusY + 17.0f, 8.0f, 8.0f, 4.0f, GREEN);
    }

    private void drawHeaderShapes(Renderer2D draw, DashboardController controller, DashboardLayout layout) {
        float chipY = layout.panelY + 67.0f;
        draw.bordered(layout.contentX, chipY, 100.0f, 25.0f, 8.0f,
            Color.argb(130, 24, 28, 43), 1.0f, BORDER_SOFT);
        draw.rect(layout.contentX + 10.0f, chipY + 9.0f, 7.0f, 7.0f, 3.5f, GREEN);
        draw.bordered(layout.contentX + 108.0f, chipY, 111.0f, 25.0f, 8.0f,
            Color.argb(130, 24, 28, 43), 1.0f, BORDER_SOFT);
        draw.rect(layout.contentX + 118.0f, chipY + 9.0f, 7.0f, 7.0f, 3.5f, CYAN);
    }

    private void drawInspectorShapes(Renderer2D draw, DashboardController controller, DashboardLayout layout) {
        if (layout.inspectorWidth <= 0.0f) return;
        ModuleModel selected = controller.selected();
        if (layout.inspectorOverlay && selected == null) return;
        if (layout.inspectorOverlay) {
            draw.surface(layout.inspectorX - 8.0f, layout.panelY + 8.0f, layout.inspectorWidth,
                layout.panelHeight - 16.0f, 14.0f, PANEL_ALT, PANEL, PANEL, PANEL_ALT,
                1.0f, BORDER, 22.0f, SHADOW);
            draw.bordered(layout.inspectorX + layout.inspectorWidth - 44.0f, layout.panelY + 18.0f,
                24.0f, 24.0f, 8.0f, FIELD, 1.0f, BORDER_SOFT);
        }
        draw.gradient(layout.inspectorX, layout.panelY + 12.0f, 1.0f, layout.panelHeight - 24.0f, 0.5f,
            Color.alpha(BORDER, 0.1f), Color.alpha(BORDER, 0.35f),
            Color.alpha(BORDER, 0.08f), Color.alpha(BORDER, 0.25f));
        if (selected == null) {
            draw.bordered(layout.inspectorX + 18.0f, layout.panelY + 90.0f,
                layout.inspectorWidth - 36.0f, 112.0f, 14.0f,
                Color.argb(96, 25, 28, 42), 1.0f, BORDER_SOFT);
            return;
        }
        float alpha = controller.inspectorAmount();
        draw.surface(layout.inspectorX + 18.0f, layout.panelY + 64.0f,
            layout.inspectorWidth - 36.0f, 46.0f, 12.0f,
            Color.alpha(CARD_ACTIVE, alpha), Color.alpha(CARD, alpha),
            Color.alpha(CARD, alpha), Color.alpha(CARD_ACTIVE, alpha),
            1.0f, Color.alpha(ACCENT, 0.25f * alpha), 8.0f, Color.alpha(ACCENT, 0.08f * alpha));

        float clipY = layout.panelY + 118.0f;
        float clipHeight = layout.panelHeight - 144.0f;
        draw.pushClip(layout.inspectorX + 18.0f, clipY, layout.inspectorWidth - 36.0f, clipHeight);
        float rowY = layout.panelY + 125.0f - controller.inspectorScroll();
        for (SettingModel setting : selected.settings()) {
            if (!setting.visible()) continue;
            float height = DashboardMetrics.settingHeight(setting);
            if (rowY > clipY + clipHeight) break;
            if (rowY + height >= clipY) {
                draw.bordered(layout.inspectorX + 18.0f, rowY, layout.inspectorWidth - 36.0f, height,
                    10.0f, Color.argb(145, 22, 25, 38), 1.0f, BORDER_SOFT);
                drawSettingControl(draw, controller, setting, layout, rowY);
            }
            rowY += DashboardMetrics.settingAdvance(setting);
        }
        draw.popClip();
    }

    private void drawSettingControl(Renderer2D draw, DashboardController controller, SettingModel setting, DashboardLayout layout, float rowY) {
        float right = layout.inspectorX + layout.inspectorWidth - 30.0f;
        if (setting.kind() == SettingModel.Kind.BOOLEAN) {
            boolean enabled = "On".equals(setting.valueText());
            int track = enabled ? Color.alpha(ACCENT, 0.72f) : Color.argb(210, 53, 58, 78);
            draw.rect(right - 31.0f, rowY + 14.0f, 31.0f, 16.0f, 8.0f, track);
            draw.rect(right - 29.0f + (enabled ? 15.0f : 0.0f), rowY + 16.0f, 12.0f, 12.0f, 6.0f, TEXT);
        } else if (setting.kind() == SettingModel.Kind.NUMBER) {
            float trackX = layout.inspectorX + 30.0f;
            float trackWidth = layout.inspectorWidth - 60.0f;
            draw.rect(trackX, rowY + 32.0f, trackWidth, 3.0f, 1.5f, Color.argb(210, 50, 55, 74));
            draw.gradient(trackX, rowY + 32.0f, trackWidth * clamp(setting.normalizedValue()), 3.0f, 1.5f,
                ACCENT, CYAN, CYAN, ACCENT);
            draw.rect(trackX + trackWidth * clamp(setting.normalizedValue()) - 3.5f,
                rowY + 30.0f, 7.0f, 7.0f, 3.5f, TEXT);
        } else if (setting.kind() == SettingModel.Kind.CHOICE || setting.kind() == SettingModel.Kind.ACTION
            || setting.kind() == SettingModel.Kind.TEXT || setting.kind() == SettingModel.Kind.KEY_BIND) {
            int border = controller.editing(setting) ? Color.alpha(ACCENT_BRIGHT, 0.75f) : Color.alpha(ACCENT, 0.32f);
            draw.bordered(right - 76.0f, rowY + 12.0f, 76.0f, 20.0f, 7.0f,
                Color.alpha(ACCENT, controller.editing(setting) ? 0.22f : 0.13f), 1.0f, border);
        } else if (setting.kind() == SettingModel.Kind.COLOR) {
            float trackX = layout.inspectorX + 30.0f;
            float trackWidth = layout.inspectorWidth - 60.0f;
            int color = setting.colorArgb();
            int opaqueColor = color | 0xFF000000;
            int hueColor = 0xFF000000 | (java.awt.Color.HSBtoRGB(clamp(setting.normalizedValue()), 1.0f, 1.0f) & 0x00FFFFFF);

            draw.gradient(trackX, rowY + 32.0f, trackWidth, 42.0f, 5.0f,
                0xFFFFFFFF, hueColor, hueColor, 0xFFFFFFFF);
            draw.gradient(trackX, rowY + 32.0f, trackWidth, 42.0f, 5.0f,
                0x00000000, 0x00000000, 0xFF000000, 0xFF000000);
            float saturationX = trackX + trackWidth * clamp(setting.colorSaturation());
            float brightnessY = rowY + 32.0f + 42.0f * (1.0f - clamp(setting.colorBrightness()));
            draw.bordered(saturationX - 4.0f, brightnessY - 4.0f, 8.0f, 8.0f, 4.0f,
                opaqueColor, 1.0f, TEXT);

            float segmentWidth = trackWidth / 6.0f;
            int[] hues = { 0xFFFF5B68, 0xFFFFE15B, 0xFF65E276, 0xFF55DDE6, 0xFF627CFF, 0xFFD368FF, 0xFFFF5B68 };
            for (int i = 0; i < 6; i++) {
                draw.gradient(trackX + i * segmentWidth, rowY + 80.0f, segmentWidth + 0.5f, 5.0f, 2.5f,
                    hues[i], hues[i + 1], hues[i + 1], hues[i]);
            }
            float hueX = trackX + trackWidth * clamp(setting.normalizedValue());
            draw.rect(hueX - 2.0f, rowY + 78.0f, 4.0f, 9.0f, 2.0f, TEXT);

            draw.rect(trackX, rowY + 97.0f, trackWidth, 5.0f, 2.5f, Color.argb(255, 36, 40, 55));
            draw.gradient(trackX, rowY + 97.0f, trackWidth, 5.0f, 2.5f,
                color & 0x00FFFFFF, opaqueColor, opaqueColor, color & 0x00FFFFFF);
            float alphaX = trackX + trackWidth * clamp(setting.colorAlpha());
            draw.rect(alphaX - 2.0f, rowY + 95.0f, 4.0f, 9.0f, 2.0f, TEXT);

            int checker = Color.argb(255, 71, 76, 94);
            draw.rect(right - 18.0f, rowY + 8.0f, 18.0f, 18.0f, 6.0f, checker);
            draw.rect(right - 18.0f, rowY + 8.0f, 18.0f, 18.0f, 6.0f, color);
        }
    }

    private void drawCardShapes(Renderer2D draw, DashboardController controller, DashboardLayout layout) {
        List<ModuleModel> modules = controller.filtered();
        if (modules.isEmpty()) {
            draw.bordered(layout.contentX, layout.bodyY + 10.0f, layout.contentWidth, 112.0f, 14.0f,
                Color.argb(125, 24, 27, 41), 1.0f, BORDER_SOFT);
            return;
        }
        for (int i = 0; i < modules.size(); i++) {
            float y = layout.cardY(i, controller.scroll());
            if (y + DashboardLayout.CARD_HEIGHT < layout.bodyY || y > layout.bodyY + layout.bodyHeight) continue;
            float x = layout.cardX(i);
            ModuleModel module = modules.get(i);
            CardMotion motion = controller.motion(module);
            int base = Color.mix(CARD, CARD_HOVER, motion.hover());
            base = Color.mix(base, CARD_ACTIVE, motion.enabled() * 0.55f);
            draw.surface(x, y, layout.cardWidth, DashboardLayout.CARD_HEIGHT, 13.0f,
                base, Color.mix(base, PANEL_ALT, 0.08f), Color.mix(base, PANEL, 0.12f), base,
                1.0f, Color.mix(BORDER_SOFT, Color.alpha(ACCENT, 0.52f), motion.enabled()),
                8.0f + 4.0f * motion.hover(), Color.alpha(ACCENT, 0.08f * motion.enabled()));
            draw.gradient(x, y + 12.0f, 2.0f, DashboardLayout.CARD_HEIGHT - 24.0f, 1.0f,
                Color.alpha(ACCENT_BRIGHT, motion.enabled()), Color.alpha(CYAN, motion.enabled()),
                Color.alpha(CYAN, motion.enabled()), Color.alpha(ACCENT, motion.enabled()));
            draw.gradient(x + 14.0f, y + 16.0f, 34.0f, 34.0f, 10.0f,
                Color.alpha(ACCENT_BRIGHT, 0.24f + 0.22f * motion.enabled()),
                Color.alpha(ACCENT, 0.16f + 0.18f * motion.enabled()),
                Color.alpha(CYAN, 0.12f + 0.14f * motion.enabled()),
                Color.alpha(ACCENT, 0.2f + 0.2f * motion.enabled()));
            float toggleX = x + layout.cardWidth - 51.0f;
            float toggleY = y + 29.0f;
            int track = Color.mix(Color.argb(220, 51, 56, 76), Color.alpha(ACCENT, 0.82f), motion.enabled());
            draw.rect(toggleX, toggleY, 35.0f, 19.0f, 9.5f, track);
            draw.surface(toggleX + 2.5f + motion.enabled() * 16.0f, toggleY + 2.5f, 14.0f, 14.0f, 7.0f,
                TEXT, TEXT, TEXT, TEXT, 0.0f, 0, 5.0f, Color.argb(90, 0, 0, 0));
        }
    }

    private void drawCardText(Renderer2D draw, DashboardController controller, DashboardLayout layout) {
        List<ModuleModel> modules = controller.filtered();
        if (modules.isEmpty()) {
            draw.text("No modules match this view", layout.contentX + 22.0f, layout.bodyY + 38.0f, 15.0f, TEXT);
            draw.text("Try another category or clear your search.", layout.contentX + 22.0f,
                layout.bodyY + 64.0f, 11.0f, TEXT_MUTED, layout.contentWidth - 44.0f);
            return;
        }
        for (int i = 0; i < modules.size(); i++) {
            float y = layout.cardY(i, controller.scroll());
            if (y + DashboardLayout.CARD_HEIGHT < layout.bodyY || y > layout.bodyY + layout.bodyHeight) continue;
            float x = layout.cardX(i);
            ModuleModel module = modules.get(i);
            String initial = module.name().isEmpty() ? "H" : module.name().substring(0, 1).toUpperCase();
            draw.text(initial, x + 25.0f - draw.textWidth(initial, 13.0f) * 0.5f, y + 25.0f, 13.0f, TEXT);
            draw.text(module.name(), x + 59.0f, y + 15.0f, 13.0f, TEXT, layout.cardWidth - 126.0f);
            draw.text(module.description(), x + 59.0f, y + 38.0f, 9.5f, TEXT_MUTED, layout.cardWidth - 126.0f);
            draw.text(module.category().label().toUpperCase(), x + 15.0f, y + 61.0f, 8.0f,
                module.enabled() ? ACCENT_BRIGHT : TEXT_DIM, layout.cardWidth - 30.0f);
        }
    }

    private void drawGlobalText(Renderer2D draw, DashboardController controller, DashboardLayout layout, RenderStats stats) {
        draw.text("H", layout.panelX + 29.0f - draw.textWidth("H", 16.0f) * 0.5f, layout.panelY + 28.0f, 16.0f, TEXT);
        if (layout.sidebarWidth >= 180.0f) {
            draw.text("HYDROGEN", layout.panelX + 64.0f, layout.panelY + 22.0f, 13.0f, TEXT);
            draw.text("DLC  /  NEXT", layout.panelX + 64.0f, layout.panelY + 40.0f, 8.0f, TEXT_DIM);
        }
        draw.text("WORKSPACE", layout.panelX + 20.0f, layout.panelY + 82.0f, 8.0f, TEXT_DIM);

        UiCategory[] categories = UiCategory.values();
        for (int i = 0; i < categories.length; i++) {
            UiCategory category = categories[i];
            float y = layout.navY(i);
            int color = category == controller.category() ? TEXT : TEXT_MUTED;
            draw.text(category.marker(), layout.navX() + 14.0f, y + 13.0f, 8.0f,
                category == controller.category() ? ACCENT_BRIGHT : TEXT_DIM);
            if (layout.sidebarWidth >= 170.0f) draw.text(category.label(), layout.navX() + 42.0f, y + 11.0f, 11.0f, color);
            String count = Integer.toString(controller.count(category));
            draw.text(count, layout.navX() + layout.navWidth() - 12.0f - draw.textWidth(count, 8.0f), y + 14.0f, 8.0f, TEXT_DIM);
        }

        float statusY = layout.panelY + layout.panelHeight - 66.0f;
        draw.text("SYSTEM ONLINE", layout.panelX + 42.0f, statusY + 12.0f, 8.5f, TEXT);
        draw.text("Custom GL pipeline", layout.panelX + 26.0f, statusY + 29.0f, 8.0f, TEXT_DIM);

        draw.text(controller.category().label(), layout.contentX, layout.panelY + 24.0f, 20.0f, TEXT);
        String resultLabel = controller.filtered().size() + " modules";
        draw.text(resultLabel, layout.contentX, layout.panelY + 50.0f, 9.5f, TEXT_MUTED);
        draw.text("Q", layout.searchX + 12.0f, layout.searchY + 11.0f, 10.0f,
            controller.searchFocused() ? ACCENT_BRIGHT : TEXT_DIM);
        String search = controller.query().isEmpty() ? "Search modules" : controller.query();
        int searchColor = controller.query().isEmpty() ? TEXT_DIM : TEXT;
        draw.text(search, layout.searchX + 31.0f, layout.searchY + 10.0f, 10.0f,
            searchColor, layout.searchWidth - 43.0f);
        if (controller.searchFocused()) {
            float caretX = layout.searchX + 31.0f + Math.min(draw.textWidth(controller.query(), 10.0f), layout.searchWidth - 48.0f);
            draw.text("|", caretX + 1.0f, layout.searchY + 9.5f, 10.0f, ACCENT_BRIGHT);
        }

        draw.text(controller.activeCount() + " ACTIVE", layout.contentX + 24.0f, layout.panelY + 74.0f, 8.5f, TEXT_MUTED);
        draw.text("BATCHED UI", layout.contentX + 132.0f, layout.panelY + 74.0f, 8.5f, TEXT_MUTED);

        String performance = stats.drawCalls() + " draws  /  " + oneDecimal(stats.smoothedCpuMilliseconds()) + " ms CPU";
        float performanceWidth = draw.textWidth(performance, 8.0f);
        draw.text(performance, layout.panelX + layout.panelWidth - performanceWidth - 18.0f,
            layout.panelY + layout.panelHeight - 13.0f, 8.0f, TEXT_DIM);
    }

    private void drawInspectorText(Renderer2D draw, DashboardController controller, DashboardLayout layout) {
        if (layout.inspectorWidth <= 0.0f) return;
        ModuleModel selected = controller.selected();
        if (layout.inspectorOverlay && selected == null) return;
        draw.text("INSPECTOR", layout.inspectorX + 18.0f, layout.panelY + 27.0f, 8.0f, TEXT_DIM);
        if (layout.inspectorOverlay) {
            String close = "X";
            draw.text(close, layout.inspectorX + layout.inspectorWidth - 32.0f - draw.textWidth(close, 12.0f) * 0.5f,
                layout.panelY + 25.0f, 12.0f, TEXT_MUTED);
        }
        if (selected == null) {
            draw.text("Select a module", layout.inspectorX + 36.0f, layout.panelY + 120.0f, 12.0f, TEXT);
            draw.text("Settings and details will", layout.inspectorX + 36.0f, layout.panelY + 145.0f, 9.0f, TEXT_MUTED);
            draw.text("appear here.", layout.inspectorX + 36.0f, layout.panelY + 161.0f, 9.0f, TEXT_MUTED);
            return;
        }
        draw.text(selected.name(), layout.inspectorX + 31.0f, layout.panelY + 75.0f, 12.0f, TEXT,
            layout.inspectorWidth - 62.0f);
        draw.text(selected.enabled() ? "ENABLED" : "DISABLED", layout.inspectorX + 31.0f,
            layout.panelY + 93.0f, 8.0f, selected.enabled() ? GREEN : TEXT_DIM);

        float clipY = layout.panelY + 118.0f;
        float clipHeight = layout.panelHeight - 144.0f;
        draw.pushClip(layout.inspectorX + 18.0f, clipY, layout.inspectorWidth - 36.0f, clipHeight);
        float rowY = layout.panelY + 125.0f - controller.inspectorScroll();
        int shown = 0;
        for (SettingModel setting : selected.settings()) {
            if (!setting.visible()) continue;
            float height = DashboardMetrics.settingHeight(setting);
            if (rowY > clipY + clipHeight) break;
            if (rowY + height < clipY) {
                rowY += DashboardMetrics.settingAdvance(setting);
                shown++;
                continue;
            }
            draw.text(setting.name(), layout.inspectorX + 30.0f, rowY + 10.0f, 9.0f, TEXT,
                layout.inspectorWidth - 108.0f);
            if (setting.kind() == SettingModel.Kind.NUMBER) {
                String value = setting.valueText();
                draw.text(value, layout.inspectorX + layout.inspectorWidth - 30.0f - draw.textWidth(value, 8.0f),
                    rowY + 9.0f, 8.0f, ACCENT_BRIGHT);
            } else if (setting.kind() == SettingModel.Kind.CHOICE || setting.kind() == SettingModel.Kind.ACTION
                || setting.kind() == SettingModel.Kind.TEXT || setting.kind() == SettingModel.Kind.KEY_BIND) {
                String value;
                if (setting.kind() == SettingModel.Kind.ACTION) value = "RUN";
                else if (controller.editing(setting) && setting.kind() == SettingModel.Kind.KEY_BIND) value = "PRESS KEY";
                else if (controller.editing(setting) && setting.kind() == SettingModel.Kind.TEXT) value = setting.valueText() + "|";
                else value = setting.valueText();
                draw.text(value, layout.inspectorX + layout.inspectorWidth - 100.0f,
                    rowY + 18.0f, 7.5f, ACCENT_BRIGHT, 66.0f);
            } else if (setting.kind() == SettingModel.Kind.COLOR) {
                String value = setting.valueText();
                draw.text(value, layout.inspectorX + layout.inspectorWidth - 54.0f - draw.textWidth(value, 7.0f),
                    rowY + 13.0f, 7.0f, TEXT_MUTED);
            }
            rowY += DashboardMetrics.settingAdvance(setting);
            shown++;
        }
        draw.popClip();
        if (shown == 0) draw.text("No configurable settings", layout.inspectorX + 30.0f,
            layout.panelY + 140.0f, 9.0f, TEXT_DIM);
    }

    private static String oneDecimal(double value) {
        return Double.toString(Math.round(value * 10.0) / 10.0);
    }

    private static float clamp(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }
}
