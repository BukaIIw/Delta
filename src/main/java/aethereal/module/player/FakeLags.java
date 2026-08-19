package aethereal.module.player;

import static aethereal.core.Interface.aM_;
import aethereal.core.InterfaceC0020Opcode;
import aethereal.core.Module;
import aethereal.render.ColorUtil;

import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.Interface;
import aethereal.core.ModuleRegister;
import aethereal.event.AttackEvent;
import aethereal.event.DrawEvent;
import aethereal.event.PacketEvent;
import aethereal.event.TickEvent;

import aethereal.setting.BooleanSetting;
import aethereal.setting.SliderSetting;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import platform.inject.accessors.ClientConnectionAccessor;

@ModuleRegister(a = "Fake Lags", b = "Задерживает отправку пакетов, имитируя лаги на сервере", c = Category.Player)
public class FakeLags extends Module implements Interface {
    private final SliderSetting b = new SliderSetting("Задержка симуляции", 20.0f, 1.0f, 40.0f, 1.0f);
    private final BooleanSetting c = new BooleanSetting("Отображать серв-позицию", false);
    private final Queue<Packet<?>> d = new ConcurrentLinkedQueue();
    private int e;
    private int f;
    private Vec3d g;

    public FakeLags() {
        a(this.b, this.c);
    }

    @Override
    public void b() {
        super.b();
        this.d.clear();
        this.e = 0;
        this.f = 0;
        this.g = null;
    }

    @Override
    public void c() {
        super.c();
        q();
        this.g = null;
    }

    @EventTarget
    public void a(AttackEvent event) {
        this.f = 2;
        q();
    }

    @EventTarget
    public void a(PacketEvent event) {
        if (aM_.player == null) {
            return;
        }
        if (event.c()) {
            EntityVelocityUpdateS2CPacket class_2743VarD = (EntityVelocityUpdateS2CPacket) event.d();
            if (class_2743VarD instanceof EntityVelocityUpdateS2CPacket) {
                EntityVelocityUpdateS2CPacket velocity = class_2743VarD;
                if (velocity.getEntityId() == aM_.player.getId()) {
                    q();
                    return;
                }
                return;
            }
            return;
        }
        if (event.b()) {
            if (this.f > 0 || a(event.d())) {
                q();
            } else {
                this.d.offer(event.d());
                event.a(true);
            }
        }
    }

    @EventTarget
    public void a(TickEvent event) {
        if (this.f > 0) {
            this.f--;
        }
        int i = this.e + 1;
        this.e = i;
        if (i >= this.b.c().intValue() && !this.d.isEmpty()) {
            q();
            this.e = 0;
        }
    }

    @EventTarget
    public void a(DrawEvent event) {
        if (event.c() && this.c.c().booleanValue() && this.g != null) {
            event.e().a(event.h(), aM_.player.getBoundingBox().offset(this.g.subtract(aM_.player.getPos())), ColorUtil.a(255, 255, 255, InterfaceC0020Opcode.aN), 0.75f);
        }
    }

    private boolean a(Packet<?> packet) {
        return (packet instanceof PlayerInteractEntityC2SPacket) || (packet instanceof ChatMessageC2SPacket) || (packet instanceof UpdateSelectedSlotC2SPacket) || (packet instanceof HandSwingC2SPacket) || (packet instanceof PlayerInteractBlockC2SPacket) || (packet instanceof PlayerInteractItemC2SPacket) || (packet instanceof ClickSlotC2SPacket);
    }

    private void q() {
        ClientConnectionAccessor connection = (ClientConnectionAccessor) aM_.player.networkHandler.getConnection();
        this.d.forEach(packet -> {
            connection.sendWithoutEvent(packet, null, true);
        });
        this.d.clear();
        this.g = aM_.player.getPos();
    }
}
