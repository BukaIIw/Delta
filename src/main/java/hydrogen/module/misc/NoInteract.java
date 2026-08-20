package hydrogen.module.misc;

import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.ModuleRegister;

import hydrogen.setting.BooleanSetting;
import lombok.Generated;

@ModuleRegister(a = "No Interact", b = "Блокирует случайное взаимодействие с контейнерами и блоками", c = Category.Misc)
public class NoInteract extends Module {
    private final BooleanSetting b = new BooleanSetting("Учитывать включённую Aura", true);

    @Generated
    public BooleanSetting q() {
        return this.b;
    }

    public NoInteract() {
        a(this.b);
    }
}
