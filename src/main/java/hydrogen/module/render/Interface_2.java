package hydrogen.module.render;

import hydrogen.ui.widget.Widget;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Interface;
import hydrogen.core.Module;

import hydrogen.config.ThemeInfo;
import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.GlobalEvent;
import hydrogen.core.ModuleRegister;
import hydrogen.event.BackendEvent;
import hydrogen.event.DrawEvent;
import hydrogen.event.PacketEvent;
import hydrogen.setting.BooleanSetting;
import hydrogen.ui.widget.ArmorWidget;
import hydrogen.ui.widget.CooldownsWidget;
import hydrogen.ui.widget.EnvironmentWidget;
import hydrogen.ui.widget.HotkeysWidget;
import hydrogen.ui.widget.ItemsWidget;
import hydrogen.ui.widget.NotificationWidget;
import hydrogen.ui.widget.PotionWidget;
import hydrogen.ui.widget.StaffWidget;
import hydrogen.ui.widget.TargetWidget;
import hydrogen.ui.widget.WatermarkWidget;

import hydrogen.setting.ColorSetting;
import hydrogen.setting.MultiModeSetting;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

@ModuleRegister(a = "Interface", b = "Отображает выбранные элементы интерфейса на экране", c = Category.Render)
public class Interface_2 extends Module {
    private final ColorSetting b = new ColorSetting("Глобальный цвет интерфейса", Integer.valueOf(HydrogenClient.h().d().o().a(ThemeInfo.PRIMARY).a()));
    private final MultiModeSetting c = new MultiModeSetting("Элементы интерфейса", new BooleanSetting("Клавиши", true), new BooleanSetting("Таргет-худ", true), new BooleanSetting("Задержки", true), new BooleanSetting("Инфо-панель", true), new BooleanSetting("Уведомления", true), new BooleanSetting("Зелья", true), new BooleanSetting("Предметы", true), new BooleanSetting("Броня", true), new BooleanSetting("Стафф", true), new BooleanSetting("Окружение", true));
    private final List<Widget> d = new ArrayList();

    @Generated
    public List<Widget> q() {
        return this.d;
    }

    public Interface_2() {
        a(this.b, this.c);
        this.d.add(new ArmorWidget());
        this.d.add(new HotkeysWidget());
        this.d.add(new CooldownsWidget());
        this.d.add(new TargetWidget());
        this.d.add(new WatermarkWidget());
        this.d.add(new PotionWidget());
        this.d.add(new ItemsWidget());
        this.d.add(new NotificationWidget());
        this.d.add(new StaffWidget());
        this.d.add(new EnvironmentWidget());
    }

    @EventTarget
    public void a(DrawEvent event) {
        if (event.b()) {
            HydrogenClient.h().d().o().a(ThemeInfo.PRIMARY).a(this.b.c().intValue());
            for (Widget widget : this.d) {
                if (this.c.a(widget.j().j()).c().booleanValue()) {
                    widget.a(event);
                }
            }
        }
    }

    @EventTarget
    public void a(GlobalEvent event) {
        for (Widget widget : this.d) {
            if (this.c.a(widget.j().j()).c().booleanValue()) {
                widget.a(event);
            }
        }
    }

    @EventTarget
    public void a(PacketEvent event) {
        for (Widget widget : this.d) {
            if (this.c.a(widget.j().j()).c().booleanValue()) {
                widget.a(event);
            }
        }
    }

    @EventTarget
    public void a(BackendEvent event) {
        for (Widget widget : this.d) {
            if (this.c.a(widget.j().j()).c().booleanValue()) {
                widget.a(event);
            }
        }
    }
}
