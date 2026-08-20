package hydrogen.command;

import hydrogen.friend.FriendConstructor;
import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.util.ServerUtil;

import hydrogen.command.BaseCommand;
import hydrogen.command.Command;
import hydrogen.core.EventTarget;
import hydrogen.event.TickEvent;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.client.network.PlayerListEntry;

@Command(a = "ccc")
public class CCCommand extends BaseCommand {
    private int c;

    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (ServerUtil.a.a() || ServerUtil.d.a()) {
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < 3 + ((int) (Math.random() * 3.0d)); i++) {
                    name.append("абвгдежзийклмнопрстуфхцчшщъыьэюяАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt((int) (Math.random() * ((double) "абвгдежзийклмнопрстуфхцчшщъыьэюяАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length()))));
                }
                aM_.player.networkHandler.sendChatMessage("/clan create " + String.valueOf(name));
                this.c = aM_.player.age + 2;
                return 1;
            }
            return 1;
        });
    }

    @EventTarget
    public void a(TickEvent eventTick) {
        if (this.c >= aM_.player.age) {
            for (FriendConstructor constructor : HydrogenClient.h().d().e().e()) {
                PlayerListEntry entry = (PlayerListEntry) aM_.player.networkHandler.getPlayerList().stream().filter(listEntry -> {
                    return listEntry.getProfile().getName().equalsIgnoreCase(constructor.a());
                }).findFirst().orElse(null);
                if (entry != null && !constructor.a().equals(aM_.getSession().getUsername())) {
                    aM_.player.networkHandler.sendChatMessage("/clan invite " + constructor.a());
                }
            }
            this.c = -3;
        }
    }
}
