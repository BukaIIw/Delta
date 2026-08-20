package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;
import net.minecraft.entity.LivingEntity;

public class JumpEvent extends Event implements IEvent {
    private final LivingEntity a;

    @Generated
    public JumpEvent(LivingEntity livingEntity) {
        this.a = livingEntity;
    }

    @Generated
    public LivingEntity b() {
        return this.a;
    }
}
