package platform.inject.mixin;


import static hydrogen.core.Interface.aM_;

import hydrogen.core.HydrogenClient;
import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.core.Interface;
import hydrogen.event.TooltipEvent;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Screen.class})
public class ScreenMixin {
    @Inject(method = {"render"}, at = {@At("HEAD")}, cancellable = true)
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen self = (Screen)(Object) this;
        if ((self instanceof ReconfiguringScreen || self instanceof DownloadingTerrainScreen)
                && hydrogen.module.player.FastLoad.shouldSkipTerrain()) {
            if (Interface.aM_ != null && Interface.aM_.currentScreen == self) {
                Interface.aM_.setScreen(null);
            }
            ci.cancel();
        }
    }

    @Inject(method = {"getTooltipFromItem"}, at = {@At("RETURN")})
    private static void getTooltipFromItem(MinecraftClient client, ItemStack stack, CallbackInfoReturnable<List<Text>> cir) {
        EventManager.a((IEvent) new TooltipEvent(stack, (List) cir.getReturnValue()));
    }
}
