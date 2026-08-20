package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;
import net.minecraft.util.hit.HitResult;

public class CrosshairTargetEvent extends Event implements IEvent {
    private final float a;
    private HitResult b;

    @Generated
    public void a(HitResult target) {
        this.b = target;
    }

    @Generated
    public float b() {
        return this.a;
    }

    @Generated
    public HitResult c() {
        return this.b;
    }

    public CrosshairTargetEvent(float tickDelta) {
        this.a = tickDelta;
    }
}
