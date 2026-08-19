package aethereal.module.player;

import platform.inject.accessors.MinecraftClientAccessor;
import static aethereal.core.Interface.aM_;
import aethereal.core.Module;

import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.Interface;
import aethereal.core.ModuleRegister;
import aethereal.event.TickEvent;

import net.minecraft.item.Items;

@ModuleRegister(a = "Fast EXP", b = "Позволяет очень быстро бросать опыт", c = Category.Player)
public class FastEXP extends Module implements Interface {
    @EventTarget
    public void a(TickEvent event) {
        if (aM_.player.getMainHandStack().getItem() == Items.EXPERIENCE_BOTTLE) {
            ((platform.inject.accessors.MinecraftClientAccessor) (Object) aM_).setItemUseCooldown(0);
        }
    }
}
