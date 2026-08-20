package hydrogen.module.player;

import platform.inject.accessors.MinecraftClientAccessor;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.Interface;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;

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
