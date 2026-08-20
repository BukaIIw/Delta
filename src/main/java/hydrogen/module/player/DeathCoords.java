package hydrogen.module.player;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;
import hydrogen.util.ChatUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;

@ModuleRegister(a = "Death Coords", b = "Выводит координаты последней смерти", c = Category.Player)
public class DeathCoords extends Module {
    @EventTarget
    public void a(TickEvent event) {
        if (aM_.player.deathTime == 1) {
            ChatUtil.a((Object) String.format("Вы погибли на координатах: &c[%d, %d, %d]", Integer.valueOf(aM_.player.getBlockPos().getX()), Integer.valueOf(aM_.player.getBlockPos().getY()), Integer.valueOf(aM_.player.getBlockPos().getZ())));
        }
    }
}
