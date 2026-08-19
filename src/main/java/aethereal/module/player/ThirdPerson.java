package aethereal.module.player;

import aethereal.core.Interface;
import aethereal.lib.javassist.TokenId;

import static aethereal.core.Interface.aM_;
import aethereal.core.Delta;
import aethereal.core.Module;
import aethereal.util.Look;
import aethereal.util.MathUtil;

import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.ModuleRegister;
import aethereal.event.KeyEvent;
import aethereal.event.TickEvent;

import aethereal.setting.BindSetting;
import aethereal.setting.ModeSetting;
import aethereal.util.Rotation;
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
                Delta.h().d().k().a(new Rotation(aM_.player.getYaw(), MathUtil.b(aM_.player.getPitch(), -89.0f, 89.0f)), 360.0f, 0, 1);
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
