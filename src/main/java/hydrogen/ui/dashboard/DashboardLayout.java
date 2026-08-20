package hydrogen.ui.dashboard;

/** Responsive geometry shared by drawing and hit testing. */
public final class DashboardLayout {
    public float screenWidth;
    public float screenHeight;
    public float panelX;
    public float panelY;
    public float panelWidth;
    public float panelHeight;
    public float sidebarWidth;
    public float contentX;
    public float contentWidth;
    public float bodyY;
    public float bodyHeight;
    public float inspectorX;
    public float inspectorWidth;
    public boolean inspectorOverlay;
    public float searchX;
    public float searchY;
    public float searchWidth;
    public float searchHeight;
    public int columns;
    public float cardWidth;

    public static final float CARD_HEIGHT = 82.0f;
    public static final float CARD_GAP = 12.0f;
    public static final float NAV_Y_OFFSET = 108.0f;
    public static final float NAV_HEIGHT = 38.0f;
    public static final float NAV_GAP = 5.0f;

    public void compute(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        panelWidth = Math.min(1180.0f, Math.max(640.0f, screenWidth - 48.0f));
        panelHeight = Math.min(720.0f, Math.max(430.0f, screenHeight - 40.0f));
        panelWidth = Math.min(panelWidth, screenWidth - 24.0f);
        panelHeight = Math.min(panelHeight, screenHeight - 20.0f);
        panelX = (screenWidth - panelWidth) * 0.5f;
        panelY = (screenHeight - panelHeight) * 0.5f;

        sidebarWidth = panelWidth < 790.0f ? 154.0f : 194.0f;
        inspectorOverlay = panelWidth < 900.0f;
        inspectorWidth = inspectorOverlay
            ? Math.min(300.0f, Math.max(210.0f, panelWidth - sidebarWidth - 24.0f))
            : 250.0f;
        float rightPadding = 22.0f;
        inspectorX = panelX + panelWidth - inspectorWidth;
        contentX = panelX + sidebarWidth + 22.0f;
        float contentRight = inspectorOverlay ? panelX + panelWidth - rightPadding : inspectorX - 20.0f;
        contentWidth = contentRight - contentX;

        searchWidth = Math.min(238.0f, Math.max(156.0f, contentWidth * 0.36f));
        searchHeight = 34.0f;
        searchX = contentX + contentWidth - searchWidth;
        searchY = panelY + 24.0f;
        bodyY = panelY + 105.0f;
        bodyHeight = panelY + panelHeight - 22.0f - bodyY;
        columns = contentWidth >= 500.0f ? 2 : 1;
        cardWidth = (contentWidth - (columns - 1) * CARD_GAP) / columns;
    }

    public float navX() { return panelX + 14.0f; }
    public float navY(int index) { return panelY + NAV_Y_OFFSET + index * (NAV_HEIGHT + NAV_GAP); }
    public float navWidth() { return sidebarWidth - 28.0f; }
    public float cardX(int index) { return contentX + (index % columns) * (cardWidth + CARD_GAP); }
    public float cardY(int index, float scroll) {
        return bodyY + (index / columns) * (CARD_HEIGHT + CARD_GAP) - scroll;
    }

    public boolean contains(float x, float y, float left, float top, float width, float height) {
        return x >= left && y >= top && x <= left + width && y <= top + height;
    }
}
