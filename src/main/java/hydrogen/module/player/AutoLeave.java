package hydrogen.module.player;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;
import hydrogen.util.ChatUtil;
import hydrogen.util.ServerUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;
import hydrogen.setting.BooleanSetting;

import hydrogen.setting.MultiModeSetting;
import hydrogen.setting.SliderSetting;
import net.minecraft.entity.player.PlayerEntity;

@ModuleRegister(a = "Auto Leave", b = "Автоматически выходит в хаб по триггерам", c = Category.Player)
public class AutoLeave extends Module {
    private final MultiModeSetting b = new MultiModeSetting("Условия срабатывания", new BooleanSetting("Малое ХП", true), new BooleanSetting("Игроки рядом", true));
    private final SliderSetting c = (SliderSetting) new SliderSetting("Минимум ХП", 8.0f, 1.0f, 20.0f, 0.5f).a(() -> {
        return this.b.a("Малое ХП").c();
    });
    private final SliderSetting d = (SliderSetting) new SliderSetting("Дистанция игроков", 8.0f, 8.0f, 128.0f, 1.0f).a(() -> {
        return this.b.a("Игроки рядом").c();
    });

    public AutoLeave() {
        a(this.b, this.c, this.d);
    }

    @EventTarget
    public void a(TickEvent event) {
        if (aM_.world.getRegistryKey().getValue().toString().equals("minecraft:lobby")) {
            return;
        }
        if (ServerUtil.a.a() && ServerUtil.a.d() == -1) {
            return;
        }
        PlayerEntity near = (PlayerEntity) aM_.world.getPlayers().stream().filter(player -> {
            return (player == aM_.player || aM_.player.squaredDistanceTo(player) > ((double) (this.d.c().floatValue() * this.d.c().floatValue())) || HydrogenClient.h().d().e().d(player.getName().getString())) ? false : true;
        }).findFirst().orElse(null);
        if (((this.b.a("Малое ХП").c().booleanValue() && aM_.player.getHealth() <= this.c.c().floatValue()) || (this.b.a("Игроки рядом").c().booleanValue() && near != null)) && !ServerUtil.e()) {
            aM_.player.networkHandler.sendChatCommand("hub");
            if (near != null) {
                ChatUtil.a((Object) ("Покинул анархию: рядом игрок &c" + near.getName().getString() + "&7 в &c" + Math.round(Math.sqrt(aM_.player.squaredDistanceTo(near))) + "&7 блоках."));
            } else {
                ChatUtil.a((Object) "Покинул &cанархию: критически мало здоровья&7.");
            }
            a();
        }
    }
}
