package hydrogen.module.movement;

import hydrogen.core.Interface;

import platform.inject.accessors.LivingEntityAccessor;
import platform.inject.accessors.MinecraftClientAccessor;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;
import hydrogen.setting.BooleanSetting;

import hydrogen.setting.MultiModeSetting;
import net.minecraft.item.BlockItem;

@ModuleRegister(a = "No Delay", b = "Убирает задержку у выбранных действий", c = Category.Movement)
public class NoDelay extends Module {
    public final MultiModeSetting b = new MultiModeSetting("Отключить задержку на", new BooleanSetting("Поставку блоков", true), new BooleanSetting("Прыжки", true));

    public NoDelay() {
        a(this.b);
    }

    @EventTarget
    public void a(TickEvent event) {
        if (this.b.a("Поставку блоков").c().booleanValue() && (aM_.player.getMainHandStack().getItem() instanceof BlockItem) && !HydrogenClient.h().d().t().aS().m()) {
            ((platform.inject.accessors.MinecraftClientAccessor) (Object) aM_).setItemUseCooldown(0);
        }
        if (this.b.a("Прыжки").c().booleanValue()) {
            ((platform.inject.accessors.LivingEntityAccessor) (Object) aM_.player).setJumpingCooldown(0);
        }
    }
}
