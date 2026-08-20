package hydrogen.module.render;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.ModuleRegister;

import hydrogen.setting.BooleanSetting;
import lombok.Generated;

@ModuleRegister(a = "Item Physic", b = "Добавляет физику предметам, лежащим на земле", c = Category.Render)
public class ItemPhysic extends Module {
    private final BooleanSetting b = new BooleanSetting("Уменьшить размер предметов", false);

    @Generated
    public BooleanSetting q() {
        return this.b;
    }

    public ItemPhysic() {
        a(this.b);
    }
}
