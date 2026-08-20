package hydrogen.integration;

import hydrogen.core.Category;
import hydrogen.core.HydrogenClient;
import hydrogen.setting.BindSetting;
import hydrogen.setting.BooleanSetting;
import hydrogen.setting.ButtonSetting;
import hydrogen.setting.ColorSetting;
import hydrogen.setting.ModeSetting;
import hydrogen.setting.MultiModeSetting;
import hydrogen.setting.Setting;
import hydrogen.setting.SliderSetting;
import hydrogen.setting.StringSetting;
import hydrogen.util.KeyUtil;
import hydrogen.ui.model.ModuleModel;
import hydrogen.ui.model.ModuleRepository;
import hydrogen.ui.model.SettingModel;
import hydrogen.ui.model.UiCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/** Anti-corruption layer between the gameplay module core and the Hydrogen UI. */
public final class ClientModuleRepository implements ModuleRepository {
    private final List<ModuleModel> modules;

    public ClientModuleRepository(HydrogenClient client) {
        List<ModuleModel> adapted = new ArrayList<>();
        if (client != null && client.d() != null && client.d().t() != null) {
            for (hydrogen.core.Module module : client.d().t().e()) {
                adapted.add(new ModuleAdapter(module));
            }
        }
        modules = Collections.unmodifiableList(adapted);
    }

    @Override
    public List<ModuleModel> modules() {
        return modules;
    }

    private static final class ModuleAdapter implements ModuleModel {
        private final hydrogen.core.Module delegate;
        private final String id;
        private final UiCategory category;
        private final List<SettingModel> settings;

        private ModuleAdapter(hydrogen.core.Module delegate) {
            this.delegate = delegate;
            category = map(delegate.l());
            id = category.name().toLowerCase(Locale.ROOT) + "." + slug(delegate.j());
            List<SettingModel> result = new ArrayList<>();
            for (Setting<?> setting : delegate.e()) {
                if (setting instanceof MultiModeSetting group) {
                    for (BooleanSetting option : group.c()) {
                        result.add(new SettingAdapter(option, setting.i() + " / " + option.i(), setting));
                    }
                } else {
                    result.add(new SettingAdapter(setting));
                }
            }
            settings = Collections.unmodifiableList(result);
        }

        @Override public String id() { return id; }
        @Override public String name() { return delegate.j(); }
        @Override public String description() { return delegate.k() == null ? "" : delegate.k(); }
        @Override public UiCategory category() { return category; }
        @Override public boolean enabled() { return delegate.m(); }
        @Override public void toggle() { delegate.a(); }
        @Override public List<SettingModel> settings() { return settings; }
    }

    private static final class SettingAdapter implements SettingModel {
        private final Setting<?> delegate;
        private final Setting<?> parent;
        private final String name;

        private SettingAdapter(Setting<?> delegate) {
            this(delegate, delegate.i(), null);
        }

        private SettingAdapter(Setting<?> delegate, String name, Setting<?> parent) {
            this.delegate = delegate;
            this.parent = parent;
            this.name = name;
        }

        @Override public String name() { return name; }

        @Override
        public String valueText() {
            Object value = delegate.c();
            if (delegate instanceof BooleanSetting) return Boolean.TRUE.equals(value) ? "On" : "Off";
            if (delegate instanceof BindSetting && value instanceof Number number) {
                return number.intValue() == -1 ? "None" : KeyUtil.b(number.intValue());
            }
            if (delegate instanceof SliderSetting) {
                float number = ((Number) value).floatValue();
                float rounded = Math.round(number * 100.0f) / 100.0f;
                return Float.toString(rounded);
            }
            if (delegate instanceof ColorSetting && value instanceof Number number) {
                return String.format(Locale.ROOT, "#%06X", number.intValue() & 0xFFFFFF);
            }
            return value == null ? "-" : String.valueOf(value);
        }

        @Override
        public Kind kind() {
            if (delegate instanceof BooleanSetting) return Kind.BOOLEAN;
            if (delegate instanceof SliderSetting) return Kind.NUMBER;
            if (delegate instanceof ModeSetting) return Kind.CHOICE;
            if (delegate instanceof ColorSetting) return Kind.COLOR;
            if (delegate instanceof StringSetting) return Kind.TEXT;
            if (delegate instanceof BindSetting) return Kind.KEY_BIND;
            if (delegate instanceof ButtonSetting) return Kind.ACTION;
            return Kind.OTHER;
        }

        @Override
        public boolean visible() {
            try {
                boolean ownVisibility = delegate.j() && Boolean.TRUE.equals(delegate.e().get());
                boolean parentVisibility = parent == null || (parent.j() && Boolean.TRUE.equals(parent.e().get()));
                return ownVisibility && parentVisibility;
            } catch (RuntimeException ignored) {
                return false;
            }
        }

        @Override
        public float normalizedValue() {
            if (delegate instanceof SliderSetting slider) {
                float range = slider.b - slider.a;
                return range <= 0.0f ? 0.0f : (slider.c() - slider.a) / range;
            }
            if (delegate instanceof ColorSetting colorSetting) {
                int color = colorSetting.c();
                return java.awt.Color.RGBtoHSB((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF, null)[0];
            }
            return 0.0f;
        }

        @Override
        public void setNormalizedValue(float value) {
            float normalized = Math.max(0.0f, Math.min(1.0f, value));
            if (delegate instanceof SliderSetting slider) {
                float raw = slider.a + (slider.b - slider.a) * normalized;
                float step = Math.max(slider.c, 0.00001f);
                float snapped = slider.a + Math.round((raw - slider.a) / step) * step;
                slider.a(Math.max(slider.a, Math.min(slider.b, snapped)));
            } else if (delegate instanceof ColorSetting colorSetting) {
                int current = colorSetting.c();
                float[] hsb = colorHsb(current);
                colorSetting.a(withHsb(current, normalized, hsb[1], hsb[2]));
            }
        }

        @Override
        public int colorArgb() {
            return delegate instanceof ColorSetting colorSetting ? colorSetting.c() : SettingModel.super.colorArgb();
        }

        @Override
        public float colorSaturation() {
            return delegate instanceof ColorSetting colorSetting ? colorHsb(colorSetting.c())[1] : 0.0f;
        }

        @Override
        public float colorBrightness() {
            return delegate instanceof ColorSetting colorSetting ? colorHsb(colorSetting.c())[2] : 0.0f;
        }

        @Override
        public float colorAlpha() {
            return delegate instanceof ColorSetting colorSetting ? ((colorSetting.c() >>> 24) & 0xFF) / 255.0f : 1.0f;
        }

        @Override
        public void setColorSaturationBrightness(float saturation, float brightness) {
            if (!(delegate instanceof ColorSetting colorSetting)) return;
            int current = colorSetting.c();
            float hue = colorHsb(current)[0];
            colorSetting.a(withHsb(current, hue, clamp01(saturation), clamp01(brightness)));
        }

        @Override
        public void setColorAlpha(float alpha) {
            if (!(delegate instanceof ColorSetting colorSetting)) return;
            int current = colorSetting.c();
            colorSetting.a((Math.round(clamp01(alpha) * 255.0f) << 24) | (current & 0x00FFFFFF));
        }

        @Override
        public void activate() {
            if (delegate instanceof BooleanSetting booleanSetting) {
                booleanSetting.a(!booleanSetting.c());
            } else if (delegate instanceof ModeSetting modeSetting) {
                List<String> choices = modeSetting.k();
                if (!choices.isEmpty()) {
                    int current = choices.indexOf(modeSetting.c());
                    modeSetting.a(choices.get((current + 1 + choices.size()) % choices.size()));
                }
            } else if (delegate instanceof ButtonSetting buttonSetting) {
                buttonSetting.k();
            }
        }

        @Override
        public void appendCharacter(char character) {
            if (!(delegate instanceof StringSetting stringSetting) || Character.isISOControl(character)) return;
            if (stringSetting.k() && !Character.isDigit(character)) return;
            String current = stringSetting.c() == null ? "" : stringSetting.c();
            stringSetting.a(current + character);
        }

        @Override
        public boolean backspaceValue() {
            if (!(delegate instanceof StringSetting stringSetting)) return false;
            String current = stringSetting.c() == null ? "" : stringSetting.c();
            if (current.isEmpty()) return true;
            int codePoint = current.codePointBefore(current.length());
            stringSetting.a(current.substring(0, current.length() - Character.charCount(codePoint)));
            return true;
        }

        @Override
        public void setKeyCode(int keyCode) {
            if (delegate instanceof BindSetting bindSetting) bindSetting.a(keyCode);
        }
    }

    private static float[] colorHsb(int color) {
        return java.awt.Color.RGBtoHSB((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF, null);
    }

    private static int withHsb(int current, float hue, float saturation, float brightness) {
        int rgb = java.awt.Color.HSBtoRGB(clamp01(hue), clamp01(saturation), clamp01(brightness));
        return (current & 0xFF000000) | (rgb & 0x00FFFFFF);
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static UiCategory map(Category category) {
        if (category == null) return UiCategory.MISC;
        return switch (category) {
            case Combat -> UiCategory.COMBAT;
            case Movement -> UiCategory.MOVEMENT;
            case Render -> UiCategory.RENDER;
            case Player -> UiCategory.PLAYER;
            case Misc -> UiCategory.MISC;
        };
    }

    private static String slug(String value) {
        StringBuilder result = new StringBuilder(value.length());
        boolean separator = false;
        for (int i = 0; i < value.length(); i++) {
            char character = Character.toLowerCase(value.charAt(i));
            if (Character.isLetterOrDigit(character)) {
                result.append(character);
                separator = false;
            } else if (!separator && !result.isEmpty()) {
                result.append('-');
                separator = true;
            }
        }
        if (!result.isEmpty() && result.charAt(result.length() - 1) == '-') result.setLength(result.length() - 1);
        return result.toString();
    }
}
