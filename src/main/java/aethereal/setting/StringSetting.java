package aethereal.setting;

import aethereal.setting.Setting;
import aethereal.ui.element.StringElement;

import aethereal.ui.element.Element_2;
import lombok.Generated;

public class StringSetting extends Setting<String> {
    private final boolean a;

    @Generated
    public boolean k() {
        return this.a;
    }

    public StringSetting(String name, String defaultVal) {
        super(name, defaultVal);
        this.a = false;
    }

    public StringSetting(String name, String defaultVal, boolean numbers) {
        super(name, defaultVal);
        this.a = numbers;
    }

    @Override
    public Element_2<?> d() {
        return new StringElement(this);
    }
}
