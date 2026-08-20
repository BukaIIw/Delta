package hydrogen.module.render;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;
import hydrogen.render.ColorUtil;

import hydrogen.config.ThemeInfo;
import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.HandEvent;
import hydrogen.ui.shader.NoiseShader;

import hydrogen.setting.SliderSetting;
import net.minecraft.client.option.Perspective;

@ModuleRegister(a = "Hands Shader", b = "Накладывает шейдер на руку от первого лица", c = Category.Render)
public class HandsShader extends Module {
    private final SliderSetting b = new SliderSetting("Непрозрачность", 0.6f, 0.0f, 1.0f, 0.05f);

    public HandsShader() {
        a(this.b);
    }

    @EventTarget
    public void a(HandEvent event) {
        NoiseShader shader = HydrogenClient.h().d().i().f();
        if (aM_.options.getPerspective() == Perspective.FIRST_PERSON) {
            if (event.b()) {
                shader.e();
            }
            if (event.c()) {
                float[] color = ColorUtil.a(HydrogenClient.h().d().o().a(ThemeInfo.PRIMARY).a());
                color[3] = this.b.c().floatValue();
                shader.a(color);
            }
        }
    }
}
