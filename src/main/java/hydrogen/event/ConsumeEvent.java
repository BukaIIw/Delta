package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;
import net.minecraft.item.ItemStack;

public class ConsumeEvent extends Event implements IEvent {
    private final ItemStack a;

    @Generated
    public ConsumeEvent(ItemStack stack) {
        this.a = stack;
    }

    @Generated
    public ItemStack b() {
        return this.a;
    }
}
