package hydrogen.module.player;

import hydrogen.core.Module;
import hydrogen.util.ChatUtil;
import hydrogen.util.ServerUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.DropItemEvent;

import hydrogen.setting.BooleanSetting;
import hydrogen.setting.MultiModeSetting;

@ModuleRegister(a = "Lock Slot", b = "Запрещает выбрасывать предметы из выбранных слотов", c = Category.Player)
public class LockSlot extends Module {
    private final MultiModeSetting b = new MultiModeSetting("Заблокированные слоты", new BooleanSetting("1", false), new BooleanSetting("2", false), new BooleanSetting("3", false), new BooleanSetting("4", false), new BooleanSetting("5", false), new BooleanSetting("6", false), new BooleanSetting("7", false), new BooleanSetting("8", false), new BooleanSetting("9", false));
    private final BooleanSetting c = new BooleanSetting("Блокировать только в PVP", true);

    public LockSlot() {
        a(this.c, this.b);
    }

    @EventTarget
    public void a(DropItemEvent event) {
        if ((!this.c.c().booleanValue() || ServerUtil.e()) && this.b.a(event.b()).c().booleanValue()) {
            ChatUtil.a((Object) ("Попытка выброса из слота \"&c" + (event.b() + 1) + "&7\" была заблокирована"));
            event.a(true);
        }
    }
}
