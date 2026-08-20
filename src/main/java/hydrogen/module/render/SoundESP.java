package hydrogen.module.render;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;
import hydrogen.render.Fonts;
import hydrogen.render.ColorUtil;
import hydrogen.util.ProjectUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.DrawEvent;
import hydrogen.event.SoundEvent;
import hydrogen.setting.BooleanSetting;

import hydrogen.util.CounterUtil;
import hydrogen.setting.MultiModeSetting;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import org.joml.Vector2f;

@ModuleRegister(a = "Sound ESP", b = "Отображает место, где был воспроизведён выбранный звук", c = Category.Render)
public class SoundESP extends Module {
    private final MultiModeSetting b = new MultiModeSetting("Отслеживать звуки", new BooleanSetting("Трезубец", true), new BooleanSetting("Фейерверк", true), new BooleanSetting("Взрывы", true));
    private final List<a> c = new ArrayList();

    public SoundESP() {
        a(this.b);
    }

    @EventTarget
    public void a(SoundEvent e) {
        String path = e.b().getId().getPath();
        if ((path.contains("entity.firework_rocket.launch") && this.b.a("Фейерверк").c().booleanValue()) || ((path.contains("entity.generic.explode") && this.b.a("Взрывы").c().booleanValue()) || (path.contains("item.trident.return") && this.b.a("Трезубец").c().booleanValue()))) {
            boolean exists = false;
            for (a info : this.c) {
                if (Math.abs(info.b().getX() - e.b().getX()) <= 0.5d && Math.abs(info.b().getY() - e.b().getY()) <= 0.5d && Math.abs(info.b().getZ() - e.b().getZ()) <= 0.5d) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                this.c.add(new a(e.b()));
            }
        }
    }

    @EventTarget
    public void a(DrawEvent event) {
        String str;
        if (event.b()) {
            int color = ColorUtil.a(0, 0, 0, 100);
            for (a soundInfo : this.c) {
                if (soundInfo.a().a(5500L)) {
                    this.c.remove(soundInfo);
                } else {
                    Vector2f screenPos = ProjectUtil.a(soundInfo.b().getX(), soundInfo.b().getY(), soundInfo.b().getZ());
                    if (ProjectUtil.a(screenPos)) {
                        String path = soundInfo.b().getId().getPath();
                        if (path.contains("firework_rocket")) {
                            str = "Фейерверк";
                        } else if (path.contains("explode")) {
                            str = "Взрывы";
                        } else {
                            str = path.contains("trident") ? "Трезубец" : "Звук";
                        }
                        String soundName = str;
                        int distance = (int) aM_.player.getPos().distanceTo(new Vec3d(soundInfo.b().getX(), soundInfo.b().getY(), soundInfo.b().getZ()));
                        int timeAlive = (int) (soundInfo.a().c() / 1000);
                        Text text = Text.literal(soundName + " [" + distance + "м/" + timeAlive + " сек]");
                        float textWidth = Fonts.e.a(text, 7.5f);
                        float textHeight = Fonts.e.d().lineHeight() * 7.5f;
                        float textX = screenPos.x() - (textWidth / 2.0f);
                        float textY = screenPos.y();
                        event.d().a(event.i().getMatrices(), textX - 2.0f, textY, textWidth + 4.0f, textHeight, 0.0f, color);
                        Fonts.e.a(event.i().getMatrices(), text, textX, textY, 7.5f);
                    }
                }
            }
        }
    }

    public static class a {
        public final CounterUtil a = new CounterUtil();
        public final SoundInstance b;

        @Generated
        public CounterUtil a() {
            return this.a;
        }

        @Generated
        public SoundInstance b() {
            return this.b;
        }

        public a(SoundInstance sound) {
            this.b = sound;
            this.a.b();
        }
    }
}
