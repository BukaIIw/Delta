package aethereal.event;

import aethereal.core.Event;
import aethereal.core.IEvent;

import lombok.Generated;
import net.minecraft.network.packet.Packet;

public class PacketEvent extends Event implements IEvent {
    public enum Type {
        SEND,
        RECEIVE
    }

    @Generated
    public Packet<?> d() {
        return this.packet;
    }

    @Generated
    public Type e() {
        return this.type;
    }

    private final Packet<?> packet;
    private final Type type;

    public PacketEvent(Packet<?> packet, Type type) {
        this.packet = packet;
        this.type = type;
    }

    public boolean b() {
        return this.type == Type.SEND;
    }

    public boolean c() {
        return this.type == Type.RECEIVE;
    }
}
