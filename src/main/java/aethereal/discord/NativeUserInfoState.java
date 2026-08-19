package aethereal.discord;

import aethereal.core.Delta;

import aethereal.core.InterfaceC0020Opcode;
import aethereal.core.User;

import java.util.Arrays;

public final class NativeUserInfoState {
    private static final byte[][] source = new byte[7][];
    private static long tailQword;
    private static final String[] cache;
    private static volatile boolean[] initialized;
    private static final int[] getterToSlot;
    private static final Object updateLock;

    private NativeUserInfoState() {
    }

    static {
        source[0] = new byte[0];
        source[1] = new byte[0];
        source[2] = new byte[0];
        source[3] = new byte[0];
        source[4] = new byte[0];
        source[5] = new byte[0];
        source[6] = new byte[0];
        cache = new String[6];
        initialized = new boolean[6];
        getterToSlot = new int[]{2, 0, 3, 1, 4, 6};
        updateLock = new Object();
    }

    public static void installNativeOrder(byte[][] bArr, long j) {
        synchronized (updateLock) {
            byte[][] bArr2 = source;
            byte[] bArr3 = bArr[0];
            bArr2[0] = Arrays.copyOf(bArr3, bArr3.length);
            byte[][] bArr4 = source;
            byte[] bArr5 = bArr[1];
            bArr4[1] = Arrays.copyOf(bArr5, bArr5.length);
            byte[][] bArr6 = source;
            byte[] bArr7 = bArr[2];
            bArr6[2] = Arrays.copyOf(bArr7, bArr7.length);
            byte[][] bArr8 = source;
            byte[] bArr9 = bArr[3];
            bArr8[3] = Arrays.copyOf(bArr9, bArr9.length);
            byte[][] bArr10 = source;
            byte[] bArr11 = bArr[4];
            bArr10[4] = Arrays.copyOf(bArr11, bArr11.length);
            byte[][] bArr12 = source;
            byte[] bArr13 = bArr[5];
            bArr12[5] = Arrays.copyOf(bArr13, bArr13.length);
            byte[][] bArr14 = source;
            byte[] bArr15 = bArr[6];
            bArr14[6] = Arrays.copyOf(bArr15, bArr15.length);
            tailQword = j;
        }
    }

    public static String uid() {
        return initialized[0] ? cache[0] : get(0);
    }

    public static String username() {
        return initialized[1] ? cache[1] : get(1);
    }

    public static String hwid() {
        return initialized[2] ? cache[2] : get(2);
    }

    public static String role() {
        return initialized[3] ? cache[3] : get(3);
    }

    public static String expire() {
        return initialized[4] ? cache[4] : get(4);
    }

    public static String token() {
        return initialized[5] ? cache[5] : get(5);
    }

    private static synchronized String get(int i) {
        if (!initialized[i]) {
            cache[i] = decode(source[getterToSlot[i]]);
            initialized[i] = true;
            initialized = initialized;
        }
        return cache[i];
    }

    private static String decode(byte[] bArr) {
        char[] cArr = new char[bArr.length];
        int i = 0;
        int i2 = 0;
        while (i < bArr.length && bArr[i] != 0) {
            int i3 = i;
            i++;
            int i4 = bArr[i3] & 255;
            if (i4 > 127) {
                if ((i4 & 224) == 192) {
                    if (i < bArr.length) {
                        i++;
                        int i5 = bArr[i] & 255;
                        if ((i5 & InterfaceC0020Opcode.C) == 128 && i4 != 193 && (i4 != 192 || i5 == 128)) {
                            int i6 = i2;
                            i2++;
                            cArr[i6] = (char) (((i4 & 31) << 6) | (i5 & 63));
                        }
                    }
                    throw bad();
                }
                if ((i4 & 240) == 224 && i + 1 < bArr.length) {
                    int i7 = i + 1;
                    int i8 = bArr[i] & 255;
                    i = i7 + 1;
                    int i9 = bArr[i7] & 255;
                    if ((i8 & InterfaceC0020Opcode.C) == 128 && (i9 & InterfaceC0020Opcode.C) == 128 && (i4 != 224 || i8 >= 160)) {
                        int i10 = i2;
                        i2++;
                        cArr[i10] = (char) (((i4 & 15) << 12) | ((i8 & 63) << 6) | (i9 & 63));
                    }
                }
                throw bad();
            }
            int i11 = i2;
            i2++;
            cArr[i11] = (char) i4;
        }
        return new String(cArr, 0, i2);
    }

    private static IllegalArgumentException bad() {
        return new IllegalArgumentException("invalid modified UTF-8");
    }

    public static void installUnifiedNativeOrder(byte[][] bArr, long j) {
        decode(bArr[5]);
        User user = new User(decode(bArr[2]), decode(bArr[0]), decode(bArr[3]), decode(bArr[1]), decode(bArr[4]), decode(bArr[6]));
        installNativeOrder(bArr, j);
        Delta.jc$publishUnifiedUser$(user);
    }
}
