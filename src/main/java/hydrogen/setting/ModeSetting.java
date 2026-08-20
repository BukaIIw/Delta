package hydrogen.setting;

import hydrogen.setting.Setting;
import hydrogen.ui.element.ModeElement;

import hydrogen.ui.element.Element_2;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;

public class ModeSetting extends Setting<String> {
    private final List<String> a;

    @Generated
    public List<String> k() {
        return this.a;
    }

    public ModeSetting(String name, String defaultVal, String... strings) {
        super(name, defaultVal);
        this.a = Arrays.asList(strings);
    }

    public boolean l(String settingName) {
        return c().equalsIgnoreCase(settingName);
    }

    @Override
    public Element_2<?> d() {
        return new ModeElement(this);
    }
}
