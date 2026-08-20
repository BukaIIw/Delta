package hydrogen.render;

import static hydrogen.core.Interface.aM_;

import hydrogen.core.Interface;

import lombok.Generated;
import net.minecraft.client.util.Window;
import net.minecraft.client.gui.DrawContext;

public class ScaleUtil implements Interface {
    @Generated
    private ScaleUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void a(DrawContext context, int scale) {
        Window window = aM_.getWindow();
        double previous = window.getScaleFactor();
        double target = window.calculateScaleFactor(scale, aM_.forcesUnicodeFont());
        window.setScaleFactor(target);
        context.getMatrices().push();
        context.getMatrices().scale((float) (target / previous), (float) (target / previous), 1.0f);
    }

    public static void a(DrawContext context) {
        context.getMatrices().pop();
        aM_.getWindow().setScaleFactor(aM_.getWindow().calculateScaleFactor(((Integer) aM_.options.getGuiScale().getValue()).intValue(), aM_.forcesUnicodeFont()));
    }

    public static void b(DrawContext context) {
        a(context, ((Integer) aM_.options.getGuiScale().getValue()).intValue());
    }

    public static void c(DrawContext context) {
        context.getMatrices().pop();
        aM_.getWindow().setScaleFactor(aM_.getWindow().calculateScaleFactor(2, aM_.forcesUnicodeFont()));
    }
}
