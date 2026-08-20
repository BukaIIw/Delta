package platform.inject.mixin;


import hydrogen.core.HydrogenClient;
import hydrogen.module.misc.StreamerMode;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({SkinTextures.class})
public class SkinTexturesMixin {
    @Inject(method = {"texture"}, at = {@At("HEAD")}, cancellable = true)
    public void texture(CallbackInfoReturnable<Identifier> cir) {
        StreamerMode streamerMode = HydrogenClient.h().d().t().aE();
        if (streamerMode.m() && streamerMode.q().c().booleanValue()) {
            cir.setReturnValue(Identifier.of("hydrogen", "pictures/skin.png"));
        }
    }
}
