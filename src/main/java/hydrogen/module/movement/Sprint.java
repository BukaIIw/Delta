package hydrogen.module.movement;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;

import net.minecraft.entity.effect.StatusEffects;

@ModuleRegister(a = "Sprint", b = "Автоматически включает спринт при движении", c = Category.Movement)
public class Sprint extends Module {
    @EventTarget
    public void a(TickEvent event) {
        aM_.player.setSprinting(aM_.player.input.movementForward > 0.0f && !aM_.player.hasStatusEffect(StatusEffects.BLINDNESS) && (aM_.player.getAbilities().invulnerable || aM_.player.getHungerManager().getFoodLevel() > 6));
    }
}
