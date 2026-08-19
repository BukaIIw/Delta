package aethereal.discord;

import aethereal.core.NativeMethodLookup;
import aethereal.core.Delta;

import aethereal.config.BaseProcessor;
import aethereal.discord.ActivityType;
import aethereal.discord.DiscordIPC;

import aethereal.api.Compile;
import java.io.IOException;
import lombok.Generated;

public class DiscordProcessor extends BaseProcessor {
    private DiscordIPC b;

    @Override
    @Compile
    public void setup() {
    }

    @Override
    public void unSetup() {
    }

    static {
        NativeMethodLookup.lookup(DiscordProcessor.class, 25);
    }

    @Generated
    public DiscordIPC a() {
        return this.b;
    }

    public void a(Void result, Throwable ex) {
        if (ex == null) {
            try {
                this.b.a(new Activity.a().a(ActivityType.PLAYING).b("username: " + Delta.h().g().b()).a("build: " + (Delta.h().c() != null ? "development" : "public")).a("https://deltaclient.xyz/api/logotype.png", "https://deltaclient.xyz/").a(System.currentTimeMillis() / 1000).a("https://i.imgur.com/E6dkFRc.jpeg", "https://deltaclient.xyz/").c("Купить", "https://deltaclient.xyz/").c("Новости", "https://t.me/dlcformine").a());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
