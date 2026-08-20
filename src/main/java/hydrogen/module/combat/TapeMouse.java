package hydrogen.module.combat;

import platform.inject.invokers.MinecraftClientInvoker;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.Interface;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;

import hydrogen.setting.BooleanSetting;
import hydrogen.util.CounterUtil;
import hydrogen.setting.ModeSetting;
import hydrogen.setting.SliderSetting;
import lombok.Generated;

@ModuleRegister(a = "Tape Mouse", b = "Автоматически кликает выбранной кнопкой мыши через заданные промежутки времени", c = Category.Combat)
public class TapeMouse extends Module implements Interface {
    private final SliderSetting b = new SliderSetting("Задержка между кликами", 1000.0f, 10.0f, 5000.0f, 10.0f);
    private final BooleanSetting c = new BooleanSetting("Не кликать во время еды", true);
    private final ModeSetting d = new ModeSetting("Кнопка мыши", "Правая", "Правая", "Левая");
    private final CounterUtil e = new CounterUtil();

    @Generated
    public SliderSetting q() {
        return this.b;
    }

    @Generated
    public BooleanSetting r() {
        return this.c;
    }

    @Generated
    public ModeSetting s() {
        return this.d;
    }

    @Generated
    public CounterUtil t() {
        return this.e;
    }

    public TapeMouse() {
        a(this.d, this.c, this.b);
    }

    @EventTarget
    public void a(TickEvent event) {
        if ((!this.c.c().booleanValue() || !aM_.player.isUsingItem()) && this.e.a(this.b.c().intValue())) {
            switch (this.d.c()) {
                case "Правая":
                    ((platform.inject.invokers.MinecraftClientInvoker) aM_).invokeDoItemUse();
                    break;
                case "Левая":
                    ((platform.inject.invokers.MinecraftClientInvoker) aM_).invokeDoAttack();
                    break;
            }
            this.e.b();
        }
    }
}
