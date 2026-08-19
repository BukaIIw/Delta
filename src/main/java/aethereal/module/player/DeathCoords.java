package aethereal.module.player;

import aethereal.core.Interface;

import static aethereal.core.Interface.aM_;
import aethereal.core.Module;
import aethereal.util.ChatUtil;

import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.ModuleRegister;
import aethereal.event.TickEvent;

@ModuleRegister(a = "Death Coords", b = "Выводит координаты последней смерти", c = Category.Player)
public class DeathCoords extends Module {
    @EventTarget
    public void a(TickEvent event) {
        if (aM_.player.deathTime == 1) {
            ChatUtil.a((Object) String.format("Вы погибли на координатах: &c[%d, %d, %d]", Integer.valueOf(aM_.player.getBlockPos().getX()), Integer.valueOf(aM_.player.getBlockPos().getY()), Integer.valueOf(aM_.player.getBlockPos().getZ())));
        }
    }
}
