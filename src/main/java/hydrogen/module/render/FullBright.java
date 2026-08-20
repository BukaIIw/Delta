package hydrogen.module.render;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.GammaEvent;
import hydrogen.event.TickEvent;

import hydrogen.setting.ModeSetting;
import hydrogen.setting.SliderSetting;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

@ModuleRegister(a = "Full Bright", b = "Полностью освещает мир через гамму или ночное зрение", c = Category.Render)
public class FullBright extends Module {
    private final ModeSetting b = new ModeSetting("Режим видения", "Гамма", "Гамма", "Ночное зрение");
    private final SliderSetting c = (SliderSetting) new SliderSetting("Уровень гаммы", 4.0f, 1.0f, 8.0f, 0.5f).a(() -> {
        return Boolean.valueOf(this.b.l("Гамма"));
    });

    public FullBright() {
        a(this.b, this.c);
    }

    @EventTarget
    public void a(TickEvent event) {
        if (this.b.l("Ночное зрение")) {
            aM_.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 240, 1, false, false, false));
        } else {
            aM_.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    @EventTarget
    public void a(GammaEvent event) {
        if (this.b.l("Гамма")) {
            event.a(this.c.c().floatValue());
        }
    }
}
