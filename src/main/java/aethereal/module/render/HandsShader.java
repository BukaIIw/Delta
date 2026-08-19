package aethereal.module.render;

import aethereal.core.Interface;

import static aethereal.core.Interface.aM_;
import aethereal.core.Delta;
import aethereal.core.Module;
import aethereal.render.ColorUtil;

import aethereal.config.ThemeInfo;
import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.ModuleRegister;
import aethereal.event.HandEvent;
import aethereal.ui.shader.NoiseShader;

import aethereal.setting.SliderSetting;
import net.minecraft.client.option.Perspective;

@ModuleRegister(a = "Hands Shader", b = "Накладывает шейдер на руку от первого лица", c = Category.Render)
public class HandsShader extends Module {
    private final SliderSetting b = new SliderSetting("Непрозрачность", 0.6f, 0.0f, 1.0f, 0.05f);

    public HandsShader() {
        a(this.b);
    }

    @EventTarget
    public void a(HandEvent event) {
        NoiseShader shader = Delta.h().d().i().f();
        if (aM_.options.getPerspective() == Perspective.FIRST_PERSON) {
            if (event.b()) {
                shader.e();
            }
            if (event.c()) {
                float[] color = ColorUtil.a(Delta.h().d().o().a(ThemeInfo.PRIMARY).a());
                color[3] = this.b.c().floatValue();
                shader.a(color);
            }
        }
    }
}
