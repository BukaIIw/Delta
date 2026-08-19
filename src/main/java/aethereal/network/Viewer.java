package aethereal.network;


import aethereal.lib.javassist.AccessFlag;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;

public class Viewer extends ClassLoader {
    private String a;
    private int b;

    public static void a(String[] args) throws Throwable {
        if (args.length >= 3) {
            Viewer cl = new Viewer(args[0], Integer.parseInt(args[1]));
            String[] args2 = new String[args.length - 3];
            System.arraycopy(args, 3, args2, 0, args.length - 3);
            cl.a(args[2], args2);
            return;
        }
        System.err.println("Usage: java javassist.tools.web.Viewer <host> <port> class [args ...]");
    }

    public Viewer(String host, int p) {
        this.a = host;
        this.b = p;
    }

    public String a() {
        return this.a;
    }

    public int b() {
        return this.b;
    }

    public void a(String classname, String[] args) throws Throwable {
        Class<?> c = loadClass(classname);
        try {
            c.getDeclaredMethod("main", String[].class).invoke(null, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            c = findClass(name);
        }
        if (c == null) {
            throw new ClassNotFoundException(name);
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> c = null;
        if (name.startsWith("java.") || name.startsWith("javax.") || name.equals("javassist.tools.web.Viewer")) {
            c = findSystemClass(name);
        }
        if (c == null) {
            try {
                byte[] b = a(name);
                if (b != null) {
                    c = defineClass(name, b, 0, b.length);
                }
            } catch (Exception e) {
            }
        }
        return c;
    }

    protected byte[] a(String classname) throws Exception {
        byte[] b;
        URL url = new URL("http", this.a, this.b, "/" + classname.replace('.', '/') + ".class");
        URLConnection con = url.openConnection();
        con.connect();
        int size = con.getContentLength();
        InputStream s = con.getInputStream();
        if (size <= 0) {
            b = a(s);
        } else {
            b = new byte[size];
            int len = 0;
            do {
                int n = s.read(b, len, size - len);
                if (n < 0) {
                    s.close();
                    throw new IOException("the stream was closed: " + classname);
                }
                len += n;
            } while (len < size);
        }
        s.close();
        return b;
    }

    private byte[] a(InputStream fin) throws IOException {
        byte[] buf = new byte[AccessFlag.o];
        int size = 0;
        int len = 0;
        do {
            size += len;
            if (buf.length - size <= 0) {
                byte[] newbuf = new byte[buf.length * 2];
                System.arraycopy(buf, 0, newbuf, 0, size);
                buf = newbuf;
            }
            len = fin.read(buf, size, buf.length - size);
        } while (len >= 0);
        byte[] result = new byte[size];
        System.arraycopy(buf, 0, result, 0, size);
        return result;
    }
}
