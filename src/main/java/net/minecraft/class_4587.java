package net.minecraft;

import net.minecraft.client.util.math.MatrixStack;

public final class class_4587 {
    private final MatrixStack delegate;

    public class_4587(MatrixStack delegate) {
        this.delegate = delegate;
    }

    public MatrixStack toMatrixStack() {
        return delegate;
    }

    public static class_4587 of(MatrixStack matrices) {
        return matrices == null ? null : new class_4587(matrices);
    }
}
