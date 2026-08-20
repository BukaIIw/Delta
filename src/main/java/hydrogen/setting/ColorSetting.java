package hydrogen.setting;

import hydrogen.setting.Setting;
import hydrogen.ui.element.ColorElement;

import hydrogen.ui.element.Element_2;

public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String name, Integer defaultVal) {
        super(name, defaultVal);
    }

    @Override
    public Element_2<?> d() {
        return new ColorElement(this);
    }
}
