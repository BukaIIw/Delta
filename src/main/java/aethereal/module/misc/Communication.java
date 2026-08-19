package aethereal.module.misc;

import aethereal.util.StringUtils;
import static aethereal.core.Interface.aM_;
import aethereal.core.Delta;
import aethereal.core.Module;
import aethereal.render.Fonts;
import aethereal.util.ChatUtil;
import aethereal.render.ColorUtil;
import aethereal.util.ProjectUtil;

import aethereal.config.ThemeInfo;
import aethereal.config.ThemeProcessor;
import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.Interface;
import aethereal.core.ModuleRegister;
import aethereal.event.BackendEvent;
import aethereal.event.DrawEvent;
import aethereal.event.PacketEvent;
import aethereal.event.TickEvent;
import aethereal.render.Draw2DProcessor;

import aethereal.setting.BindSetting;
import aethereal.setting.BooleanSetting;
import aethereal.util.CounterUtil;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.text.MutableText;
import org.joml.Vector2f;

@ModuleRegister(a = "Communication", b = "Связывает вас с другими игроками через групповые и глобальные сообщения (party, IRC и др.)", c = Category.Misc)
public class Communication extends Module implements Interface {
    private final BindSetting b = new BindSetting("Отправление метки друзьям", -1).a(() -> {
        JsonObject posObject = new JsonObject();
        posObject.addProperty("x", Double.valueOf(aM_.player.getPos().x));
        posObject.addProperty("y", Double.valueOf(aM_.player.getPos().y));
        posObject.addProperty("z", Double.valueOf(aM_.player.getPos().z));
        Delta.h().f().a(false, "friend", "type", "mark", "pos", posObject);
    });
    private final BooleanSetting c = new BooleanSetting("Клиентский чат", false);
    private final List<a> d = new ArrayList();

    public Communication() {
        a(this.b, this.c);
    }

    @EventTarget
    public void a(TickEvent event) {
        this.d.removeIf(mark -> {
            return mark.a().a(5000L);
        });
    }

    @EventTarget
    public void a(PacketEvent event) {
        if (this.c.c().booleanValue() && event.b()) {
            ChatMessageC2SPacket class_2797VarD = (ChatMessageC2SPacket) event.d();
            if (class_2797VarD instanceof ChatMessageC2SPacket) {
                ChatMessageC2SPacket packet = class_2797VarD;
                String content = packet.chatMessage();
                if (content.startsWith("@")) {
                    Delta.h().f().a(false, "irc", "message", content.substring(1));
                    event.a(true);
                }
            }
        }
    }

    @EventTarget
    public void a(DrawEvent event) {
        if (event.b()) {
            for (a mark : this.d) {
                a(event, mark, aM_.player.getEyePos());
            }
        }
    }

    private void a(DrawEvent event, a mark, Vec3d eyes) {
        String[] parts = mark.c().split(",\\s*");
        if (parts.length < 3) {
            return;
        }
        Vec3d position = new Vec3d(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
        Vector2f screen = ProjectUtil.a(position.x, position.y, position.z);
        if (ProjectUtil.a(screen)) {
            ThemeProcessor theme = Delta.h().d().o();
            int primary = theme.a(ThemeInfo.PRIMARY).a();
            int background = ColorUtil.a(theme.a(ThemeInfo.BACKGROUND_HUD).a(), theme.a(ThemeInfo.BACKGROUND_HUD).b());
            Text text = Text.literal(mark.b().toUpperCase(Locale.ROOT)).append(Text.literal(" /  ").setStyle(Style.EMPTY.withColor(primary))).append(Text.literal(String.format(Locale.US, "%.1fм", Double.valueOf(eyes.distanceTo(position)))));
            float width = (3.0f * 2.0f) + 8.0f + 2.5f + Fonts.e.a(text, 6.25f);
            float height = (8.0f + (3.0f * 2.0f)) - 2.0f;
            float x = screen.x() - (width / 2.0f);
            float y = screen.y() - (height / 2.0f);
            Draw2DProcessor draw = event.d();
            draw.a(event.h(), x, y, width - 0.5f, height, 3.5f, background, 1.0f, background, 6.0f);
            draw.a(event.h(), draw.c().b(mark.b()), null, (x + 3.0f) - 0.5f, (y + 3.0f) - 1.0f, 8.0f, 8.0f, 1.0f, 1.0f);
            Fonts.e.a(event.h(), text, x + 3.0f + 8.0f + 2.0f, (y + ((height - Fonts.e.a(6.25f)) / 2.0f)) - 0.5f, 6.25f);
        }
    }

    @EventTarget
    public void a(BackendEvent event) {
        String payload = event.d().c();
        String type = event.d().a().a(payload, "type");
        String user = event.d().a().a(payload, "user");
        String message = event.d().a().a(payload, "message");
        String priority = event.d().a().a(payload, "priority");
        if ("irc".equals(event.d().b()) && this.c.c().booleanValue()) {
            Prefix prefix = Prefix.a(priority);
            MutableText line = Text.empty();
            if (prefix != null) {
                line.append(Text.literal(prefix.a()).setStyle(Style.EMPTY.withFont(Identifier.of("delta", "prefixes")))).append(Text.literal(StringUtils.a));
            }
            line.append(ChatUtil.b("[" + user + "] → " + message));
            ChatUtil.a((Object) "[IRC]", line);
        }
        if ("friend".equals(event.d().b()) && "mark".equals(type)) {
            JsonObject pos = event.d().a().b(payload, "pos").getAsJsonObject();
            String position = String.format("%.0f, %.0f, %.0f", Double.valueOf(pos.get("x").getAsDouble()), Double.valueOf(pos.get("y").getAsDouble()), Double.valueOf(pos.get("z").getAsDouble()));
            String login = event.d().a().a(payload, "minecraft");
            this.d.removeIf(mark -> {
                return mark.b().equalsIgnoreCase(login);
            });
            this.d.add(new a(new CounterUtil(), login, position));
        }
    }

    public enum Prefix {
        ADMIN("Администратор", "\ue100"),
        STAFF("Сотрудник", "\ue101"),
        YOUTUBER("Ютубер", "\ue102"),
        SHADE("shade", "\ue103"),
        DANGEROUS("dangerous", "\ue104"),
        DEVSTVENIK("девственник", "\ue105"),
        DRUN("друн", "\ue106"),
        QCOLD("qcold", "\ue107"),
        WIN("win", "\ue108"),
        BURMALDA("бурмалда", "\ue109"),
        VOZDUXAN("воздухан", "\ue110");

        private final String l;
        private final String m;

        Prefix(String role, String glyph) {
            this.l = role;
            this.m = glyph;
        }

        public String a() {
            return this.m;
        }

        public static Prefix a(String role) {
            return (Prefix) Arrays.stream(values()).filter(prefix -> {
                return prefix.l.equalsIgnoreCase(role);
            }).findFirst().orElse(null);
        }
    }

    public static final class a {
        private final CounterUtil a;
        private final String b;
        private final String c;

        public a(CounterUtil counterUtil, String login, String position) {
            this.a = counterUtil;
            this.b = login;
            this.c = position;
        }
public CounterUtil a() {
            return this.a;
        }

        public String b() {
            return this.b;
        }

        public String c() {
            return this.c;
        }
    }
}
