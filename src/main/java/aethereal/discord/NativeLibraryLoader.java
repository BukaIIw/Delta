package aethereal.discord;


import aethereal.util.BooleanUtils;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.security.CodeSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.jar.JarEntry;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

public class NativeLibraryLoader {
    private static final String PROP_LOG = "aethereal.loader.log";
    private static final String PROP_DIR = "aethereal.loader.dir";
    private static final String PROP_PURGE = "aethereal.loader.purge";
    private static final AtomicLong STAGING_SEQ = new AtomicLong();

    interface Source {
        String identity();

        long size();

        InputStream open() throws IOException;

        String origin();
    }

    private NativeLibraryLoader() {
    }

    public static void loadAbsolute(String path) {
        Trace t = new Trace();
        t.log("mode=absolute");
        t.log("path=" + path);
        Path p = Paths.get(path, new String[0]);
        t.log("present=" + Files.isRegularFile(p, new LinkOption[0]) + " size=" + sizeOf(p));
        load(path, t);
    }

    public static void loadBundled(String resource, String buildId) {
        Trace t = new Trace();
        t.log("mode=bundled");
        t.log("resource=" + resource + " buildId=" + buildId);
        Path dir = extractDir(t);
        try {
            Path lib = materialize(jarSource(resource, buildId, t), resource, dir, t);
            load(lib.toAbsolutePath().toString(), t);
        } catch (Error | RuntimeException e) {
            t.flush();
            throw e;
        }
    }

    public static String identity(long size, long crc) {
        return Long.toHexString(size) + "-" + Long.toHexString(crc & 4294967295L);
    }

    static Path materialize(Source src, String resourceName, Path dir, Trace t) {
        String name = versioned(resourceName, src.identity());
        Path target = dir.resolve(name);
        t.log("target=" + target);
        if (usable(target, src.size())) {
            t.log("already extracted (" + sizeOf(target) + " bytes) - nothing written");
            return target;
        }
        long jPid = ProcessHandle.current().pid();
        STAGING_SEQ.incrementAndGet();
        Path staged = dir.resolve(name + "." + jPid + "." + dir + ".tmp");
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
            InputStream in = src.open();
            try {
                long n = Files.copy(in, staged, StandardCopyOption.REPLACE_EXISTING);
                String strOrigin = src.origin();
                staged.getFileName();
                t.log("staged " + n + " bytes from " + t + " -> " + strOrigin);
                if (in != null) {
                    in.close();
                }
                Files.move(staged, target, new CopyOption[0]);
                t.log("published");
            } catch (Throwable th) {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (FileAlreadyExistsException e) {
            if (usable(target, src.size())) {
                t.log("another process published it first - using that copy");
                delete(staged);
            } else {
                t.log("a file of the wrong size holds the name - loading the staged copy instead");
                return staged;
            }
        } catch (Throwable e2) {
            t.log("extraction failed: " + e2);
            delete(staged);
            if (!Files.isRegularFile(target, new LinkOption[0])) {
                IllegalStateException fail = new IllegalStateException("aethereal: cannot extract native library '" + resourceName + "' into " + dir + t.tail());
                fail.initCause(e2);
                throw fail;
            }
            t.log("a copy is on disk - loading that one");
        }
        purge(dir, resourceName, target, t);
        return target;
    }

    static String versioned(String resourceName, String identity) {
        int slash = Math.max(resourceName.lastIndexOf(47), resourceName.lastIndexOf(92));
        String file = resourceName.substring(slash + 1);
        int dot = file.lastIndexOf(46);
        return dot < 0 ? file + "-" + identity : file.substring(0, dot) + "-" + identity + file.substring(dot);
    }

    private static boolean usable(Path target, long expectedSize) {
        try {
            return Files.isRegularFile(target, new LinkOption[0]) && (expectedSize < 0 || Files.size(target) == expectedSize);
        } catch (IOException e) {
            return false;
        }
    }

    private static void purge(Path dir, String resourceName, Path keep, Trace t) {
        if (!Boolean.parseBoolean(System.getProperty(PROP_PURGE))) {
            return;
        }
        int dot = resourceName.lastIndexOf(46);
        String base = dot < 0 ? resourceName : resourceName.substring(0, dot);
        String ext = dot < 0 ? "" : resourceName.substring(dot);
        try {
            Stream<Path> list = Files.list(dir);
            try {
                Objects.requireNonNull(list);
                Iterable<Path> iterable = list::iterator;
                for (Path p : iterable) {
                    String n = p.getFileName().toString();
                    if (!p.equals(keep) && n.startsWith(base + "-") && n.endsWith(ext) && Files.isRegularFile(p, new LinkOption[0])) {
                        t.log("purge " + n + " -> " + Files.deleteIfExists(p));
                    }
                }
                if (list != null) {
                    list.close();
                }
            } catch (Throwable th) {
                if (list != null) {
                    try {
                        list.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Throwable e) {
            t.log("purge skipped: " + e);
        }
    }

    private static Source jarSource(String resource, String buildId, Trace t) {
        Path jar = codeSourceJar(t);
        if (jar != null) {
            try {
                java.util.zip.ZipFile zf = new java.util.zip.ZipFile(jar.toFile());
                try {
                    ZipEntry e = zf.getEntry(resource);
                    if (e == null) {
                        t.log("no entry '" + resource + "' in " + jar);
                    } else if (e.getSize() < 0 || e.getCrc() < 0) {
                        t.log("entry '" + resource + "' has no size/crc in the directory");
                    } else {
                        Source sourceReport = report(new ZipSource(jar, resource, e.getSize(), identity(e.getSize(), e.getCrc())), buildId, t);
                        zf.close();
                        return sourceReport;
                    }
                    zf.close();
                } catch (Throwable th) {
                    try {
                        zf.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (Throwable e2) {
                t.log("cannot read own jar " + jar + ": " + e2);
            }
        }
        URL url = NativeLibraryLoader.class.getResource("/" + resource);
        t.log("resource url=" + url);
        if (url == null) {
            throw new IllegalStateException("aethereal: bundled native library '" + resource + "' is missing from the jar" + t.tail());
        }
        try {
            URLConnection c = url.openConnection();
            if (c instanceof JarURLConnection) {
                JarURLConnection jc = (JarURLConnection) c;
                JarEntry e3 = jc.getJarEntry();
                if (e3 != null && e3.getSize() >= 0 && e3.getCrc() >= 0) {
                    return report(new UrlSource(url, e3.getSize(), identity(e3.getSize(), e3.getCrc())), buildId, t);
                }
            }
        } catch (Throwable e4) {
            t.log("cannot inspect " + url + ": " + e4);
        }
        t.log("identity unavailable at runtime - falling back to the baked buildId");
        return new UrlSource(url, -1L, buildId);
    }

    private static Source report(Source src, String buildId, Trace t) {
        String strIdentity = src.identity();
        long size = src.size();
        src.origin();
        t.log("bundled id=" + strIdentity + " size=" + size + " (" + t + ")");
        if (!src.identity().equals(buildId)) {
            t.log("NOTE: this is not the library the jar was built with (baked id " + buildId + ") - the jar was repacked, e.g. with the protected build. That is supported: identity is read from the jar, not from the baked value.");
        }
        return src;
    }

    private static Path codeSourceJar(Trace t) {
        try {
            CodeSource cs = NativeLibraryLoader.class.getProtectionDomain().getCodeSource();
            if (cs == null || cs.getLocation() == null) {
                return null;
            }
            Path p = Paths.get(cs.getLocation().toURI());
            if (Files.isRegularFile(p, new LinkOption[0])) {
                return p;
            }
            return null;
        } catch (Throwable e) {
            t.log("code source unavailable: " + e);
            return null;
        }
    }

    private static final class ZipSource implements Source {
        private final Path jar;
        private final String entry;
        private final long size;
        private final String identity;

        private ZipSource(Path jar, String entry, long size, String identity) {
            this.jar = jar;
            this.entry = entry;
            this.size = size;
            this.identity = identity;
        }
public Path jar() {
            return this.jar;
        }

        public String entry() {
            return this.entry;
        }

        @Override
        public long size() {
            return this.size;
        }

        @Override
        public String identity() {
            return this.identity;
        }

        @Override
        public InputStream open() throws IOException {
            java.util.zip.ZipFile zf = new java.util.zip.ZipFile(this.jar.toFile());
            ZipEntry e = zf.getEntry(this.entry);
            if (e == null) {
                zf.close();
                throw new IOException("entry '" + this.entry + "' vanished from " + this.jar);
            }
            return new ClosingStream(zf.getInputStream(e), zf);
        }

        @Override
        public String origin() {
            return this.jar + "!/" + this.entry;
        }
    }

    private static final class ClosingStream extends FilterInputStream {
        private final java.util.zip.ZipFile jar;

        ClosingStream(InputStream in, java.util.zip.ZipFile jar) {
            super(in);
            this.jar = jar;
        }

        @Override
        public void close() throws IOException {
            try {
                super.close();
            } finally {
                this.jar.close();
            }
        }
    }

    private static final class UrlSource implements Source {
        private final URL url;
        private final long size;
        private final String identity;

        private UrlSource(URL url, long size, String identity) {
            this.url = url;
            this.size = size;
            this.identity = identity;
        }
public URL url() {
            return this.url;
        }

        @Override
        public long size() {
            return this.size;
        }

        @Override
        public String identity() {
            return this.identity;
        }

        @Override
        public InputStream open() throws IOException {
            return this.url.openStream();
        }

        @Override
        public String origin() {
            return this.url.toString();
        }
    }

    private static void load(String path, Trace t) {
        try {
            System.load(path);
            t.log("System.load OK");
            t.flush();
        } catch (Throwable e) {
            t.log("System.load FAILED: " + e);
            t.flush();
            UnsatisfiedLinkError fail = new UnsatisfiedLinkError("aethereal: failed to load native library " + path + hint(e) + t.tail());
            fail.initCause(e);
            throw fail;
        }
    }

    private static String hint(Throwable e) {
        String m = String.valueOf(e.getMessage()).toLowerCase(Locale.ROOT);
        if (m.contains("dependent libraries")) {
            return "\n  hint: the library imports another DLL that is not on the process search path.\n        A build carrying VMProtect markers imports VMProtectSDK64.dll until the\n        protector removes it - protect the library and put the protected file in\n        the jar (identity is read from the jar, so nothing has to be rebuilt).";
        }
        if (m.contains("not a valid win32 application") || m.contains("wrong elf class") || m.contains("bad magic") || m.contains("invalid elf")) {
            return "\n  hint: the file is not a library for this platform/architecture (a 32-bit JVM on a 64-bit library, or a truncated copy).";
        }
        return "";
    }

    private static Path extractDir(Trace t) {
        String override = System.getProperty(PROP_DIR);
        Path dir = Paths.get((override == null || override.isBlank()) ? System.getProperty("java.io.tmpdir") : override, new String[0]);
        t.log("dir=" + dir + (override == null ? "" : " [aethereal.loader.dir]"));
        return dir;
    }

    private static long sizeOf(Path p) {
        try {
            return Files.size(p);
        } catch (IOException e) {
            return -1L;
        }
    }

    private static void delete(Path p) {
        try {
            Files.deleteIfExists(p);
        } catch (IOException e) {
        }
    }

    static final class Trace {
        private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        private final List<String> lines = new ArrayList();
        private final long startNanos = System.nanoTime();

        Trace() {
        }

        void log(String line) {
            this.lines.add(line);
        }

        String tail() {
            StringBuilder sb = new StringBuilder("\n  loader trace:");
            for (String l : this.lines) {
                sb.append("\n    ").append(l);
            }
            return sb.toString();
        }

        void flush() {
            String where = System.getProperty(NativeLibraryLoader.PROP_LOG);
            if (where == null || where.isBlank() || "0".equals(where) || BooleanUtils.a.equalsIgnoreCase(where)) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            String stamp = LocalDateTime.now().format(STAMP);
            for (String l : this.lines) {
                sb.append(stamp).append(" [aethereal-loader] ").append(l).append(System.lineSeparator());
            }
            sb.append(stamp).append(" [aethereal-loader] done in ").append((System.nanoTime() - this.startNanos) / 1000000).append(" ms").append(System.lineSeparator());
            this.lines.clear();
            if ("1".equals(where) || BooleanUtils.e.equalsIgnoreCase(where)) {
                PrintStream err = System.err;
                err.print(sb);
                err.flush();
            } else {
                try {
                    Files.writeString(Paths.get(where, new String[0]), sb.toString(), StandardCharsets.UTF_8, new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND});
                } catch (Throwable e) {
                    System.err.println("[aethereal-loader] cannot write " + where + ": " + e);
                    System.err.print(sb);
                }
            }
        }
    }
}
