package platform.inject.mixin;


import hydrogen.core.EventManager;
import hydrogen.core.IEvent;
import hydrogen.event.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientConnection.class})
public class ClientConnectionMixin {
    @Inject(method = {"send*"}, at = {@At("HEAD")}, cancellable = true)
    private void send(Packet<?> packet, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(packet, PacketEvent.Type.SEND);
        EventManager.a((IEvent) event);
        if (event.a()) {
            ci.cancel();
        }
    }

    @Inject(method = {"channelRead0*"}, at = {@At("HEAD")}, cancellable = true)
    private void channelRead0(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        PacketEvent event = new PacketEvent(packet, PacketEvent.Type.RECEIVE);
        EventManager.a((IEvent) event);
        if (event.a()) {
            ci.cancel();
        }
    }
}
