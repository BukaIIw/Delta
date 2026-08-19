package aethereal.event;

import aethereal.core.Event;
import aethereal.core.IEvent;

import java.util.List;
import lombok.Generated;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class TooltipEvent extends Event implements IEvent {
    private final ItemStack a;
    private final List<Text> b;

    @Generated
    public ItemStack b() {
        return this.a;
    }

    @Generated
    public List<Text> c() {
        return this.b;
    }

    public TooltipEvent(ItemStack stack, List<Text> lines) {
        this.a = stack;
        this.b = lines;
    }
}
