package hydrogen.module.movement;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;

import hydrogen.setting.SliderSetting;
import net.minecraft.util.hit.BlockHitResult;

@ModuleRegister(a = "Fast Break", b = "Ускоряет разрушение блоков, обрабатывая добычу несколько раз за тик", c = Category.Movement)
public class FastBreak extends Module {
    private final SliderSetting b = new SliderSetting("Интенсивность копания", 2.0f, 1.0f, 5.0f, 0.25f);

    public FastBreak() {
        a(this.b);
    }

    @EventTarget
    public void a(TickEvent event) {
        if (aM_.options.attackKey.isPressed() && aM_.interactionManager.isBreakingBlock()) {
            if (aM_.crosshairTarget instanceof BlockHitResult class_3965Var) {
                BlockHitResult hit = class_3965Var;
                for (int i = 0; i < this.b.c().intValue() - 1; i++) {
                    aM_.interactionManager.updateBlockBreakingProgress(hit.getBlockPos(), hit.getSide());
                }
            }
        }
    }
}
