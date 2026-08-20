package platform.inject.mixin;


import hydrogen.event.AmbienceEvent;
import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.event.RemovalsEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Fog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BackgroundRenderer.class})
public class BackgroundRendererMixin {
    @Inject(method = {"getFogModifier(Lnet/minecraft/entity/Entity;F)Lnet/minecraft/client/render/BackgroundRenderer$StatusEffectFogModifier;"}, at = {@At("HEAD")}, cancellable = true)
    private static void onGetFogModifier(Entity entity, float tickDelta, CallbackInfoReturnable<Object> info) {
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            if (living.hasStatusEffect(StatusEffects.BLINDNESS)) {
                RemovalsEvent event = new RemovalsEvent(RemovalsEvent.a.BLINDNESS);
                EventManager.a((IEvent) event);
                if (event.a()) {
                    info.setReturnValue((Object) null);
                    return;
                }
            }
            if (living.hasStatusEffect(StatusEffects.DARKNESS)) {
                RemovalsEvent event2 = new RemovalsEvent(RemovalsEvent.a.DARKNESS);
                EventManager.a((IEvent) event2);
                if (event2.a()) {
                    info.setReturnValue((Object) null);
                }
            }
        }
    }

    @ModifyReturnValue(method = {"applyFog"}, at = {@At("RETURN")})
    private static Fog onApplyFog(Fog original, @Local(argsOnly = true) Camera camera, @Local(argsOnly = true, ordinal = 0) float viewDistance) {
        AmbienceEvent.b event = new AmbienceEvent.b(camera, viewDistance, original);
        EventManager.a((IEvent) event);
        return event.d();
    }
}
