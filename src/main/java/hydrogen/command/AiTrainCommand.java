package hydrogen.command;

import hydrogen.core.HydrogenClient;
import hydrogen.module.combat.Aura;
import hydrogen.util.ChatUtil;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

@Command(a = "train")
public class AiTrainCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> train(null))
                .then(d("имя").executes(context -> train(a(context, "имя"))));
    }

    private int train(String name) {
        Aura aura = HydrogenClient.h().d().t().B();
        int samples = aura.trainAi(name);
        if (samples <= 0) {
            ChatUtil.a((Object) "Train: нет семплов. Сначала .ai start, потом .ai stop и .train");
            return 1;
        }
        ChatUtil.a((Object) ("Train готов: &f" + aura.aiDatasetName() + "&7, семплов &a" + samples));
        return 1;
    }
}
