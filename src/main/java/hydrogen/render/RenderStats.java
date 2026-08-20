package hydrogen.render;

/** Per-frame instrumentation for the standalone renderer. */
public final class RenderStats {
    private int frameDrawCalls;
    private int frameShapeInstances;
    private int frameGlyphs;
    private long frameUploadedBytes;
    private int completedDrawCalls;
    private int completedShapeInstances;
    private int completedGlyphs;
    private long completedUploadedBytes;
    private double completedCpuMilliseconds;
    private double smoothedCpuMilliseconds;

    /** Starts an unpublished frame; accessors continue to expose the last completed frame. */
    public void reset() {
        frameDrawCalls = 0;
        frameShapeInstances = 0;
        frameGlyphs = 0;
        frameUploadedBytes = 0L;
    }

    public void draw(int instances, int bytes) {
        frameDrawCalls++;
        frameShapeInstances += instances;
        frameUploadedBytes += bytes;
    }

    public void textDraw(int glyphCount, int bytes) {
        frameDrawCalls++;
        frameGlyphs += glyphCount;
        frameUploadedBytes += bytes;
    }

    /** Publishes one exact snapshot only after every pending batch has flushed. */
    public void finish(long startedNanos) {
        completedDrawCalls = frameDrawCalls;
        completedShapeInstances = frameShapeInstances;
        completedGlyphs = frameGlyphs;
        completedUploadedBytes = frameUploadedBytes;
        completedCpuMilliseconds = (System.nanoTime() - startedNanos) / 1_000_000.0;
        smoothedCpuMilliseconds += (completedCpuMilliseconds - smoothedCpuMilliseconds) * 0.08;
    }

    public int drawCalls() { return completedDrawCalls; }
    public int shapeInstances() { return completedShapeInstances; }
    public int glyphs() { return completedGlyphs; }
    public long uploadedBytes() { return completedUploadedBytes; }
    public double cpuMilliseconds() { return completedCpuMilliseconds; }
    public double smoothedCpuMilliseconds() { return smoothedCpuMilliseconds; }
}
