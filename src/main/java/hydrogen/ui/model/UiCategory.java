package hydrogen.ui.model;

public enum UiCategory {
    ALL("Overview", "01"),
    COMBAT("Combat", "02"),
    MOVEMENT("Movement", "03"),
    RENDER("Visuals", "04"),
    PLAYER("Player", "05"),
    MISC("Utility", "06");

    private final String label;
    private final String marker;

    UiCategory(String label, String marker) {
        this.label = label;
        this.marker = marker;
    }

    public String label() { return label; }
    public String marker() { return marker; }
}
