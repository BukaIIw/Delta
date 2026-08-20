package platform.inject.mixin;

import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.event.TooltipEvent;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Screen.class})
public class ScreenMixin {
    @Inject(method = {"getTooltipFromItem"}, at = {@At("RETURN")})
    private static void getTooltipFromItem(MinecraftClient client, ItemStack stack, CallbackInfoReturnable<List<Text>> cir) {
        EventManager.a((IEvent) new TooltipEvent(stack, (List) cir.getReturnValue()));
    }
}
