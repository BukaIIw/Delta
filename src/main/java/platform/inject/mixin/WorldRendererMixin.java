package platform.inject.mixin;


import hydrogen.core.HydrogenClient;
import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.core.Interface;
import hydrogen.event.RemovalsEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Fog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldRenderer.class})
public class WorldRendererMixin implements Interface {
    @Inject(method = {"renderWeather"}, at = {@At("HEAD")}, cancellable = true)
    private void onRenderWeather(FrameGraphBuilder frameGraphBuilder, Vec3d pos, float tickDelta, Fog fog, CallbackInfo ci) {
        RemovalsEvent event = new RemovalsEvent(RemovalsEvent.a.WEATHER);
        EventManager.a((IEvent) event);
        if (event.a()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = {"setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V"}, at = @At("HEAD"), argsOnly = true, index = 4)
    private boolean onSetupTerrain(boolean spectator) {
        return HydrogenClient.h().d().t().h().m() || spectator;
    }
}
