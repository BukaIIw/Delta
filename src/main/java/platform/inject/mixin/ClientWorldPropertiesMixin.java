package platform.inject.mixin;


import hydrogen.event.AmbienceEvent;
import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ClientWorld.Properties.class})
public class ClientWorldPropertiesMixin {
    @ModifyReturnValue(method = {"getTimeOfDay"}, at = {@At("RETURN")})
    private long getTimeOfDay(long original) {
        AmbienceEvent.c event = new AmbienceEvent.c(original);
        EventManager.a((IEvent) event);
        return event.b();
    }
}
