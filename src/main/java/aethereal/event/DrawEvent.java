package aethereal.event;

import aethereal.core.HydrogenClient;
import aethereal.core.IEvent;

import aethereal.core.Event;
import aethereal.core.Interface;

import aethereal.render.Draw2DProcessor;
import aethereal.render.Draw3DProcessor;
import lombok.Generated;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public class DrawEvent extends Event implements Interface, IEvent {
    private final Draw2DProcessor b = HydrogenClient.h().d().i();
    private final Draw3DProcessor c = HydrogenClient.h().d().j();
    private final a d;
    private final float e;
    private final MatrixStack f;
    private DrawContext g;

    public enum a {
        D2D,
        D3D
    }

    @Generated
    public Draw2DProcessor d() {
        return this.b;
    }

    @Generated
    public Draw3DProcessor e() {
        return this.c;
    }

    @Generated
    public a f() {
        return this.d;
    }

    @Generated
    public float g() {
        return this.e;
    }

    @Generated
    public MatrixStack h() {
        return this.f;
    }

    @Generated
    public DrawContext i() {
        return this.g;
    }

    public DrawEvent(MatrixStack stack, float tickDelta, a type) {
        this.f = stack;
        this.e = tickDelta;
        this.d = type;
    }

    public DrawEvent(DrawContext context, float tickDelta, a type) {
        this.g = context;
        this.f = context.getMatrices();
        this.e = tickDelta;
        this.d = type;
    }

    public boolean b() {
        return this.d == a.D2D;
    }

    public boolean c() {
        return this.d == a.D3D;
    }
}
