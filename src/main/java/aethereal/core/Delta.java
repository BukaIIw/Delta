package aethereal.core;

import aethereal.core.NativeMethodLookup;
import aethereal.render.ScaleUtil;
import static aethereal.core.Interface.aM_;
import aethereal.core.EventManager;
import aethereal.core.Interface;
import aethereal.core.Module;
import aethereal.render.EasingList;

import aethereal.core.Client;
import aethereal.core.EventTarget;
import aethereal.core.Processor_2;
import aethereal.core.User;
import aethereal.event.DrawEvent;
import aethereal.event.KeyEvent;

import aethereal.ui.screen.GUIScreen;
import aethereal.ui.screen.GUIPanel;
import aethereal.api.Compile;
import aethereal.api.Ultra;
import java.io.File;
import lombok.Generated;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

public class Delta {
    private static Delta a;
    private Processor_2 b;
    private GUIScreen c;
    private Client d;
    private User e;
    private static volatile User jc$unifiedPendingUser$;
    private static volatile Delta jc$unifiedClient$;

    @Compile
    @Ultra
    protected void a() {
        this.e = new User("1", "Владелец", " ", "Владелец", "01.01.2099 00:00", "");
        a = this;
        jc$bindUnifiedClient$(this);
        this.b = new Processor_2();
        this.d = new Client(false);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> this.a(client));
        EventManager.a(this);
        this.b.a();
    }

    @Compile
    @Ultra
    protected void b() {
        this.b.b();
    }

    @Compile
    @Ultra
    public String c() {
        if (new File("ide\\fruzek").exists()) {
            return "fruzek";
        }
        if (new File("ide\\dezz").exists()) {
            return "dezz";
        }
        return null;
    }

    static {
        NativeMethodLookup.lookup(Delta.class, 0);
    }

    public static void jc$publishUnifiedUser$(User user) {
        jc$unifiedPendingUser$ = user;
        Delta delta = jc$unifiedClient$;
        if (delta != null) {
            delta.e = user;
        }
    }

    private static void jc$bindUnifiedClient$(Delta delta) {
        jc$unifiedClient$ = delta;
        User user = jc$unifiedPendingUser$;
        if (user != null) {
            delta.e = user;
        }
    }

    @Generated
    public void a(Processor_2 processor) {
        this.b = processor;
    }

    @Generated
    public void a(GUIScreen guiScreen) {
        this.c = guiScreen;
    }

    @Generated
    public void a(Client client) {
        this.d = client;
    }

    @Generated
    public void a(User user) {
        this.e = user;
    }

    @Generated
    public static Delta h() {
        return a;
    }

    @Generated
    public Processor_2 d() {
        return this.b;
    }

    @Generated
    public GUIScreen e() {
        return this.c;
    }

    @Generated
    public Client f() {
        return this.d;
    }

    @Generated
    public User g() {
        return this.e;
    }

    public Delta() {
        a();
    }

    public void a(MinecraftClient client) {
        b();
    }

    @EventTarget
    public void a(KeyEvent event) {
        GUIScreen gUIScreen;
        if (event.d() == 1 && Interface.aM_.currentScreen == null && event.b() == 344) {
            MinecraftClient class_310Var = Interface.aM_;
            if (this.c != null) {
                gUIScreen = this.c;
            } else {
                GUIScreen gUIScreen2 = new GUIScreen(Text.literal(""));
                gUIScreen = gUIScreen2;
                this.c = gUIScreen2;
            }
            class_310Var.setScreen(gUIScreen);
        }
    }

    @EventTarget(a = 0)
    public void a(DrawEvent event) {
        if (event.b()) {
            ScaleUtil.a(event.i(), 2);
            for (Module module : h().d().t().e()) {
                module.f().a(0.0f, 1.0f, 0.3f, EasingList.i, event.g());
                module.f().a(module.m());
                module.g().a(0.0f, 1.0f, 0.3f, EasingList.i, event.g());
                module.g().a(module.n());
            }
            for (GUIPanel panel : e().c()) {
                panel.b().a(0.0f, 1.0f, 0.3f, EasingList.g, event.g());
                panel.b().a(Interface.aM_.currentScreen instanceof GUIScreen);
            }
        }
    }

    @EventTarget(a = 4)
    public void b(DrawEvent event) {
        if (event.b()) {
            ScaleUtil.a(event.i());
        }
    }
}
