package hydrogen.module.player;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;
import hydrogen.util.ChatUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.Interface;
import hydrogen.core.ModuleRegister;
import hydrogen.event.HotbarEvent;
import hydrogen.event.PacketEvent;
import hydrogen.event.TickEvent;

import hydrogen.util.CounterUtil;
import lombok.Generated;
import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

@ModuleRegister(a = "Auto Fish", b = "Автоматически ловит рыбу в AFK-режиме", c = Category.Player)
public class AutoFish extends Module implements Interface {
    private final CounterUtil b = new CounterUtil();
    private boolean c;

    @Generated
    public CounterUtil q() {
        return this.b;
    }

    @Generated
    public boolean r() {
        return this.c;
    }

    @Override
    public void b() {
        super.b();
        if (aM_.player != null && aM_.player.getInventory().getStack(aM_.player.getInventory().selectedSlot).getItem() == Items.FISHING_ROD) {
            if (aM_.player.fishHook == null) {
                d(false);
            }
            ChatUtil.a((Object) (j() + " активирован, удачной рыбалки!"));
        }
    }

    @EventTarget
    public void a(PacketEvent event) {
        PlaySoundS2CPacket class_2767VarD = (PlaySoundS2CPacket) event.d();
        if (class_2767VarD instanceof PlaySoundS2CPacket) {
            PlaySoundS2CPacket packet = class_2767VarD;
            if (((SoundEvent) packet.getSound().value()).id().equals(SoundEvents.ENTITY_FISHING_BOBBER_SPLASH.id()) && aM_.player.fishHook.squaredDistanceTo(packet.getX(), packet.getY(), packet.getZ()) <= 0.48999979194765847d && aM_.player.fishHook != null) {
                d(true);
                this.b.b();
            }
        }
    }

    @EventTarget
    public void a(TickEvent event) {
        if (this.b.a(450L) && this.c) {
            d(false);
        }
    }

    @EventTarget
    public void a(HotbarEvent event) {
        if (this.c) {
            event.a(true);
        }
    }

    public void d(boolean cast) {
        aM_.interactionManager.interactItem(aM_.player, Hand.MAIN_HAND);
        aM_.player.swingHand(Hand.MAIN_HAND);
        this.c = cast;
    }
}
