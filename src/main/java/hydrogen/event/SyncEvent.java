package hydrogen.event;

import hydrogen.core.Event;
import hydrogen.core.IEvent;

import lombok.Generated;
import net.minecraft.item.ItemStack;

public class SyncEvent extends Event implements IEvent {
    private final int a;
    private ItemStack b;

    @Generated
    public void a(ItemStack stack) {
        this.b = stack;
    }

    @Generated
    public int b() {
        return this.a;
    }

    @Generated
    public ItemStack c() {
        return this.b;
    }

    public SyncEvent(int slot, ItemStack stack) {
        this.a = slot;
        this.b = stack;
    }
}
