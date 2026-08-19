package aethereal.util;

import aethereal.util.Cast_2;
import aethereal.util.Lazy;
import aethereal.core.Supplier;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LazyUtil {
    private static final Object a = new Object() {
        public String toString() {
            return "null";
        }
    };

    LazyUtil() {
    }

    static Object a(final Object value) {
        return value == null ? a : value;
    }

    static <T> T b(Object obj) {
        if (obj == a) {
            return null;
        }
        return (T) Cast_2.a(obj);
    }

    static class a<T> implements Lazy<T> {
        private final T a;

        a(final T value) {
            this.a = value;
        }
        public T a() {
            return this.a;
        }
        public boolean b() {
            return true;
        }
        public void a(final T newValue) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return String.valueOf(this.a);
        }
    }

    static class d<T> implements Lazy<T> {
        private final WeakReference<T> a;

        d(final T value) {
            this.a = new WeakReference<>(value);
        }
        public T a() {
            return this.a.get();
        }
        public boolean b() {
            return true;
        }
        public void a(final T newValue) {
            throw new UnsupportedOperationException();
        }

        public String toString() {
            return String.valueOf(a());
        }
    }

    static class c<T> implements Lazy<T> {
        private final Lock a = new ReentrantLock();
        private final java.util.function.Supplier<T> b;
        private volatile Object c;

        c(final java.util.function.Supplier<T> supplier) {
            this.b = supplier;
        }
        public T a() {
            Object obj = this.c;
            if (obj == null) {
                this.a.lock();
                try {
                    obj = this.c;
                    if (obj == null) {
                        obj = this.b.get();
                        this.c = LazyUtil.a(obj);
                    }
                } finally {
                    this.a.unlock();
                }
            }
            return (T) LazyUtil.b(obj);
        }
        public void a(final T newValue) {
            this.c = newValue;
        }

        public void c() {
            this.c = null;
        }
        public boolean b() {
            return this.c != null;
        }

        public String toString() {
            return b() ? String.valueOf(this.c) : "Lazy value not initialized";
        }
    }

    static class b<T> implements Lazy<T> {
        private final java.util.function.Supplier<T> a;
        private Object b;

        public b(final java.util.function.Supplier<T> supplier) {
            this.a = supplier;
        }
        public T a() {
            Object obj = this.b;
            if (obj == null) {
                obj = this.a.get();
                this.b = LazyUtil.a(obj);
            }
            return (T) LazyUtil.b(obj);
        }
        public boolean b() {
            return this.b != null;
        }
        public void a(final T newValue) {
            this.b = newValue;
        }
    }
}
