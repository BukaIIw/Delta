package aethereal.command;

import aethereal.core.Delta;
import aethereal.util.ChatUtil;

import aethereal.command.BaseCommand;
import aethereal.command.Command;
import aethereal.macro.MacrosConstructor;
import aethereal.macro.MacrosProcessor;
import aethereal.util.KeyUtil;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import java.util.Objects;
import net.minecraft.command.CommandSource;

@Command(a = "macros")
public class MacrosCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        MacrosProcessor processor = Delta.h().d().d();
        LiteralArgumentBuilder literalArgumentBuilderThen = builder.then(a("add").executes(context -> {
            ChatUtil.a((Object) "Использование: .macros add <клавиша> <команда>");
            return 1;
        }).then(d("клавиша").suggests(b()).then(c("команда").executes(context2 -> {
            String key = a((CommandContext<CommandSource>) context2, "клавиша");
            String command = a((CommandContext<CommandSource>) context2, "команда");
            KeyUtil keyUtil = KeyUtil.a(key);
            if (keyUtil == KeyUtil.UNKNOWN) {
                ChatUtil.a((Object) ("Клавиша " + key + " не найдена."));
                return 1;
            }
            processor.a(key, command);
            processor.unSetup();
            ChatUtil.a((Object) ("Макрос " + command + " был успешно добавлен на клавишу " + keyUtil.b() + "."));
            return 1;
        }))));
        LiteralArgumentBuilder literalArgumentBuilderExecutes = a("remove").executes(context3 -> {
            List<MacrosConstructor> macros = processor.a();
            if (macros.isEmpty()) {
                ChatUtil.a((Object) "Список макросов пуст.");
                return 1;
            }
            ChatUtil.a((Object) ("Доступные команды для удаления (" + macros.size() + "):"));
            for (MacrosConstructor macro : macros) {
                ChatUtil.a((Object) ("  " + macro.b() + " (клавиша: " + KeyUtil.a(macro.a()).b() + ")"));
            }
            ChatUtil.a((Object) "Использование: .macros remove <команда>");
            return 1;
        });
        RequiredArgumentBuilder<CommandSource, String> requiredArgumentBuilderC = c("команда");
        Objects.requireNonNull(processor);
        literalArgumentBuilderThen.then(literalArgumentBuilderExecutes.then(requiredArgumentBuilderC.suggests(a(processor::a, (v0) -> {
            return v0.b();
        })).executes(context4 -> {
            String command = a((CommandContext<CommandSource>) context4, "команда");
            MacrosConstructor macro = processor.a().stream().filter(m -> {
                return m.b().equals(command);
            }).findFirst().orElse(null);
            if (macro == null) {
                ChatUtil.a((Object) ("Макрос с командой " + command + " не найден в списке макросов."));
                return 1;
            }
            processor.b(macro.a());
            processor.unSetup();
            ChatUtil.a((Object) ("Макрос " + command + " был успешно удален с клавиши " + KeyUtil.a(macro.a()).b() + "."));
            return 1;
        }))).then(a("list").executes(context5 -> {
            List<MacrosConstructor> macros = processor.a();
            if (macros.isEmpty()) {
                ChatUtil.a((Object) "Список макросов пуст.");
                return 1;
            }
            ChatUtil.a((Object) ("Список макросов (" + macros.size() + "):"));
            for (MacrosConstructor macro : macros) {
                ChatUtil.a((Object) ("  " + KeyUtil.a(macro.a()).b() + ": " + macro.b()));
            }
            return 1;
        })).then(a("clear").executes(context6 -> {
            ChatUtil.a((Object) ("Было успешно удалено макросов из списка: " + processor.a().size()));
            processor.f();
            processor.unSetup();
            return 1;
        })).executes(context7 -> {
            ChatUtil.a((Object) "Использование: .macros <add|remove|list|clear>");
            return 1;
        });
    }
}
