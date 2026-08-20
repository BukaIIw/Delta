package hydrogen.module.combat;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.PacketEvent;

import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

@ModuleRegister(a = "No Slot Change", b = "Не даёт серверу принудительно менять активный слот в хотбаре", c = Category.Combat)
public class NoSlotChange extends Module {
    @EventTarget
    public void a(PacketEvent event) {
        if (event.c() && (event.d() instanceof UpdateSelectedSlotS2CPacket)) {
            aM_.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(aM_.player.getInventory().selectedSlot));
            event.a(true);
        }
    }
}
