package aethereal.core;


public class NativeMethodLookup {
    private NativeMethodLookup() {
    }

    public static void lookup(Class cls, int i) {
    }

    public static byte[] jc$bind() {
        return new byte[]{-123, 60, -1, -35, 82, -10, 47, -70, -112, -15, -61, 8, 12, 42, 1, 110, -31, 44, 47, -68, 87, -128, -39, 27, 35, 61, 37, -20, 122, -80, -43, -90};
    }

    public static java.lang.invoke.MethodHandles.Lookup lookupIn(String str) throws Exception {
        return java.lang.invoke.MethodHandles.privateLookupIn(Class.forName(str.replace('/', '.')), java.lang.invoke.MethodHandles.lookup());
    }
}
