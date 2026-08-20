package hydrogen.handler;

import hydrogen.handler.Handler_2;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.util.ChatUtil;

import hydrogen.core.EventTarget;
import hydrogen.core.Interface;
import hydrogen.event.ClickEvent;
import hydrogen.event.HotbarEvent;
import hydrogen.event.TickEvent;
import hydrogen.handler.BaseHandler;
import hydrogen.handler.InventoryHandler;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.util.Hand;

@Handler_2
public class InteractHandler extends BaseHandler implements Interface {
    private final List<a> b = new ArrayList();

    @Generated
    public List<a> b() {
        return this.b;
    }

    public void a(int slot) {
        if (this.b.isEmpty() && HydrogenClient.h().d().v().a().a().isEmpty()) {
            this.b.add(new a(slot));
        }
    }

    public boolean a() {
        return !this.b.isEmpty();
    }

    @EventTarget
    public void a(TickEvent event) {
        if (!this.b.isEmpty() && aM_.player.age > 40) {
            InventoryHandler inventoryHandler = HydrogenClient.h().d().v().a();
            a task = (a) this.b.getFirst();
            boolean inventory = task.b() > 8;
            task.a(task.d() + 1);
            if (task.d() == 1) {
                if (inventory) {
                    inventoryHandler.a(task.b(), task.a(), 2);
                } else {
                    aM_.player.getInventory().selectedSlot = task.b();
                }
            } else if (!task.c() && task.d() > 0 && inventoryHandler.a().isEmpty()) {
                if (aM_.player.isUsingItem()) {
                    task.a(true);
                } else {
                    aM_.interactionManager.interactItem(aM_.player, Hand.MAIN_HAND);
                }
            } else if (task.c() && !aM_.player.isUsingItem() && inventoryHandler.a().isEmpty()) {
                if (inventory) {
                    inventoryHandler.a(task.a(), task.b(), 2);
                } else {
                    aM_.player.getInventory().selectedSlot = task.a();
                }
                this.b.remove(task);
            }
            if (task.d() >= 60) {
                ChatUtil.a((Object) "Использование предмета не удалось по неизвестной причине");
                this.b.remove(task);
            }
        }
    }

    @EventTarget
    public void a(HotbarEvent event) {
        if (a()) {
            event.a(true);
        }
    }

    @EventTarget
    public void a(ClickEvent event) {
        if (a() && event.h() == 1) {
            event.a(true);
        }
    }

    public static final class a {
        private final int a = Interface.aM_.player.getInventory().selectedSlot;
        private final int b;
        private boolean c;
        private int d;

        @Generated
        public void a(boolean returned) {
            this.c = returned;
        }

        @Generated
        public void a(int ticks) {
            this.d = ticks;
        }

        @Generated
        public int a() {
            return this.a;
        }

        @Generated
        public int b() {
            return this.b;
        }

        @Generated
        public boolean c() {
            return this.c;
        }

        @Generated
        public int d() {
            return this.d;
        }

        public a(int eatSlot) {
            this.b = eatSlot;
        }
    }
}
