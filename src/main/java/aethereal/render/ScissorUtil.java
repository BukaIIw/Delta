package aethereal.render;

import static aethereal.core.Interface.aM_;

import aethereal.core.Interface;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayDeque;
import java.util.Deque;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class ScissorUtil implements Interface {
    private static final Deque<a> b = new ArrayDeque();
    private static final Matrix4f c = new Matrix4f();

    @Generated
    private ScissorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void a(MatrixStack matrixStack, float x, float y, float width, float height) {
        float scaleFactor = (float) aM_.getWindow().getScaleFactor();
        a scissorBox = new a((int) (x * scaleFactor), (int) (((aM_.getWindow().getScaledHeight() - y) - height) * scaleFactor), (int) (width * scaleFactor), (int) (height * scaleFactor));
        if (!b.isEmpty()) {
            scissorBox = scissorBox.a(b.peek());
        }
        b.push(scissorBox);
        matrixStack.push();
        a(scissorBox);
    }

    public static void a(MatrixStack matrixStack) {
        b.pop();
        if (b.isEmpty()) {
            RenderSystem.disableScissor();
        } else {
            a(b.peek());
        }
        matrixStack.pop();
    }

    private static void a(a box) {
        RenderSystem.enableScissor(box.a, box.b, box.c, box.d);
    }

    static final class a {
        final int a;
        final int b;
        final int c;
        final int d;

        a(int x, int y, int w, int h) {
            this.a = x;
            this.b = y;
            this.c = w;
            this.d = h;
        }
public int a() {
            return this.a;
        }

        public int b() {
            return this.b;
        }

        public int c() {
            return this.c;
        }

        public int d() {
            return this.d;
        }

        a a(a p) {
            int nx = Math.max(this.a, p.a);
            int ny = Math.max(this.b, p.b);
            return new a(nx, ny, Math.max(0, Math.min(this.a + this.c, p.a + p.c) - nx), Math.max(0, Math.min(this.b + this.d, p.b + p.d) - ny));
        }
    }
}
