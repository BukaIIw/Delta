package hydrogen.command;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.util.ChatUtil;

import hydrogen.command.BaseCommand;
import hydrogen.command.Command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.util.Formatting;
import net.minecraft.command.CommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Command(a = "vclip")
public class VClipCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(a("up").executes(context -> {
            float offset = a(true);
            if (offset != 0.0f) {
                a(offset);
                ChatUtil.a((Object) "Вы были успешно подняты по Y");
                return 1;
            }
            return 1;
        })).then(a("down").executes(context2 -> {
            float offset = a(false);
            if (offset != 0.0f) {
                a(offset);
                ChatUtil.a((Object) "Вы были успешно опущены по Y");
                return 1;
            }
            return 1;
        })).then(f("число").executes(context3 -> {
            float offset = c(context3, "число");
            a(offset);
            ChatUtil.a((Object) ("Вы были успешно перемещены на " + offset + " по Y"));
            return 1;
        })).executes(context4 -> {
            ChatUtil.a((Object) "Использование: .vclip <число|up|down>");
            return 1;
        });
    }

    private void a(float yOffset) {
        double x = aM_.player.getX();
        double y = aM_.player.getY() + ((double) yOffset);
        double z = aM_.player.getZ();
        aM_.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, aM_.player.isOnGround(), aM_.player.horizontalCollision));
        aM_.player.setPosition(x, y, z);
    }

    private float a(boolean up) {
        BlockPos playerPos = aM_.player.getBlockPos();
        int startY = up ? 25 : -1;
        int endY = up ? 255 : -255;
        int step = up ? 1 : -1;
        int i = startY;
        while (true) {
            int offset = i;
            if (offset != endY) {
                BlockPos targetPos = playerPos.add(0, offset, 0);
                BlockPos nextPos = playerPos.add(0, offset + step, 0);
                if (aM_.world.getBlockState(targetPos).isAir() && aM_.world.getBlockState(nextPos).isAir()) {
                    return offset + (up ? 1.0f : -1.0f);
                }
                if (up || !aM_.world.getBlockState(targetPos).isOf(Blocks.BEDROCK)) {
                    i = offset + step;
                } else {
                    ChatUtil.a((Object) (String.valueOf(Formatting.GRAY) + "Телепортация в данное место невозможно"));
                    return 0.0f;
                }
            } else {
                return 0.0f;
            }
        }
    }
}
