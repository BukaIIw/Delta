package hydrogen.module.render;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.HandViewEvent;

import hydrogen.setting.ButtonSetting;
import hydrogen.setting.SliderSetting;
import net.minecraft.util.Hand;
import net.minecraft.client.util.math.MatrixStack;

@ModuleRegister(a = "View Model", b = "Изменяет положение и размер предметов в руке", c = Category.Render)
public class ViewModel extends Module {
    private final SliderSetting b = new SliderSetting("Основная рука X", 0.0f, -2.0f, 2.0f, 0.1f);
    private final SliderSetting c = new SliderSetting("Основная рука Y", 0.0f, -2.0f, 2.0f, 0.1f);
    private final SliderSetting d = new SliderSetting("Основная рука Z", 0.0f, -2.0f, 2.0f, 0.1f);
    private final SliderSetting e = new SliderSetting("Вторая рука X", 0.0f, -2.0f, 2.0f, 0.1f);
    private final SliderSetting f = new SliderSetting("Вторая рука Y", 0.0f, -2.0f, 2.0f, 0.1f);
    private final SliderSetting g = new SliderSetting("Вторая рука Z", 0.0f, -2.0f, 2.0f, 0.1f);
    private final ButtonSetting h = new ButtonSetting("Сбросить позиции", () -> {
        e().forEach(setting -> {
            if (setting instanceof SliderSetting) {
                SliderSetting slider = (SliderSetting) setting;
                slider.a(slider.g());
            }
        });
    });

    public ViewModel() {
        a(this.b, this.c, this.d, this.e, this.f, this.g, this.h);
    }

    @EventTarget
    public void a(HandViewEvent e) {
        MatrixStack matrix = e.b();
        if (e.d().equals(Hand.MAIN_HAND)) {
            matrix.translate(this.b.h().floatValue(), this.c.h().floatValue(), this.d.h().floatValue());
        } else {
            matrix.translate(this.e.h().floatValue(), this.f.h().floatValue(), this.g.h().floatValue());
        }
    }
}
