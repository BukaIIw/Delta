package hydrogen.ai;

import net.minecraft.client.MinecraftClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Named dataset writer. Each session name becomes {@code <runDir>/hydrogen/ai/<name>.csv}.
 */
public final class AiNamedRecorder {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static String openName;
    private static BufferedWriter writer;
    private static long rows;

    private AiNamedRecorder() {
    }

    public static Path directory(MinecraftClient mc) {
        Path dir = mc.runDirectory.toPath().resolve("hydrogen").resolve("ai");
        try {
            Files.createDirectories(dir);
        } catch (IOException ignored) {
        }
        return dir;
    }

    public static String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "combat";
        }
        String cleaned = name.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
        return cleaned.isEmpty() ? "combat" : cleaned;
    }

    public static void record(MinecraftClient mc, String name, float[] features, float labelYaw, float labelPitch) {
        if (mc == null || features == null) {
            return;
        }
        String key = sanitize(name);
        LOCK.lock();
        try {
            ensure(mc, key);
            writer.write(AiFeatures.row(features, labelYaw, labelPitch));
            writer.newLine();
            rows++;
            if (rows % 32 == 0) {
                writer.flush();
            }
        } catch (IOException ignored) {
        } finally {
            LOCK.unlock();
        }
    }

    public static void flush() {
        LOCK.lock();
        try {
            if (writer != null) {
                writer.flush();
            }
        } catch (IOException ignored) {
        } finally {
            LOCK.unlock();
        }
    }

    public static long rowsWritten() {
        return rows;
    }

    private static void ensure(MinecraftClient mc, String name) throws IOException {
        if (writer != null && name.equals(openName)) {
            return;
        }
        if (writer != null) {
            writer.flush();
            writer.close();
        }
        Path file = directory(mc).resolve(name + ".csv");
        boolean fresh = Files.notExists(file) || Files.size(file) == 0L;
        writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        if (fresh) {
            writer.write(AiFeatures.header());
            writer.newLine();
        }
        openName = name;
    }
}
