package hydrogen.module.misc;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.InputEvent;
import hydrogen.event.PacketEvent;

import hydrogen.setting.BooleanSetting;
import hydrogen.setting.ModeSetting;
import hydrogen.setting.MultiModeSetting;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

@ModuleRegister(a = "Anti AFK", b = "Не даёт серверу кикнуть вас за бездействие", c = Category.Player)
public class AntiAFK extends Module {
    private final ModeSetting b = new ModeSetting("Режим использования", "Обычный", "Обычный", "FunTime");
    private final MultiModeSetting c = (MultiModeSetting) new MultiModeSetting("Выполнять действия", new BooleanSetting("Прыжок", true), new BooleanSetting("Взмах", true), new BooleanSetting("Движение", true)).a(() -> {
        return Boolean.valueOf(this.b.l("Обычный"));
    });
    private final BooleanSetting d = (BooleanSetting) new BooleanSetting("Реагировать на недоступность", false).a(() -> {
        return Boolean.valueOf(this.b.l("FunTime"));
    });

    public AntiAFK() {
        a(this.b, this.c, this.d);
    }

    @EventTarget
    public void a(InputEvent event) {
        if (aM_.player.age % 600 == 0) {
            if (this.b.l("Обычный")) {
                if (this.c.a("Прыжок").c().booleanValue() && aM_.player.isOnGround()) {
                    event.b(true);
                }
                if (this.c.a("Взмах").c().booleanValue()) {
                    aM_.player.swingHand(Hand.MAIN_HAND);
                }
                if (this.c.a("Движение").c().booleanValue()) {
                    HydrogenClient.h().d().v().g().a(7);
                    return;
                }
                return;
            }
            if (this.b.l("FunTime") && !this.d.c().booleanValue()) {
                HydrogenClient.h().d().v().g().a(7);
            }
        }
    }

    @EventTarget
    public void a(PacketEvent event) {
        if (this.b.l("FunTime") && this.d.c().booleanValue() && event.c()) {
            GameMessageS2CPacket class_7439VarD = (GameMessageS2CPacket) event.d();
            if (class_7439VarD instanceof GameMessageS2CPacket) {
                GameMessageS2CPacket messageS2CPacket = class_7439VarD;
                if (messageS2CPacket.content().getString().equals("Данная команда недоступна в режиме AFK")) {
                    HydrogenClient.h().d().v().g().a(7);
                }
            }
        }
    }
}
