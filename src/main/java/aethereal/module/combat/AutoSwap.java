package aethereal.module.combat;

import static aethereal.core.Interface.aM_;
import aethereal.core.Delta;
import aethereal.core.Module;
import aethereal.util.InventoryUtil;

import aethereal.core.Category;
import aethereal.core.Interface;
import aethereal.core.ModuleRegister;

import aethereal.setting.BindSetting;
import aethereal.setting.ModeSetting;
import aethereal.ui.screen.SwapScreen;
import lombok.Generated;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.Screen;

@ModuleRegister(a = "Auto Swap", b = "Мгновенно перекладывает выбранные предметы во вторую руку по нажатию клавиши", c = Category.Combat)
public class AutoSwap extends Module implements Interface {
    private final ModeSetting b = new ModeSetting("Режим перемещения", "Двойной", "Двойной", "Тройной");
    private final ModeSetting c = (ModeSetting) new ModeSetting("Первый предмет", "Сфера", "Сфера", "Тотем", "Золотое яблоко", "Щит").a(() -> {
        return Boolean.valueOf(this.b.l("Двойной"));
    });
    private final ModeSetting d = (ModeSetting) new ModeSetting("Второй предмет", "Тотем", "Сфера", "Тотем", "Золотое яблоко", "Щит").a(() -> {
        return Boolean.valueOf(this.b.l("Двойной"));
    });
    private final SwapScreen e = new SwapScreen(Text.literal("SwapMenu"));
    private final BindSetting f = new BindSetting("Кнопка перемещения", 86, 0).a(() -> {
        if (Delta.h().d().t().V().b) {
            return;
        }
        if (this.b.l("Двойной")) {
            Delta.h().d().v().a().a(InventoryUtil.c(aM_.player.getOffHandStack().getItem() == a(this.c) ? a(this.d) : a(this.c)), 45, 1);
        } else if (this.b.l("Тройной")) {
            aM_.setScreen(this.e);
        }
    }).b(() -> {
        if (aM_.currentScreen instanceof SwapScreen) {
            aM_.setScreen((Screen) null);
        }
    });

    @Generated
    public SwapScreen q() {
        return this.e;
    }

    public AutoSwap() {
        a(this.f, this.b, this.c, this.d);
    }

    private Item a(ModeSetting modeSetting) {
        switch (modeSetting.c()) {
            case "Сфера":
                return Items.PLAYER_HEAD;
            case "Тотем":
                return Items.TOTEM_OF_UNDYING;
            case "Золотое яблоко":
                return Items.GOLDEN_APPLE;
            case "Щит":
                return Items.SHIELD;
            default:
                return null;
        }
    }
}
