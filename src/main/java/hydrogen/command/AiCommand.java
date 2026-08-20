package hydrogen.command;

import hydrogen.ai.AiAimModel;
import hydrogen.ai.AiNamedRecorder;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Interface;
import hydrogen.module.combat.Aura;
import hydrogen.util.ChatUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.List;

@Command(a = "ai")
public class AiCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> toggle(null))
                .then(a("start").executes(context -> start(null))
                        .then(d("имя").executes(context -> start(a(context, "имя")))))
                .then(a("stop").executes(context -> stop()))
                .then(a("status").executes(context -> status()))
                .then(a("train").executes(context -> train(null))
                        .then(d("имя").suggests(datasets()).executes(context -> train(a(context, "имя")))))
                .then(a("select").executes(context -> list())
                        .then(d("имя").suggests(datasets()).executes(context -> select(a(context, "имя")))));
    }

    private int toggle(String name) {
        Aura aura = aura();
        if (aura == null) {
            ChatUtil.a((Object) "Aura не найдена.");
            return 1;
        }
        if (aura.aiRecording()) {
            return stop();
        }
        return start(name);
    }

    private int start(String name) {
        Aura aura = aura();
        if (aura == null) {
            ChatUtil.a((Object) "Aura не найдена.");
            return 1;
        }
        String used = aura.startAiRecord(name);
        ChatUtil.a((Object) ("Запись &astart&7 (тики, ручками / Aura): &f" + used + ".csv"));
        return 1;
    }

    private int stop() {
        Aura aura = aura();
        if (aura == null) {
            ChatUtil.a((Object) "Aura не найдена.");
            return 1;
        }
        String used = aura.stopAiRecord();
        ChatUtil.a((Object) ("Запись AI датасета &cвыключена&7. Файл: &f" + used + ".csv &8(" + AiNamedRecorder.rowsWritten() + " строк)"));
        return 1;
    }

    private int train(String name) {
        Aura aura = aura();
        if (aura == null) {
            ChatUtil.a((Object) "Aura не найдена.");
            return 1;
        }
        int samples = aura.trainAi(name);
        if (samples <= 0) {
            ChatUtil.a((Object) "Train: нет семплов. Сначала .ai start, потом .ai stop и .ai train.");
            return 1;
        }
        ChatUtil.a((Object) ("Train готов: &f" + aura.aiDatasetName() + "&7, семплов &a" + samples));
        return 1;
    }

    private int select(String name) {
        Aura aura = aura();
        if (aura == null) {
            ChatUtil.a((Object) "Aura не найдена.");
            return 1;
        }
        String used = aura.selectAiDataset(name);
        ChatUtil.a((Object) ("Select: активный датасет &f" + used));
        return 1;
    }

    private int list() {
        Aura aura = aura();
        List<String> names = AiNamedRecorder.listNames(Interface.aM_);
        if (names.isEmpty()) {
            ChatUtil.a((Object) "Датасетов нет. Запиши через .ai start");
            return 1;
        }
        String current = aura == null ? "" : aura.aiDatasetName();
        ChatUtil.a((Object) ("Датасеты (" + names.size() + "):"));
        for (String name : names) {
            ChatUtil.a((Object) ((name.equals(current) ? " &a> " : "   ") + name));
        }
        ChatUtil.a((Object) "Выбор: .ai select <имя>");
        return 1;
    }

    private int status() {
        Aura aura = aura();
        if (aura == null) {
            ChatUtil.a((Object) "Aura не найдена.");
            return 1;
        }
        ChatUtil.a((Object) ("AI: запись " + (aura.aiRecording() ? "&aON" : "&cOFF")
                + "&7, имя &f" + aura.aiDatasetName()
                + "&7, модель &f" + (AiAimModel.loadedName().isEmpty() ? "-" : AiAimModel.loadedName())
                + "&7, семплов &f" + AiAimModel.sampleCount()));
        return 1;
    }

    private com.mojang.brigadier.suggestion.SuggestionProvider<CommandSource> datasets() {
        return (context, builder) -> {
            String remaining = builder.getRemainingLowerCase() == null ? "" : builder.getRemainingLowerCase();
            for (String name : AiNamedRecorder.listNames(Interface.aM_)) {
                if (name.toLowerCase().startsWith(remaining)) {
                    builder.suggest(name);
                }
            }
            return builder.buildFuture();
        };
    }

    private Aura aura() {
        if (HydrogenClient.h() == null || HydrogenClient.h().d() == null || HydrogenClient.h().d().t() == null) {
            return null;
        }
        return HydrogenClient.h().d().t().B();
    }
}
