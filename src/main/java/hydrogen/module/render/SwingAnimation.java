package hydrogen.module.render;

import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.HandAnimationEvent;

import hydrogen.setting.BooleanSetting;
import hydrogen.setting.ModeSetting;
import hydrogen.setting.SliderSetting;
import lombok.Generated;
import net.minecraft.util.Hand;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

@ModuleRegister(a = "Swing Animation", b = "Настраивает анимацию взмаха руки", c = Category.Render)
public class SwingAnimation extends Module {
    private final BooleanSetting b = new BooleanSetting("Учитывать включённую Aura", true);
    private final ModeSetting c = new ModeSetting("Режим анимации", "Мод 1", "Мод 1", "Мод 2", "Мод 3", "Мод 4", "Мод 5", "Мод 6", "Мод 7", "Мод 8");
    private final SliderSetting d = (SliderSetting) new SliderSetting("Угол поворота", 75.0f, 0.0f, 360.0f, 1.0f).a(() -> {
        return Boolean.valueOf(this.c.l("Мод 1"));
    });
    private final SliderSetting e = (SliderSetting) new SliderSetting("Наклон кончика", -20.0f, -90.0f, 90.0f, 1.0f).a(() -> {
        return Boolean.valueOf(!this.c.l("Мод 5"));
    });
    private final SliderSetting f = new SliderSetting("Интенсивность взмаха", 5.0f, 1.0f, 10.0f, 1.0f);

    @Generated
    public BooleanSetting q() {
        return this.b;
    }

    @Generated
    public ModeSetting r() {
        return this.c;
    }

    @Generated
    public SliderSetting s() {
        return this.d;
    }

    @Generated
    public SliderSetting t() {
        return this.e;
    }

    @Generated
    public SliderSetting u() {
        return this.f;
    }

    public SwingAnimation() {
        a(this.b, this.c, this.d, this.e, this.f);
    }

    @EventTarget
    public void a(HandAnimationEvent event) {
        if ((!this.b.c().booleanValue() || HydrogenClient.h().d().t().B().s() != null) && event.c() == Hand.MAIN_HAND) {
            MatrixStack matrices = event.b();
            float anim = (float) Math.sin(((double) event.d()) * 3.1415936112270124d);
            float power = this.f.c().floatValue() * 10.0f;
            int arm = event.e();
            matrices.translate(arm * (this.c.l("Мод 5") ? 0.5f : 0.72f), -0.5f, this.c.l("Мод 5") ? -0.72f : -1.0f);
            if (!this.c.l("Мод 5")) {
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-this.e.c().floatValue()));
            }
            switch (this.c.c()) {
                case "Мод 1":
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * 90));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * (-70)));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((-this.d.c().floatValue()) - (power * anim)));
                    break;
                case "Мод 2":
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * 90));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * (-65)));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((-65.0f) + (power * anim)));
                    break;
                case "Мод 3":
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * (-90)));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * 60));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(30.0f));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * power * anim));
                    break;
                case "Мод 4":
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * 90));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * (-75)));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((-45.0f) - (power * anim)));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * power * anim * 0.5f));
                    break;
                case "Мод 5":
                    float strength = power / 80.0f;
                    float swing = anim * anim;
                    float twist = (float) Math.sin(((double) (event.d() * event.d())) * 3.1415936112270124d);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * (45.0f + (twist * (-20.0f) * strength))));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * swing * (-22.0f) * strength));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(swing * (-85.0f) * strength));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * (-45.0f)));
                    break;
                case "Мод 6":
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * 80));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * (-50.0f + (power * anim * 0.35f))));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((-90.0f) + (power * anim)));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * 25.0f * anim));
                    break;
                case "Мод 7":
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * 95));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * (-40)));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((-20.0f) - (power * anim * 1.35f)));
                    matrices.translate(0.0f, 0.0f, -0.18f * anim);
                    break;
                case "Мод 8":
                    float loop = (float) Math.sin(((double) event.d()) * 6.283185d);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * (70.0f + (loop * 18.0f))));
                    matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm * ((-55.0f) - (anim * 20.0f))));
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((-55.0f) - (power * 0.55f * anim)));
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm * loop * power * 0.25f));
                    break;
            }
            event.a(true);
        }
    }
}
