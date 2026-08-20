package hydrogen.module.movement;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.SlowEvent;

import hydrogen.setting.ModeSetting;

@ModuleRegister(a = "No Slow Down", b = "Убирает замедление при использовании предметов", c = Category.Movement)
public class NoSlowDown extends Module {
    private final ModeSetting b = new ModeSetting("Режим использования", "Vanilla", "Vanilla");

    public NoSlowDown() {
        a(this.b);
    }

    @EventTarget
    public void a(SlowEvent slow) {
        if (this.b.l("Vanilla")) {
            slow.a(true);
        }
    }
}
