package aethereal.command;

import aethereal.util.BooleanUtils;
import aethereal.core.Interface;

import static aethereal.core.Interface.aM_;
import aethereal.core.Delta;
import aethereal.util.ChatUtil;

import aethereal.command.BaseCommand;
import aethereal.command.Command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import lombok.Generated;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

@Command(a = "gps")
public class GPSCommand extends BaseCommand {
    private Vec3d c;

    @Generated
    public Vec3d c() {
        return this.c;
    }

    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(a(BooleanUtils.c).executes(context -> {
            if (this.c == null) {
                ChatUtil.a((Object) "GPS-метка сейчас отсутствует");
                return 1;
            }
            this.c = null;
            ChatUtil.a((Object) "GPS-метка больше не отображается");
            return 1;
        })).then(e("x").executes(context2 -> {
            ChatUtil.a((Object) "Использование: .gps <x> <z>, .gps <x> <y> <z> или .gps off");
            return 1;
        }).then(e("y или z").executes(context3 -> {
            a(new Vec3d(b((CommandContext<CommandSource>) context3, "x"), aM_.player.getY(), b((CommandContext<CommandSource>) context3, "y или z")));
            return 1;
        }).then(e("z").executes(context4 -> {
            a(new Vec3d(b((CommandContext<CommandSource>) context4, "x"), b((CommandContext<CommandSource>) context4, "y или z"), b((CommandContext<CommandSource>) context4, "z")));
            return 1;
        })))).then(a("event").executes(context5 -> {
            Delta.h().d().u().c().a(WayCommand.a.GPS);
            aM_.player.networkHandler.sendCommand("event delay");
            return 1;
        })).executes(context6 -> {
            ChatUtil.a((Object) "Использование: .gps <x> <z>, .gps <x> <y> <z> или .gps off");
            return 1;
        });
    }

    public void a(Vec3d pos) {
        this.c = pos;
        ChatUtil.a((Object) ("GPS-метка установлена: " + ((int) pos.getX()) + ", " + ((int) pos.getY()) + ", " + ((int) pos.getZ())));
    }
}
