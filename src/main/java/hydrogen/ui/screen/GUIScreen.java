package hydrogen.ui.screen;

import hydrogen.api.Compile;
import hydrogen.core.Category;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Interface;
import hydrogen.core.Module;
import hydrogen.core.NativeMethodLookup;
import hydrogen.render.EasingList;
import hydrogen.render.ScaleUtil;
import hydrogen.util.MathUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Original Hydrogen clickgui: blurred world + category columns.
 */
public class GUIScreen extends Screen {
    private static final float PANEL_W = 125.0f;
    private static final float PANEL_H = 270.0f;
    private static final float GAP = 6.0f;

    private final List<GUIPanel> panels = new ArrayList<>();
    private GUIPanel dragging;
    private float dragOffX;
    private float dragOffY;

    static {
        NativeMethodLookup.lookup(GUIScreen.class, 5);
    }

    public GUIScreen(Text title) {
        super(title);
        for (Category category : Category.values()) {
            this.panels.add(new GUIPanel(category));
        }
        layoutDefaults();
        refreshModules();
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Compile
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (HydrogenClient.h() == null || HydrogenClient.h().d() == null) {
            return;
        }
        refreshModules();
        ScaleUtil.a(context, 2);
        int mx = (int) MathUtil.scale(mouseX, 2);
        int my = (int) MathUtil.scale(mouseY, 2);
        HydrogenClient.h().d().i().e().a(context.getMatrices());
        boolean open = Interface.aM_.currentScreen instanceof GUIScreen;
        for (GUIPanel panel : this.panels) {
            panel.b().a(open);
            panel.b().a(0.0f, 1.0f, 0.15f, EasingList.g, delta);
            panel.a(context, mx, my, delta);
        }
        for (GUIPanel panel : this.panels) {
            panel.a(context, (double) mx, (double) my, delta);
        }
        ScaleUtil.a(context);
    }

    @Compile
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mx = MathUtil.scale(mouseX, 2);
        double my = MathUtil.scale(mouseY, 2);
        for (int i = this.panels.size() - 1; i >= 0; i--) {
            GUIPanel panel = this.panels.get(i);
            Vector4f box = panel.f();
            if (button == 0 && MathUtil.a(mx, my, box.x, box.y, box.z, 24.0f)) {
                this.dragging = panel;
                this.dragOffX = (float) (mx - box.x);
                this.dragOffY = (float) (my - box.y);
                this.panels.remove(i);
                this.panels.add(panel);
                return true;
            }
            if (panel.a(mx, my, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Compile
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        double mx = MathUtil.scale(mouseX, 2);
        double my = MathUtil.scale(mouseY, 2);
        this.dragging = null;
        for (GUIPanel panel : this.panels) {
            if (panel.b(mx, my, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Compile
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        double mx = MathUtil.scale(mouseX, 2);
        double my = MathUtil.scale(mouseY, 2);
        if (this.dragging != null && button == 0) {
            this.dragging.f().x = (float) (mx - this.dragOffX);
            this.dragging.f().y = (float) (my - this.dragOffY);
            return true;
        }
        for (GUIPanel panel : this.panels) {
            if (panel.a(mx, my, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Compile
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double mx = MathUtil.scale(mouseX, 2);
        double my = MathUtil.scale(mouseY, 2);
        for (int i = this.panels.size() - 1; i >= 0; i--) {
            if (this.panels.get(i).a(mx, my, verticalAmount)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Compile
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (GUIPanel panel : this.panels) {
            if (panel.a(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Compile
    @Override
    public boolean charTyped(char character, int modifiers) {
        for (GUIPanel panel : this.panels) {
            if (panel.a(character, modifiers)) {
                return true;
            }
        }
        return super.charTyped(character, modifiers);
    }

    @Override
    public void close() {
        super.close();
        for (GUIPanel panel : this.panels) {
            panel.b().c(0.0f);
        }
    }

    private void layoutDefaults() {
        float total = (Category.values().length * PANEL_W) + ((Category.values().length - 1) * GAP);
        float width = 480.0f;
        float height = 270.0f;
        if (Interface.aM_ != null && Interface.aM_.getWindow() != null) {
            width = Interface.aM_.getWindow().getScaledWidth();
            height = Interface.aM_.getWindow().getScaledHeight();
        }
        float startX = (width - total) * 0.5f;
        float y = Math.max(18.0f, (height - PANEL_H) * 0.5f);
        Category[] cats = Category.values();
        for (int i = 0; i < this.panels.size(); i++) {
            Vector4f box = this.panels.get(i).f();
            box.x = startX + (i * (PANEL_W + GAP));
            box.y = y;
            box.z = PANEL_W;
            box.w = PANEL_H;
            if (i < cats.length && this.panels.get(i).c() != cats[i]) {
                // keep existing category order
            }
        }
    }

    private void refreshModules() {
        if (HydrogenClient.h() == null || HydrogenClient.h().d() == null || HydrogenClient.h().d().t() == null) {
            return;
        }
        List<Module> all = HydrogenClient.h().d().t().e();
        for (GUIPanel panel : this.panels) {
            panel.a(all.stream().filter(module -> module.l() == panel.c()).collect(Collectors.toList()));
        }
    }
}
