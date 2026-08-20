package platform.inject.mixin;


import static hydrogen.core.Interface.aM_;

import hydrogen.core.HydrogenClient;
import hydrogen.core.EventManager;
import hydrogen.ui.screen.GUIScreen;
import hydrogen.core.GlobalEvent;
import hydrogen.event.HotbarEvent;
import hydrogen.core.IEvent;
import hydrogen.core.Interface;
import hydrogen.core.InterfaceC0020Opcode;
import hydrogen.module.player.FastLoad;
import hydrogen.module.player.OpenWalls;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClientMixin implements Interface {

    @Unique
    private static final String HYDROGEN_WINDOW_TITLE = "HydrogenDLC 4.0";

    @Unique
    private Set<String> resourcePacks;

    @Unique
    private boolean skipNextResourceReload;

    @Inject(method = {"getWindowTitle"}, at = {@At("HEAD")}, cancellable = true)
    private void getWindowTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(HYDROGEN_WINDOW_TITLE);
    }

    @Inject(method = {"setScreen"}, at = {@At("HEAD")}, cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (aM_.currentScreen instanceof PackScreen && !(screen instanceof PackScreen) && FastLoad.shouldSkipResourceReload()) {
            this.skipNextResourceReload = true;
        }
        if (screen instanceof PackScreen) {
            this.resourcePacks = aM_.getResourcePackManager().getEnabledProfiles().stream()
                    .map(profile -> profile.getId())
                    .collect(Collectors.toCollection(HashSet::new));
        }
        if (aM_.currentScreen instanceof GUIScreen) {
            if (screen == null || screen instanceof DownloadingTerrainScreen) {
                for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                    if (element.getClassName().equals(Screen.class.getName()) || element.getClassName().equals(Keyboard.class.getName())) {
                        return;
                    }
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = {"reloadResources()Ljava/util/concurrent/CompletableFuture;"}, at = {@At("HEAD")}, cancellable = true)
    private void reloadResources(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        if (FastLoad.shouldSkipLanguageReload() && fromLanguageChange()) {
            cir.setReturnValue(CompletableFuture.completedFuture(null));
            return;
        }
        if (this.skipNextResourceReload) {
            this.skipNextResourceReload = false;
            this.resourcePacks = null;
            cir.setReturnValue(CompletableFuture.completedFuture(null));
            return;
        }
        if (this.resourcePacks != null) {
            Set<String> current = aM_.getResourcePackManager().getEnabledProfiles().stream()
                    .map(profile -> profile.getId())
                    .collect(Collectors.toSet());
            if (this.resourcePacks.equals(current)) {
                cir.setReturnValue(CompletableFuture.completedFuture(null));
            }
            this.resourcePacks = null;
        }
    }

    @Unique
    private boolean fromLanguageChange() {
        for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
            String name = element.getClassName();
            if (name.contains("LanguageManager") || name.contains("LanguageOptions")
                    || name.contains("LanguageSelection") || name.contains("I18n")) {
                return true;
            }
        }
        return false;
    }

    @Inject(method = {"tick"}, at = {@At("HEAD")})
    private void onGlobalTick(CallbackInfo ci) {
        EventManager.a((IEvent) new GlobalEvent());
    }

    @Redirect(method = {"handleInputEvents"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    private void handleInputEvents(ClientPlayerInteractionManager manager, PlayerEntity player) {
        if (HydrogenClient.h().d().v().k().a()) {
            return;
        }
        if (player.isUsingItem()) {
            ItemStack stack = player.getActiveItem();
            if ((stack.getItem() instanceof CrossbowItem) && stack.getItem().getMaxUseTime(stack, player) - player.getItemUseTimeLeft() <= CrossbowItem.getPullTime(stack, player)) {
                return;
            }
        }
        manager.stopUsingItem(player);
    }

    @Inject(method = {"handleInputEvents"}, at = {@At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = InterfaceC0020Opcode.cQ)}, cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void handleInputEvents(CallbackInfo ci, int i) {
        HotbarEvent event = new HotbarEvent(i);
        EventManager.a((IEvent) event);
        if (event.a()) {
            ci.cancel();
        }
    }

    @Inject(method = {"doItemUse"}, at = {@At("HEAD")}, cancellable = true)
    private void doItemUse(CallbackInfo ci) {
        ItemStack stack = aM_.player.getStackInHand(Hand.MAIN_HAND);
        if ((stack.getItem() instanceof PotionItem) && aM_.player.getItemCooldownManager().isCoolingDown(stack)) {
            ci.cancel();
        }
        if ((stack.getItem() instanceof CrossbowItem) && CrossbowItem.isCharged(stack) && aM_.player.getItemCooldownManager().isCoolingDown(stack)) {
            ci.cancel();
        }
    }

    @Redirect(method = {"doItemUse"}, at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;", opcode = InterfaceC0020Opcode.aK))
    private HitResult doItemUse(MinecraftClient instance) {
        OpenWalls openWalls = HydrogenClient.h().d().t().a();
        if (!openWalls.m()) {
            return instance.crosshairTarget;
        }
        BlockHitResult hit = openWalls.a((ClientPlayerEntity) Objects.requireNonNull(instance.player));
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            return hit;
        }
        return instance.crosshairTarget;
    }
}
