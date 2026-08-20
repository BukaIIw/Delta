package hydrogen.module.player;

import hydrogen.core.Category;
import hydrogen.core.Module;
import hydrogen.core.ModuleRegister;
import hydrogen.setting.BooleanSetting;
import lombok.Generated;

@ModuleRegister(a = "Fast Load", b = "Ускоряет загрузку мира", c = Category.Player)
public class FastLoad extends Module {
    private final BooleanSetting world = new BooleanSetting("Ускоряет загрузку мира", true);
    private final BooleanSetting resourcePacks = new BooleanSetting("Фаст РП", true);
    private final BooleanSetting language = new BooleanSetting("Фаст смена языка", true);

    public FastLoad() {
        a(this.world, this.resourcePacks, this.language);
    }

    @Generated
    public BooleanSetting q() {
        return this.world;
    }

    @Generated
    public BooleanSetting r() {
        return this.resourcePacks;
    }

    @Generated
    public BooleanSetting s() {
        return this.language;
    }

    public boolean skipTerrain() {
        return m() && this.world.c().booleanValue();
    }

    public boolean skipResourceReload() {
        return m() && this.resourcePacks.c().booleanValue();
    }

    public boolean skipLanguageReload() {
        return m() && this.language.c().booleanValue();
    }
}
