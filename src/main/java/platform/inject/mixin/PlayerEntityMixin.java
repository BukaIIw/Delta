package platform.inject.mixin;


import static aethereal.core.Interface.aM_;

import aethereal.core.EventManager;
import aethereal.core.IEvent;
import aethereal.core.Interface;
import aethereal.event.PushEvent;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerEntity.class})
public class PlayerEntityMixin {
    @Inject(method = {"isPushedByFluids"}, at = {@At("HEAD")}, cancellable = true)
    private void removePushFromFluids(CallbackInfoReturnable<Boolean> cir) {
        if (((PlayerEntity)(Object) this) == Interface.aM_.player) {
            PushEvent event = new PushEvent(PushEvent.a.FLUIDS);
            EventManager.a((IEvent) event);
            if (event.a()) {
                cir.setReturnValue(false);
            }
        }
    }
}
