package platform.inject.mixin;


import hydrogen.render.AnimationUtil;
import hydrogen.mixin.IStatusEffectInstance;
import lombok.Generated;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({StatusEffectInstance.class})
public abstract class StatusEffectInstanceMixin implements IStatusEffectInstance {

    @Shadow
    public abstract int getDuration();

    @Shadow
    public abstract int getAmplifier();

    @Shadow
    public abstract RegistryEntry<StatusEffect> getEffectType();

    @Unique
    private final AnimationUtil animation = new AnimationUtil();

    @Unique
    private int initialDuration;

    @Override
    @Generated
    public AnimationUtil getAnimation() {
        return this.animation;
    }

    @Override
    @Generated
    public int getInitialDuration() {
        return this.initialDuration;
    }

    @Override
    @Generated
    public void setInitialDuration(int initialDuration) {
        this.initialDuration = initialDuration;
    }

    @Inject(method = {"<init>*"}, at = {@At("TAIL")})
    private void onInit(CallbackInfo ci) {
        this.initialDuration = ((StatusEffectInstance)(Object) this).getDuration();
    }
}
