package platform.inject.mixin;


import static aethereal.core.Interface.aM_;

import aethereal.core.Delta;
import aethereal.core.EventManager;
import aethereal.core.IEvent;
import aethereal.core.Interface;
import aethereal.event.TooltipEvent;
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
        if (((self instanceof ReconfiguringScreen) || (self instanceof DownloadingTerrainScreen)) && Delta.h().d().t().aN().m()) {
            if (self instanceof DownloadingTerrainScreen) {
                Interface.aM_.setScreen((Screen) null);
            }
            ci.cancel();
        }
    }

    @Inject(method = {"getTooltipFromItem"}, at = {@At("RETURN")})
    private static void getTooltipFromItem(MinecraftClient client, ItemStack stack, CallbackInfoReturnable<List<Text>> cir) {
        EventManager.a((IEvent) new TooltipEvent(stack, (List) cir.getReturnValue()));
    }
}
