package hydrogen.command;

import hydrogen.ai.AiNamedRecorder;
import hydrogen.core.HydrogenClient;
import hydrogen.module.combat.Aura;
import hydrogen.util.ChatUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

@Command(a = "ai")
public class AiCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> toggle(null))
                .then(a("start").executes(context -> start(null))
                        .then(d("имя").executes(context -> start(a(context, "имя")))))
                .then(a("stop").executes(context -> stop()))
                .then(a("status").executes(context -> status()));
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
        ChatUtil.a((Object) ("Запись AI датасета &aвключена&7: &f" + used + ".csv"));
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

    private int status() {
        Aura aura = aura();
        if (aura == null) {
            ChatUtil.a((Object) "Aura не найдена.");
            return 1;
        }
        ChatUtil.a((Object) ("AI: запись " + (aura.aiRecording() ? "&aON" : "&cOFF")
                + "&7, имя &f" + aura.aiDatasetName()
                + "&7, семплов в модели &f" + hydrogen.ai.AiAimModel.sampleCount()));
        return 1;
    }

    private Aura aura() {
        if (HydrogenClient.h() == null || HydrogenClient.h().d() == null || HydrogenClient.h().d().t() == null) {
            return null;
        }
        return HydrogenClient.h().d().t().B();
    }
}
