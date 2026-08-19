package aethereal.config;


import aethereal.render.ColorUtil;

import lombok.Generated;

public class ThemeConstructor {
    private String a;
    private int b;
    private int c;
    private int d;
    private int e;

    @Generated
    public void a(String name) {
        this.a = name;
    }

    @Generated
    public void b(int r) {
        this.b = r;
    }

    @Generated
    public void c(int g) {
        this.c = g;
    }

    @Generated
    public void d(int b) {
        this.d = b;
    }

    @Generated
    public void e(int a) {
        this.e = a;
    }

    @Generated
    public ThemeConstructor() {
    }

    @Generated
    public ThemeConstructor(String name, int r, int g, int b, int a) {
        this.a = name;
        this.b = r;
        this.c = g;
        this.d = b;
        this.e = a;
    }

    @Generated
    public String c() {
        return this.a;
    }

    @Generated
    public int d() {
        return this.b;
    }

    @Generated
    public int e() {
        return this.c;
    }

    @Generated
    public int f() {
        return this.d;
    }

    @Generated
    public int g() {
        return this.e;
    }

    public int a() {
        return ColorUtil.a(this.b, this.c, this.d, this.e);
    }

    public void a(int color) {
        int[] components = ColorUtil.b(color);
        this.b = components[0];
        this.c = components[1];
        this.d = components[2];
        this.e = components[3];
    }

    public float b() {
        return this.e / 255.0f;
    }
}
