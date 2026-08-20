package hydrogen.module.combat;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.AttackEvent;
import hydrogen.event.InputEvent;
import hydrogen.event.TickEvent;

import net.minecraft.entity.player.PlayerEntity;

@ModuleRegister(a = "Shift TAP", b = "Автоматически приседает в момент удара по игроку", c = Category.Combat)
public class ShiftTAP extends Module {
    private int b;

    @EventTarget
    public void a(AttackEvent event) {
        if (event.b() instanceof PlayerEntity) {
            this.b = 2;
        }
    }

    @EventTarget
    public void a(TickEvent event) {
        if (this.b > 0) {
            this.b--;
        }
    }

    @EventTarget
    public void a(InputEvent event) {
        if (this.b > 0) {
            event.c(true);
        }
    }
}
