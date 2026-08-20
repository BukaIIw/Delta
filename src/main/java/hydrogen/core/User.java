package hydrogen.core;


import lombok.Generated;

public class User {
    public final String a;
    public final String b;
    public final String c;
    public final String d;
    public final String e;
    public final String f;

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
    public String e() {
        return this.e;
    }

    @Generated
    public String f() {
        return this.f;
    }

    public User(String uid, String username, String hwid, String role, String expire, String token) {
        this.a = uid;
        this.b = username;
        this.c = hwid;
        this.d = role;
        this.e = expire;
        this.f = token;
    }
}
