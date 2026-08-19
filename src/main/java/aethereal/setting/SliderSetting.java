package aethereal.setting;

import aethereal.setting.Setting;
import aethereal.ui.element.SliderElement;

import aethereal.ui.element.Element_2;

public class SliderSetting extends Setting<Float> {
    public float a;
    public float b;
    public float c;
    public float d;
    public boolean e;

    public SliderSetting(String name, float defaultVal, float min, float max, float increment) {
        this(name, defaultVal, min, max, increment, false);
    }

    public SliderSetting(String name, float defaultVal, float min, float max, float increment, boolean scroll) {
        super(name, Float.valueOf(defaultVal));
        this.a = min;
        this.b = max;
        this.d = defaultVal;
        this.c = increment;
        this.e = scroll;
    }

    @Override
    public Element_2<?> d() {
        return new SliderElement(this);
    }
}
