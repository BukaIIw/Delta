package hydrogen.ui.model;

/** Mutable setting contract used by the dashboard without game-specific UI types. */
public interface SettingModel {
    enum Kind { BOOLEAN, NUMBER, CHOICE, COLOR, TEXT, KEY_BIND, ACTION, OTHER }

    String name();
    String valueText();
    Kind kind();
    boolean visible();
    float normalizedValue();
    void setNormalizedValue(float value);

    default int colorArgb() { return 0xFFFFFFFF; }
    default float colorSaturation() { return 0.0f; }
    default float colorBrightness() { return 0.0f; }
    default float colorAlpha() { return 1.0f; }
    default void setColorSaturationBrightness(float saturation, float brightness) {}
    default void setColorAlpha(float alpha) {}

    void activate();

    default void appendCharacter(char character) {
    }

    default boolean backspaceValue() {
        return false;
    }

    default void setKeyCode(int keyCode) {
    }
}
