package aethereal.util;


public class KeyValuePair {
    public final String a;
    public final Object b;

    public KeyValuePair(String key, Object value) {
        this.a = key;
        this.b = value;
    }

    public String toString() {
        return String.valueOf(this.a) + "=\"" + String.valueOf(this.b) + "\"";
    }
}
