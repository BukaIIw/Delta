package hydrogen.command;

import hydrogen.ai.AiNamedRecorder;
import hydrogen.ai.AiRecordService;
import hydrogen.util.ChatUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

@Command(a = "record")
public class AiRecordCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> toggle(null))
                .then(a("start").executes(context -> start(null))
                        .then(d("имя").executes(context -> start(a(context, "имя")))))
                .then(a("stop").executes(context -> stop()));
    }

    private int toggle(String name) {
        if (AiRecordService.get().recording()) {
            return stop();
        }
        return start(name);
    }

    private int start(String name) {
        String used = AiRecordService.get().start(name);
        ChatUtil.a((Object) ("Запись &astart&7: каждый тик, файл &f" + used + ".csv"));
        return 1;
    }

    private int stop() {
        String used = AiRecordService.get().stop();
        ChatUtil.a((Object) ("Запись &cstop&7: &f" + used + ".csv &8(" + AiNamedRecorder.rowsWritten() + " строк)"));
        return 1;
    }
}
