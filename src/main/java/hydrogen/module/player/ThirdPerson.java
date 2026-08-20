package hydrogen.module.player;

import hydrogen.core.Interface;
import hydrogen.lib.javassist.TokenId;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;
import hydrogen.util.Look;
import hydrogen.util.MathUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.KeyEvent;
import hydrogen.event.TickEvent;

import hydrogen.setting.BindSetting;
import hydrogen.setting.ModeSetting;
import hydrogen.util.Rotation;
import net.minecraft.client.option.Perspective;

@ModuleRegister(a = "Third Person", b = "Свободный обзор от третьего лица без изменения направления движения", c = Category.Player)
public class ThirdPerson extends Module {
    private boolean c;
    private Rotation e;
    private final ModeSetting b = new ModeSetting("Режим активации осмотра", "По нажатию", "По нажатию", "По зажатию");
    private final BindSetting d = new BindSetting("Кнопка осмотра", Integer.valueOf(TokenId.Q_), 0).a(() -> {
        if (this.b.l("По зажатию")) {
            d(true);
        } else {
            d(!this.c);
        }
    }).b(() -> {
        if (this.c && this.b.l("По зажатию")) {
            d(false);
        }
    });

    public ThirdPerson() {
        a(this.b, this.d);
    }

    @EventTarget
    public void a(KeyEvent event) {
        if (this.c && event.b() == aM_.options.togglePerspectiveKey.getDefaultKey().getCode()) {
            event.a(true);
        }
    }

    @EventTarget
    public void a(TickEvent event) {
        if (this.c) {
            if (aM_.currentScreen != null) {
                d(false);
            } else {
                HydrogenClient.h().d().k().a(new Rotation(aM_.player.getYaw(), MathUtil.b(aM_.player.getPitch(), -89.0f, 89.0f)), 360.0f, 0, 1);
            }
        }
    }

    private void d(boolean active) {
        if (active) {
            this.e = new Rotation(Look.b(), Look.c());
        } else {
            Look.a(this.e.c());
            Look.b(this.e.d());
        }
        aM_.options.setPerspective(active ? Perspective.THIRD_PERSON_BACK : Perspective.FIRST_PERSON);
        this.c = active;
    }
}
