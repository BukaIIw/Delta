package hydrogen.command;

import hydrogen.core.HydrogenClient;
import hydrogen.util.ChatUtil;

import hydrogen.command.BaseCommand;
import hydrogen.command.Command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.command.CommandSource;

@Command(a = "warden")
public class WardenCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        List<Integer> anarchies = HydrogenClient.h().d().t().aU().q();
        builder.then(a("add").executes(context -> {
            ChatUtil.a((Object) "Использование: .warden add <анархия>");
            return 1;
        }).then(e("анархия").executes(context2 -> {
            int anarchy = b((CommandContext<CommandSource>) context2, "анархия");
            if (anarchy >= 1 && anarchy <= 999) {
                if (!anarchies.contains(Integer.valueOf(anarchy))) {
                    if (anarchies.size() < 10) {
                        anarchies.add(Integer.valueOf(anarchy));
                        ChatUtil.a((Object) ("Анархия " + anarchy + " добавлена."));
                        return 1;
                    }
                    ChatUtil.a((Object) "Можно добавить максимум 10 анархий.");
                    return 1;
                }
                ChatUtil.a((Object) ("Анархия " + anarchy + " уже в списке."));
                return 1;
            }
            ChatUtil.a((Object) "Анархия должна быть от 1 до 999.");
            return 1;
        }))).then(a("remove").executes(context3 -> {
            ChatUtil.a((Object) "Использование: .warden remove <анархия>");
            return 1;
        }).then(e("анархия").executes(context4 -> {
            int anarchy = b((CommandContext<CommandSource>) context4, "анархия");
            if (!anarchies.remove(Integer.valueOf(anarchy))) {
                ChatUtil.a((Object) ("Анархия " + anarchy + " не найдена."));
                return 1;
            }
            ChatUtil.a((Object) ("Анархия " + anarchy + " удалена."));
            return 1;
        }))).then(a("list").executes(context5 -> {
            if (!anarchies.isEmpty()) {
                ChatUtil.a((Object) ("Анархии (" + anarchies.size() + "): " + String.valueOf(anarchies)));
                return 1;
            }
            ChatUtil.a((Object) "Список анархий пуст.");
            return 1;
        })).then(a("clear").executes(context6 -> {
            anarchies.clear();
            ChatUtil.a((Object) "Список анархий очищен.");
            return 1;
        })).executes(context7 -> {
            ChatUtil.a((Object) "Использование: .warden <add|remove|list|clear>");
            return 1;
        });
    }
}
