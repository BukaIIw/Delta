package hydrogen.render;

import org.joml.Matrix4f;

public final class RenderContext {
    private final int width;
    private final int height;
    private final float partialTick;
    private final Matrix4f projection;
    private final Matrix4f view;
    private final Matrix4f model;

    public RenderContext(int width, int height, Matrix4f projection, Matrix4f view, float partialTick) {
        this.width = width;
        this.height = height;
        this.partialTick = partialTick;
        this.projection = new Matrix4f(projection);
        this.view = new Matrix4f(view);
        this.model = new Matrix4f();
    }

    public int width() { return width; }
    public int height() { return height; }
    public float partialTick() { return partialTick; }
    public Matrix4f projection() { return projection; }
    public Matrix4f view() { return view; }
    public Matrix4f model() { return model; }
}
