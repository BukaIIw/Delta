package platform.inject.mixin;


import static aethereal.core.Interface.aM_;

import aethereal.render.ColorUtil;
import aethereal.core.Delta;
import aethereal.core.Interface;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntityRenderer.class})
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Unique
    private T currentEntity;

    @Unique
    private float pitch;

    @Inject(method = {"updateRenderState*"}, at = {@At("HEAD")})
    private void onUpdateRenderState(T entity, S state, float f, CallbackInfo ci) {
        this.currentEntity = entity;
    }

    @Inject(method = {"updateRenderState*"}, at = {@At("TAIL")})
    private void updateRenderState(T entity, S state, float f, CallbackInfo ci) {
        if (entity == Interface.aM_.player) {
            this.pitch = Delta.h().d().k().a().a() ? MathHelper.lerp(0.5f, this.pitch, ((LivingEntityRenderState) state).pitch) : ((LivingEntityRenderState) state).pitch;
            ((LivingEntityRenderState) state).pitch = this.pitch;
        }
    }

    @Inject(method = {"isVisible"}, at = {@At("HEAD")}, cancellable = true)
    private void onIsVisible(S state, CallbackInfoReturnable<Boolean> cir) {
        if (Delta.h().d().t().T().m() && ((LivingEntityRenderState) state).invisible && (this.currentEntity instanceof PlayerEntity)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyReturnValue(method = {"getMixColor"}, at = {@At("RETURN")})
    private int modifyMixColor(int original, S state) {
        if (Delta.h().d().t().T().m() && ((LivingEntityRenderState) state).invisible && (this.currentEntity instanceof PlayerEntity)) {
            return ColorUtil.a(original, Delta.h().d().t().T().q());
        }
        return original;
    }
}
