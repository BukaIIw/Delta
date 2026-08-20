package hydrogen.module.misc;

import hydrogen.friend.FriendConstructor;
import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.ScoreboardEvent;
import hydrogen.event.TextVisitEvent;

import hydrogen.setting.BooleanSetting;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.MutableText;

@ModuleRegister(a = "Streamer Mode", b = "Скрывает личные данные при стриминге и записи", c = Category.Misc)
public class StreamerMode extends Module {
    private final BooleanSetting b = new BooleanSetting("Скрывать скины игроков", true);
    private final BooleanSetting c = new BooleanSetting("Скрывать имена друзей", true);
    private final BooleanSetting d = new BooleanSetting("Скрывать номер анархии", true);

    @Generated
    public BooleanSetting q() {
        return this.b;
    }

    @Generated
    public BooleanSetting r() {
        return this.c;
    }

    @Generated
    public BooleanSetting s() {
        return this.d;
    }

    public StreamerMode() {
        a(this.b, this.c, this.d);
    }

    @EventTarget
    public void a(TextVisitEvent event) {
        event.a(a(event.b()));
    }

    @EventTarget
    public void a(ScoreboardEvent event) {
        if (this.d.c().booleanValue()) {
            MutableText text = Text.empty();
            event.b().visit((style, part) -> {
                text.append(Text.literal(part.replaceAll("Анархия-\\d+", "Анархия-???")).setStyle(style));
                return Optional.empty();
            }, Style.EMPTY);
            event.a((Text) text);
        }
    }

    public String a(String text) {
        String result = text.replaceAll("(?i)" + aM_.getSession().getUsername(), "Protected");
        if (this.c.c().booleanValue()) {
            for (FriendConstructor friend : HydrogenClient.h().d().e().a()) {
                result = Pattern.compile(friend.a(), 82).matcher(result).replaceAll("Protected");
            }
        }
        if (this.d.c().booleanValue()) {
            result = result.replaceAll("Анархия-\\d+", "Анархия-???");
        }
        return result;
    }
}
