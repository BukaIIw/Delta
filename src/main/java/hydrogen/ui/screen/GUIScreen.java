package hydrogen.ui.screen;

import hydrogen.Hydrogen;
import hydrogen.render.RenderEngine;
import hydrogen.render.Renderer2D;
import hydrogen.ui.dashboard.DashboardController;
import hydrogen.ui.dashboard.DashboardLayout;
import hydrogen.ui.dashboard.DashboardRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Thin Minecraft window/input adapter for the standalone Hydrogen dashboard.
 * DrawContext is deliberately unused: all pixels are produced by Hydrogen's
 * own shaders, VAOs, streaming buffers, atlas and batching pipeline.
 */
public class GUIScreen extends Screen {
    private final DashboardController controller;
    private final DashboardLayout layout = new DashboardLayout();
    private final DashboardRenderer dashboard = new DashboardRenderer();
    private long previousFrameNanos;

    public GUIScreen(Text title) {
        super(title);
        Hydrogen hydrogen = Hydrogen.get();
        if (!hydrogen.initialized()) hydrogen.init();
        controller = new DashboardController(hydrogen.moduleRepository());
    }

    @Override
    public void render(DrawContext ignored, int mouseX, int mouseY, float tickDelta) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        layout.compute(width, height);
        long now = System.nanoTime();
        float deltaSeconds = previousFrameNanos == 0L
            ? 1.0f / 60.0f
            : Math.min((now - previousFrameNanos) / 1_000_000_000.0f, 0.05f);
        previousFrameNanos = now;
        controller.update(deltaSeconds, layout, mouseX, mouseY);

        RenderEngine engine = Hydrogen.get().render();
        Renderer2D renderer = engine.beginFrame(
            width,
            height,
            minecraft.getWindow().getFramebufferWidth(),
            minecraft.getWindow().getFramebufferHeight()
        );
        try {
            dashboard.render(renderer, controller, layout, engine.stats());
        } finally {
            engine.endFrame();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (controller.click(layout, (float) mouseX, (float) mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (controller.drag(layout, (float) mouseX, (float) mouseY, button)) return true;
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        controller.release();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (controller.scroll(layout, (float) mouseX, (float) mouseY, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (controller.keyPressed(keyCode)) return true;
        if (keyCode == GLFW.GLFW_KEY_F && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            controller.focusSearch(layout);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE && layout.inspectorOverlay && controller.selected() != null) {
            controller.closeInspector();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && controller.backspace()) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        if (controller.textInputActive()) {
            controller.append(character);
            return true;
        }
        return super.charTyped(character, modifiers);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // The custom full-screen SDF backdrop replaces vanilla's background.
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
