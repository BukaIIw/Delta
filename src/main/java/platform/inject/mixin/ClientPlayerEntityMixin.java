package platform.inject.mixin;


import static hydrogen.core.Interface.aM_;

import hydrogen.event.DropItemEvent;
import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.event.InputEvent;
import hydrogen.core.Interface;
import hydrogen.util.Look;
import hydrogen.event.MotionEvent;
import hydrogen.util.MoveUtil;
import hydrogen.event.PushEvent;
import hydrogen.event.SlowEvent;
import hydrogen.event.TickEvent;
import net.minecraft.util.PlayerInput;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin {
    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void tick(CallbackInfo ci) {
        EventManager.a((IEvent) new TickEvent());
    }

    @Redirect(method = {"tickMovement"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick()V"))
    private void tickMovement(Input input) {
        input.tick();
        InputEvent event = new InputEvent(input.movementForward, input.movementSideways, input.playerInput.jump(), input.playerInput.sneak());
        EventManager.a((IEvent) event);
        MoveUtil.a(event);
        input.movementForward = event.b();
        input.movementSideways = event.c();
        input.playerInput = new PlayerInput(event.b() > 0.0f, event.b() < 0.0f, event.c() > 0.0f, event.c() < 0.0f, event.d(), event.e(), input.playerInput.sprint());
    }

    @Inject(method = {"sendMovementPackets"}, at = {@At("HEAD")})
    private void onSendMovementPackets(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (ClientPlayerEntity)(Object) this;
        EventManager.a((IEvent) new MotionEvent(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), player.isOnGround(), player.isSneaking(), player.isSprinting()));
    }

    @Redirect(method = {"tickNewAi"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    private float pitchAi(ClientPlayerEntity instance) {
        return Look.c();
    }

    @Redirect(method = {"tickNewAi"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    private float yawAi(ClientPlayerEntity instance) {
        return Look.b();
    }

    @Inject(method = {"dropSelectedItem"}, at = {@At("HEAD")}, cancellable = true)
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        DropItemEvent dropItemEvent = new DropItemEvent(Interface.aM_.player.getInventory().selectedSlot);
        EventManager.a((IEvent) dropItemEvent);
        if (dropItemEvent.a()) {
            cir.cancel();
        }
    }

    @Redirect(method = {"tickMovement"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require = 0)
    private boolean onTickMovement(ClientPlayerEntity player) {
        if (!player.isUsingItem()) {
            return player.isUsingItem() && player.getVehicle() == null;
        }
        SlowEvent slowEvent = new SlowEvent();
        EventManager.a((IEvent) slowEvent);
        return player.isUsingItem() && player.getVehicle() == null && !slowEvent.a();
    }

    @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
    public void removePushOutFromBlocks(double x, double z, CallbackInfo ci) {
        PushEvent event = new PushEvent(PushEvent.a.BLOCKS);
        EventManager.a((IEvent) event);
        if (event.a()) {
            ci.cancel();
        }
    }
}
