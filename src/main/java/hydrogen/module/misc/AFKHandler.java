package hydrogen.module.misc;

import hydrogen.handler.Handler_2;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.util.MathUtil;

import hydrogen.core.EventTarget;
import hydrogen.core.Interface;
import hydrogen.event.InputEvent;
import hydrogen.event.TickEvent;
import hydrogen.handler.BaseHandler;
import hydrogen.util.Rotation;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Generated;

@Handler_2
public class AFKHandler extends BaseHandler implements Interface {
    private int b = -1;

    @Generated
    public int b() {
        return this.b;
    }

    public void a(int ticks) {
        this.b = ticks;
    }

    public boolean a() {
        return this.b > 0;
    }

    @EventTarget
    public void a(TickEvent event) {
        if (this.b > 0) {
            this.b--;
        }
    }

    @EventTarget
    public void a(InputEvent e) {
        if (this.b > 0) {
            e.a(ThreadLocalRandom.current().nextBoolean() ? 1.0f : -1.0f);
            e.b(ThreadLocalRandom.current().nextBoolean() ? 1.0f : -1.0f);
            HydrogenClient.h().d().k().a(new Rotation(aM_.player.getYaw() + MathUtil.a(-2.0f, 2.0f), MathUtil.b(aM_.player.getPitch() + MathUtil.a(-1.0f, 1.0f), -90.0f, 90.0f)), 150.0f, 10, 1);
        }
    }
}
