package platform.inject.mixin;


import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.event.RemovalsEvent;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BossBarHud.class})
public class BossBarHudMixin {
    @Inject(method = {"render"}, at = {@At("HEAD")}, cancellable = true)
    private void render(CallbackInfo ci) {
        RemovalsEvent event = new RemovalsEvent(RemovalsEvent.a.BOSS_BAR);
        EventManager.a((IEvent) event);
        if (event.a()) {
            ci.cancel();
        }
    }
}
