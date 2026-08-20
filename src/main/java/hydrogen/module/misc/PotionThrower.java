package hydrogen.module.misc;

import hydrogen.autobuy.AutoBuyEntry;
import hydrogen.autobuy.AutoBuyProcessor;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.Interface;
import hydrogen.core.ModuleRegister;

import hydrogen.ui.screen.AssistantScreen;
import hydrogen.setting.BindSetting;
import hydrogen.setting.ModeSetting;
import lombok.Generated;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.Screen;

@ModuleRegister(a = "Potion Thrower", b = "Быстрое метание бафов через колесо или по клавише", c = Category.Misc)
public class PotionThrower extends Module implements Interface {
    private final ModeSetting b = new ModeSetting("Способ использования зелий", "Колесо выбора", "Колесо выбора", "Клавиша");
    private final AssistantScreen c = new AssistantScreen(Text.literal("Potion Thrower"));
    private final BindSetting d = (BindSetting) new BindSetting("Открыть меню зелий", 86, 0).a(() -> {
        aM_.setScreen(this.c);
    }).b(() -> {
        if (aM_.currentScreen == this.c) {
            this.c.b(this.c.b());
            if (aM_.currentScreen == this.c) {
                aM_.setScreen((Screen) null);
            }
        }
    }).a(() -> {
        return Boolean.valueOf(this.b.l("Колесо выбора"));
    });

    @Generated
    public AssistantScreen q() {
        return this.c;
    }

    public PotionThrower() {
        a(this.b, this.d);
        for (AutoBuyEntry potion : AutoBuyEntry.values()) {
            if (potion.d() == Items.SPLASH_POTION) {
                a(new BindSetting(potion.b(), -1).a(() -> {
                    HydrogenClient.h().d().v().b().a(potion.a());
                }).a(() -> {
                    return Boolean.valueOf(this.b.l("Клавиша"));
                }));
            }
        }
    }
}
