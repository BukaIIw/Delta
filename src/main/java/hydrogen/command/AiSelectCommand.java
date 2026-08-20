package hydrogen.command;

import hydrogen.ai.AiNamedRecorder;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Interface;
import hydrogen.module.combat.Aura;
import hydrogen.util.ChatUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

import java.util.List;

@Command(a = "select")
public class AiSelectCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> list())
                .then(d("имя").executes(context -> select(a(context, "имя"))));
    }

    private int select(String name) {
        Aura aura = HydrogenClient.h().d().t().B();
        String used = aura.selectAiDataset(name);
        ChatUtil.a((Object) ("Select: активный датасет &f" + used));
        return 1;
    }

    private int list() {
        Aura aura = HydrogenClient.h().d().t().B();
        List<String> names = AiNamedRecorder.listNames(Interface.aM_);
        if (names.isEmpty()) {
            ChatUtil.a((Object) "Датасетов нет. Запиши через .ai start");
            return 1;
        }
        ChatUtil.a((Object) ("Датасеты (" + names.size() + "):"));
        for (String name : names) {
            ChatUtil.a((Object) ((name.equals(aura.aiDatasetName()) ? " &a> " : "   ") + name));
        }
        ChatUtil.a((Object) "Выбор: .select <имя>");
        return 1;
    }
}
