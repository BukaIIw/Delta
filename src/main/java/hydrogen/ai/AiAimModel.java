package hydrogen.ai;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Tiny k-NN over a named CSV. This is classical ML, not an LLM —
 * the "AI" label is only the product name of the aim mode.
 */
public final class AiAimModel {
    private static final int K = 8;
    private static String loadedName;
    private static final List<float[]> samples = new ArrayList<>();

    private AiAimModel() {
    }

    public static String loadedName() {
        return loadedName == null ? "" : loadedName;
    }

    public static void reload(MinecraftClient mc, String name) {
        loadedName = null;
        samples.clear();
        ensureLoaded(mc, name);
    }

    public static void ensureLoaded(MinecraftClient mc, String name) {
        String key = AiNamedRecorder.sanitize(name);
        if (key.equals(loadedName) && !samples.isEmpty()) {
            return;
        }
        samples.clear();
        loadedName = key;
        Path file = AiNamedRecorder.directory(mc).resolve(key + ".csv");
        if (!Files.isRegularFile(file)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int need = AiFeatures.COLUMNS.length + 2;
                if (parts.length < need) {
                    continue;
                }
                float[] row = new float[need];
                boolean ok = true;
                for (int i = 0; i < need; i++) {
                    try {
                        row[i] = Float.parseFloat(parts[i]);
                    } catch (NumberFormatException ex) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    samples.add(row);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static float[] predict(float[] query) {
        if (samples.isEmpty() || query == null) {
            return new float[]{0f, 0f};
        }
        int n = samples.size();
        int take = Math.min(K, n);
        float[] bestDist = new float[take];
        int[] bestIdx = new int[take];
        java.util.Arrays.fill(bestDist, Float.POSITIVE_INFINITY);
        java.util.Arrays.fill(bestIdx, -1);
        for (int i = 0; i < n; i++) {
            float d = distance(query, samples.get(i));
            int slot = -1;
            float worst = -1f;
            for (int k = 0; k < take; k++) {
                if (bestDist[k] > worst) {
                    worst = bestDist[k];
                    slot = k;
                }
            }
            if (slot >= 0 && d < bestDist[slot]) {
                bestDist[slot] = d;
                bestIdx[slot] = i;
            }
        }
        float wsum = 0f;
        float yaw = 0f;
        float pitch = 0f;
        int cols = AiFeatures.COLUMNS.length;
        for (int k = 0; k < take; k++) {
            if (bestIdx[k] < 0) {
                continue;
            }
            float w = 1f / (bestDist[k] + 1e-3f);
            float[] s = samples.get(bestIdx[k]);
            yaw += s[cols] * w;
            pitch += s[cols + 1] * w;
            wsum += w;
        }
        if (wsum <= 0f) {
            return new float[]{0f, 0f};
        }
        return new float[]{yaw / wsum, pitch / wsum};
    }

    public static int sampleCount() {
        return samples.size();
    }

    private static float distance(float[] q, float[] s) {
        float acc = 0f;
        int n = Math.min(q.length, AiFeatures.COLUMNS.length);
        for (int i = 0; i < n; i++) {
            float a = q[i];
            float b = s[i];
            if (i == 10 || i == 11) {
                a = MathHelper.wrapDegrees(a);
                b = MathHelper.wrapDegrees(b);
            }
            float d = a - b;
            acc += d * d;
        }
        return acc;
    }
}
