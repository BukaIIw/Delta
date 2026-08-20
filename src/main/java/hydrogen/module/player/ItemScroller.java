package hydrogen.module.player;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.ModuleRegister;

import hydrogen.util.CounterUtil;
import hydrogen.setting.SliderSetting;
import lombok.Generated;

@ModuleRegister(a = "Item Scroller", b = "Позволяет быстро перекладывать предметы в окнах прокруткой", c = Category.Player)
public class ItemScroller extends Module {
    private final SliderSetting b = new SliderSetting("Задержка между слотами", 50.0f, 0.0f, 100.0f, 1.0f);
    private final CounterUtil c = new CounterUtil();

    @Generated
    public SliderSetting q() {
        return this.b;
    }

    @Generated
    public CounterUtil r() {
        return this.c;
    }

    public ItemScroller() {
        a(this.b);
    }
}
