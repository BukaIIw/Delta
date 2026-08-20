package hydrogen.module.misc;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;
import hydrogen.util.ServerUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;

import hydrogen.setting.BooleanSetting;
import lombok.Generated;

@ModuleRegister(a = "Chat Helper", b = "Расширяет возможности чата и его настройки", c = Category.Misc)
public class ChatHelper extends Module {
    private final BooleanSetting b = new BooleanSetting("Ширина под сообщение", false);
    private final BooleanSetting c = new BooleanSetting("Автоматическое /event delay", false);
    private int d = -1;

    @Generated
    public BooleanSetting q() {
        return this.b;
    }

    public ChatHelper() {
        a(this.b, this.c);
    }

    @EventTarget
    public void a(TickEvent event) {
        int iB;
        if (this.c.c().booleanValue()) {
            if (ServerUtil.a.a()) {
                iB = ServerUtil.a.d();
            } else {
                iB = ServerUtil.d.a() ? ServerUtil.d.b() : -1;
            }
            int currentAnarchy = iB;
            if (currentAnarchy == -1) {
                this.d = 0;
                return;
            }
            if (this.d == -1) {
                this.d = currentAnarchy;
            } else if (currentAnarchy != this.d && aM_.player.age >= 5) {
                aM_.player.networkHandler.sendChatCommand("event delay");
                this.d = currentAnarchy;
            }
        }
    }
}
