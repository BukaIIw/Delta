package hydrogen.ai;

import net.minecraft.client.MinecraftClient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Named dataset writer. Each session name becomes {@code <runDir>/hydrogen/ai/<name>.csv}.
 */
public final class AiNamedRecorder {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final float DUP_EPS = 0.035f;
    private static String openName;
    private static BufferedWriter writer;
    private static long rows;
    private static float[] lastFeatures;
    private static float lastYaw = Float.NaN;
    private static float lastPitch = Float.NaN;

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
        if (name == null || name.isBlank() || "auto".equalsIgnoreCase(name.trim())) {
            return "auto";
        }
        String cleaned = name.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9._-]", "_");
        return cleaned.isEmpty() ? "auto" : cleaned;
    }

    public static String nextAutoName(MinecraftClient mc) {
        String player = "player";
        if (mc != null && mc.getSession() != null && mc.getSession().getUsername() != null) {
            player = sanitize(mc.getSession().getUsername());
            if ("auto".equals(player)) {
                player = "player";
            }
        }
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return player + "_" + stamp;
    }

    public static boolean isAuto(String name) {
        return name == null || name.isBlank() || "auto".equalsIgnoreCase(name.trim()) || "combat".equalsIgnoreCase(name.trim());
    }

    public static void record(MinecraftClient mc, String name, float[] features, float labelYaw, float labelPitch) {
        if (mc == null || features == null) {
            return;
        }
        String key = sanitize(name);
        LOCK.lock();
        try {
            if (duplicate(features, labelYaw, labelPitch)) {
                return;
            }
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

    public static java.util.List<String> listNames(MinecraftClient mc) {
        java.util.List<String> names = new java.util.ArrayList<>();
        if (mc == null) {
            return names;
        }
        Path dir = directory(mc);
        try (java.nio.file.DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.csv")) {
            for (Path path : stream) {
                String file = path.getFileName().toString();
                if (file.endsWith(".csv")) {
                    names.add(file.substring(0, file.length() - 4));
                }
            }
        } catch (IOException ignored) {
        }
        names.sort(String::compareTo);
        return names;
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
        lastFeatures = null;
        lastYaw = Float.NaN;
        lastPitch = Float.NaN;
    }

    private static boolean duplicate(float[] features, float labelYaw, float labelPitch) {
        if (lastFeatures == null || lastFeatures.length != features.length) {
            return false;
        }
        if (Math.abs(labelYaw - lastYaw) > 0.12f || Math.abs(labelPitch - lastPitch) > 0.12f) {
            return false;
        }
        for (int i = 0; i < features.length; i++) {
            float a = features[i];
            float b = lastFeatures[i];
            float scale = (i == 10 || i == 11 || i == 18 || i == 19) ? 0.12f : DUP_EPS;
            if (Math.abs(a - b) > scale) {
                return false;
            }
        }
        return true;
    }

    private static void remember(float[] features, float labelYaw, float labelPitch) {
        lastFeatures = features.clone();
        lastYaw = labelYaw;
        lastPitch = labelPitch;
    }
}
