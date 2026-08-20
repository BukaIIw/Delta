package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;
import net.minecraft.client.gui.DrawContext;

public class CrosshairEvent extends Event implements IEvent {
    private final DrawContext a;
    private final float b;

    @Generated
    public DrawContext b() {
        return this.a;
    }

    @Generated
    public float c() {
        return this.b;
    }

    public CrosshairEvent(DrawContext context, float partialTicks) {
        this.a = context;
        this.b = partialTicks;
    }
}
