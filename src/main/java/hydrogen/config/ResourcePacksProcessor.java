package hydrogen.config;

import hydrogen.core.NativeMethodLookup;
import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.util.ChatUtil;

import hydrogen.config.BaseProcessor;
import hydrogen.core.EventTarget;
import hydrogen.event.BackendEvent;
import hydrogen.network.PacketSecurity;

import hydrogen.api.Compile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;

public class ResourcePacksProcessor extends BaseProcessor {
    @Override
    @Compile
    public void setup() {
    }

    static {
        NativeMethodLookup.lookup(ResourcePacksProcessor.class, 35);
    }

    @Override
    public void unSetup() {
    }

    @EventTarget
    public void a(BackendEvent event) {
        if (event.b() && "resource-packs".equals(event.d().b())) {
            PacketSecurity security = event.d().a();
            String payload = event.d().c();
            String pack = security.a(payload, "pack");
            String archive = security.a(payload, "archive");
            if (pack != null && archive != null) {
                File directory = new File(aM_.runDirectory, "resourcepacks");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                try {
                    Files.write(new File(directory, pack + ".zip").toPath(), java.util.Base64.getDecoder().decode(archive), new OpenOption[0]);
                } catch (IOException e) {
                    ChatUtil.a((Object) ("&c✖ &7Не удалось сохранить ресурс-пак &a" + pack));
                    return;
                }
                ChatUtil.a((Object) ("&a✔ &7Ресурс-пак &a" + pack + " &7успешно добавлен в список доступных ресурс-паков."));
            }
        }
    }
}
