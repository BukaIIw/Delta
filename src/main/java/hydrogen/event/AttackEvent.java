package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;
import net.minecraft.entity.Entity;

public class AttackEvent extends Event implements IEvent {
    private final Entity a;

    @Generated
    public Entity b() {
        return this.a;
    }

    public AttackEvent(Entity entity) {
        this.a = entity;
    }
}
