package hydrogen.core;


import hydrogen.network.PacketSecurity;
import lombok.Generated;

public class Packet {
    private final PacketSecurity a;
    private final String b;
    private String c;

    @Generated
    public void a(String payload) {
        this.c = payload;
    }

    @Generated
    public PacketSecurity a() {
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

    public Packet(String id, String payload, PacketSecurity security) {
        this.b = id;
        this.c = payload;
        this.a = security;
    }
}
