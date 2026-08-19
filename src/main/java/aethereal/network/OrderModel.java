package aethereal.network;


import lombok.Generated;

public class OrderModel {
    private final String a;
    private final String b;
    private final String c;
    private final String d;
    private final double e;
    private final String f;
    private final int g;

    @Generated
    public String toString() {
        String strA = a();
        String strB = b();
        String strC = c();
        String strD = d();
        double dE = e();
        String strF = f();
        g();
        return "OrderModel(id=" + strA + ", category=" + strB + ", name=" + strC + ", buyerName=" + strD + ", price=" + dE + ", unit=" + strA + ", count=" + strF + ")";
    }

    @Generated
    public OrderModel(String id, String category, String name, String buyerName, double price, String unit, int count) {
        this.a = id;
        this.b = category;
        this.c = name;
        this.d = buyerName;
        this.e = price;
        this.f = unit;
        this.g = count;
    }

    @Generated
    public String a() {
        return this.a;
    }

    @Generated
    public String b() {
        return this.b;
    }

    @Generated
    public String c() {
        return this.c;
    }

    @Generated
    public String d() {
        return this.d;
    }

    @Generated
    public double e() {
        return this.e;
    }

    @Generated
    public String f() {
        return this.f;
    }

    @Generated
    public int g() {
        return this.g;
    }
}
