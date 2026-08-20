package hydrogen.module.combat;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;
import hydrogen.util.InventoryUtil;

import hydrogen.core.Category;
import hydrogen.core.Interface;
import hydrogen.core.ModuleRegister;

import hydrogen.setting.BindSetting;
import java.util.List;
import lombok.Generated;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

@ModuleRegister(a = "Item Helper", b = "Перемещает нужный предмет и возвращает его обратно по нажатию клавиши", c = Category.Combat)
public class ItemHelper extends Module implements Interface {
    public ItemHelper() {
        List.of(new a(this, "Зачарованное яблоко", Items.ENCHANTED_GOLDEN_APPLE), new a(this, "Золотое яблоко", Items.GOLDEN_APPLE), new a(this, "Плод хоруса", Items.CHORUS_FRUIT), new a(this, "Арбалет", Items.CROSSBOW)).forEach(item -> {
            a(item.b());
        });
    }

    final class a {
        private final Item a;
        private final BindSetting b;
        private int c = -1;
        private int d = -1;

        @Generated
        public Item a() {
            return this.a;
        }

        @Generated
        public BindSetting b() {
            return this.b;
        }

        @Generated
        public int c() {
            return this.c;
        }

        @Generated
        public int d() {
            return this.d;
        }

        a(ItemHelper itemHelper, String name, Item item) {
            this.a = item;
            this.b = new BindSetting(name, -1, 1).a(this::e);
        }

        private void e() {
            if (this.d == -1) {
                f();
            } else {
                g();
            }
        }

        private void f() {
            if (this.d == -1) {
                int from = InventoryUtil.b(this.a);
                int target = Interface.aM_.player.getInventory().selectedSlot;
                if (from != -1 && from != target) {
                    this.d = from;
                    this.c = target;
                    HydrogenClient.h().d().v().a().a(from, target, 1);
                }
            }
        }

        private void g() {
            if (this.d != -1) {
                HydrogenClient.h().d().v().a().a(this.d, this.c, 1);
                this.d = -1;
                this.c = -1;
            }
        }
    }
}
