package aethereal.util;


public class Util_2 {
    private static a a;
    private static boolean b = false;

    private Util_2() {
    }

    public static String a(String key) {
        if (key == null) {
            throw new IllegalArgumentException("null input");
        }
        String result = null;
        try {
            result = System.getProperty(key);
        } catch (SecurityException e) {
        }
        return result;
    }

    public static boolean b(String key) {
        String value = a(key);
        if (value == null) {
            return false;
        }
        return value.equalsIgnoreCase(BooleanUtils.e);
    }

    static final class a extends SecurityManager {
        private a() {
        }

        @Override
        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

    private static a b() {
        if (a != null) {
            return a;
        }
        if (b) {
            return null;
        }
        a = c();
        b = true;
        return a;
    }

    private static a c() {
        try {
            return new a();
        } catch (SecurityException e) {
            return null;
        }
    }

    public static Class<?> a() {
        a securityManager = b();
        if (securityManager == null) {
            return null;
        }
        Class<?>[] trace = securityManager.getClassContext();
        String thisClassName = Util_2.class.getName();
        int i = 0;
        while (i < trace.length && !thisClassName.equals(trace[i].getName())) {
            i++;
        }
        if (i >= trace.length || i + 2 >= trace.length) {
            throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen");
        }
        return trace[i + 2];
    }

    public static final void a(String msg, Throwable t) {
        System.err.println(msg);
        System.err.println("Reported exception:");
        t.printStackTrace();
    }

    public static final void c(String msg) {
        System.err.println("SLF4J: " + msg);
    }
}
