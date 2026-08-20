package hydrogen.module.movement;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.InputEvent;

@ModuleRegister(a = "Safe Walk", b = "Не даёт упасть с края блоков", c = Category.Movement)
public class SafeWalk extends Module {
    @EventTarget
    public void a(InputEvent event) {
        b(event);
    }

    public void b(InputEvent event) {
        event.c(event.e() || (aM_.world.getBlockState(aM_.player.getBlockPos().down()).getCollisionShape(aM_.world, aM_.player.getBlockPos().down()).isEmpty() && aM_.player.isOnGround()));
    }
}
