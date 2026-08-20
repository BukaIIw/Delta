package hydrogen.module.player;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;
import hydrogen.core.ModuleRegister;
import hydrogen.event.TickEvent;
import hydrogen.setting.BooleanSetting;
import lombok.Generated;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ReconfiguringScreen;

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

    @EventTarget
    public void onTick(TickEvent event) {
        if (!skipTerrain() || aM_ == null || aM_.player == null || aM_.world == null || aM_.currentScreen == null) {
            return;
        }
        if (aM_.currentScreen instanceof DownloadingTerrainScreen || aM_.currentScreen instanceof ReconfiguringScreen) {
            aM_.setScreen(null);
        }
    }

    public static FastLoad current() {
        try {
            if (HydrogenClient.h() == null || HydrogenClient.h().d() == null || HydrogenClient.h().d().t() == null) {
                return null;
            }
            return HydrogenClient.h().d().t().aN();
        } catch (Exception ignored) {
            return null;
        }
    }

    public static boolean shouldSkipTerrain() {
        FastLoad load = current();
        return load != null && load.skipTerrain();
    }

    public static boolean shouldSkipResourceReload() {
        FastLoad load = current();
        return load != null && load.skipResourceReload();
    }

    public static boolean shouldSkipLanguageReload() {
        FastLoad load = current();
        return load != null && load.skipLanguageReload();
    }
}
