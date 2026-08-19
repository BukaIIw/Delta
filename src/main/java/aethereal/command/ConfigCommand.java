package aethereal.command;

import aethereal.core.Delta;
import aethereal.core.Module;
import aethereal.util.ChatUtil;

import aethereal.command.BaseCommand;
import aethereal.command.Command;
import aethereal.config.ModuleProcessor;
import aethereal.setting.Setting;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.util.Util;
import net.minecraft.command.CommandSource;

@Command(a = "cfg")
public class ConfigCommand extends BaseCommand {
    @Override
    public void a(LiteralArgumentBuilder<CommandSource> builder) {
        ModuleProcessor processor = Delta.h().d().t();
        builder.then(a("save").executes(context -> {
            ChatUtil.a((Object) "Использование: .cfg save <имя>");
            return 1;
        }).then(b("имя").executes(context2 -> {
            String configName = a((CommandContext<CommandSource>) context2, "имя");
            processor.b(configName);
            ChatUtil.a((Object) ("Конфиг " + configName + " был успешно сохранен."));
            return 1;
        }))).then(a("load").executes(context3 -> {
            ChatUtil.a((Object) "Использование: .cfg load <имя>");
            return 1;
        }).then(b("имя").suggests(c()).executes(context4 -> {
            String configName = a((CommandContext<CommandSource>) context4, "имя");
            if (processor.c(configName)) {
                ChatUtil.a((Object) ("Конфиг " + configName + " был успешно загружен."));
                return 1;
            }
            ChatUtil.a((Object) ("Конфиг " + configName + " не найден."));
            return 1;
        }))).then(a("list").executes(context5 -> {
            File configDir = processor.d();
            File[] files = configDir.listFiles((dir, name) -> {
                return name.endsWith(".json");
            });
            if (files == null || files.length == 0) {
                ChatUtil.a((Object) "Список конфигов пуст.");
                return 1;
            }
            ChatUtil.a((Object) ("Список конфигов (" + files.length + "):"));
            for (File file : files) {
                ChatUtil.a((Object) ("  - " + file.getName()));
            }
            return 1;
        })).then(a("reset").executes(context6 -> {
            for (Module module : processor.e()) {
                module.a(false);
                module.a(-1);
                for (Setting<?> setting : module.e()) {
                    if (setting.g() != null) {
                        resetSettingValue(setting);
                    }
                }
            }
            ChatUtil.a((Object) "Все модули были сброшены в состояние по умолчанию.");
            return 1;
        })).then(a("remove").executes(context7 -> {
            ChatUtil.a((Object) "Использование: .cfg remove <имя>");
            return 1;
        }).then(b("имя").suggests(c()).executes(context8 -> {
            String configName = a((CommandContext<CommandSource>) context8, "имя");
            if (processor.d(configName)) {
                ChatUtil.a((Object) ("Конфиг " + configName + " был успешно удален."));
                return 1;
            }
            ChatUtil.a((Object) ("Конфиг " + configName + " не найден."));
            return 1;
        }))).then(a("dir").executes(context9 -> {
            Util.getOperatingSystem().open(processor.d());
            return 1;
        })).executes(context10 -> {
            ChatUtil.a((Object) "Использование: .cfg <load|save|list|reset|remove|dir>");
            return 1;
        });
    }

    private SuggestionProvider<CommandSource> c() {
        return (context, builder) -> {
            File[] files;
            ModuleProcessor processor = Delta.h().d().t();
            File configDir = processor.d();
            if (configDir.exists() && (files = configDir.listFiles((dir, name) -> {
                return name.endsWith(".json");
            })) != null) {
                Stream map = Arrays.stream(files).map((v0) -> {
                    return v0.getName();
                }).map(name2 -> {
                    return name2.substring(0, name2.length() - 5);
                });
                Objects.requireNonNull(builder);
                map.forEach(s -> builder.suggest((String) s));
            }
            return builder.buildFuture();
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> void resetSettingValue(Setting<?> setting) {
        ((Setting<T>) setting).a((T) setting.g());
    }
}
