package platform.inject.mixin;


import static hydrogen.core.Interface.aM_;

import hydrogen.ui.screen.AltScreen;
import hydrogen.core.Interface;
import hydrogen.core.InterfaceC0020Opcode;
import hydrogen.ui.screen.MainScreen;
import net.minecraft.client.option.InactivityFpsLimiter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({InactivityFpsLimiter.class})
public class InactivityFpsLimiterMixin {
    @Inject(method = {"update"}, at = {@At("HEAD")}, cancellable = true)
    private void onUpdate(CallbackInfoReturnable<Integer> cir) {
        if ((Interface.aM_.currentScreen instanceof MainScreen) || (Interface.aM_.currentScreen instanceof AltScreen)) {
            cir.setReturnValue(Integer.valueOf(InterfaceC0020Opcode.aN));
        }
    }
}
