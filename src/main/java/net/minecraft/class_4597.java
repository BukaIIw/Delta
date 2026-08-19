package net.minecraft;

import net.minecraft.client.render.VertexConsumerProvider;

public final class class_4597 {
    private final VertexConsumerProvider delegate;

    public class_4597(VertexConsumerProvider delegate) {
        this.delegate = delegate;
    }

    public VertexConsumerProvider toVertexConsumerProvider() {
        return delegate;
    }

    public static class_4597 of(VertexConsumerProvider buffers) {
        return buffers == null ? null : new class_4597(buffers);
    }
}
