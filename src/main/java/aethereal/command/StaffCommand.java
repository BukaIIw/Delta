package aethereal.command;

import aethereal.staff.StaffConstructor;
import aethereal.core.HydrogenClient;
import aethereal.util.ChatUtil;

import aethereal.command.BaseCommand;
import aethereal.command.Command;
import aethereal.staff.StaffProcessor;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Objects;
import net.minecraft.command.CommandSource;

@Command(a = "staff")
public class StaffCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        StaffProcessor processor = HydrogenClient.h().d().f();
        LiteralArgumentBuilder literalArgumentBuilderThen = builder.then(a("add").executes(context -> {
            ChatUtil.a((Object) "Использование: .staff add <ник>");
            return 1;
        }).then(b("ник").suggests(a()).executes(context2 -> {
            String name = a((CommandContext<CommandSource>) context2, "ник");
            if (processor.d(name)) {
                ChatUtil.a((Object) ("Стафф " + name + " уже находится в списке стаффа."));
                return 1;
            }
            processor.b(name);
            processor.unSetup();
            ChatUtil.a((Object) ("Стафф " + name + " был успешно добавлен в список стаффа."));
            return 1;
        })));
        LiteralArgumentBuilder literalArgumentBuilderExecutes = a("remove").executes(context3 -> {
            ChatUtil.a((Object) "Использование: .staff remove <ник>");
            return 1;
        });
        RequiredArgumentBuilder<CommandSource, String> requiredArgumentBuilderB = b("ник");
        Objects.requireNonNull(processor);
        literalArgumentBuilderThen.then(literalArgumentBuilderExecutes.then(requiredArgumentBuilderB.suggests(a(processor::a, (v0) -> {
            return v0.a();
        })).executes(context4 -> {
            String name = a((CommandContext<CommandSource>) context4, "ник");
            if (!processor.d(name)) {
                ChatUtil.a((Object) ("Стафф " + name + " не найден в списке стаффа."));
                return 1;
            }
            processor.c(name);
            processor.unSetup();
            ChatUtil.a((Object) ("Стафф " + name + " был успешно удален из списка стаффа."));
            return 1;
        }))).then(a("list").executes(context5 -> {
            if (processor.a().isEmpty()) {
                ChatUtil.a((Object) "Список стаффа пуст.");
                return 1;
            }
            ChatUtil.a((Object) ("Список стаффа (" + processor.a().size() + "):"));
            for (StaffConstructor staffConstructor : processor.a()) {
                ChatUtil.a((Object) ("  - " + staffConstructor.a()));
            }
            return 1;
        })).then(a("clear").executes(context6 -> {
            ChatUtil.a((Object) ("Было успешно удалено стаффа из списка: " + processor.a().size()));
            processor.f();
            processor.unSetup();
            return 1;
        })).executes(context7 -> {
            ChatUtil.a((Object) "Использование: .staff <add|remove|list|clear>");
            return 1;
        });
    }
}
