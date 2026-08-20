package hydrogen.module.player;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;

import net.minecraft.client.gui.screen.DeathScreen;

@ModuleRegister(a = "Auto Respawn", b = "Автоматически возрождает персонажа после смерти", c = Category.Player)
public class AutoRespawn extends Module {
    @EventTarget
    public void a(TickEvent event) {
        if ((aM_.currentScreen instanceof DeathScreen) && aM_.player.deathTime >= 5) {
            aM_.player.requestRespawn();
        }
    }
}
