package platform.inject.mixin;


import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.event.PushEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FishingBobberEntity.class})
public class FishingBobberEntityMixin {
    @Inject(method = {"pullHookedEntity"}, at = {@At("HEAD")}, cancellable = true)
    private void pullHookedEntity(Entity entity, CallbackInfo ci) {
        if (entity instanceof ClientPlayerEntity) {
            PushEvent event = new PushEvent(PushEvent.a.FISHING_HOOK);
            EventManager.a((IEvent) event);
            if (event.a()) {
                ci.cancel();
            }
        }
    }
}
