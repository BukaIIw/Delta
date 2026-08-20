package hydrogen.ui.widget;

import hydrogen.staff.StaffConstructor;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.render.EasingList;
import hydrogen.render.Fonts;
import hydrogen.render.ColorUtil;
import hydrogen.util.MathUtil;

import hydrogen.config.ThemeInfo;
import hydrogen.core.GlobalEvent;
import hydrogen.core.Interface;
import hydrogen.event.DrawEvent;
import hydrogen.render.AnimationUtil;
import hydrogen.ui.element.DragInfo;
import hydrogen.ui.widget.Widget;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;

public class StaffWidget extends Widget implements Interface {
    public StaffWidget() {
        super(new DragInfo("Стафф", 0.0f, 0.0f, 0.0f, 0.0f));
        j().a(this);
    }

    @Override
    public void a(DrawEvent event) {
        d().a(0.0f, 1.0f, 0.3f, EasingList.g, event.g());
        float x = j().a();
        float y = j().b();
        float targetWidth = 14.5f + Fonts.e.a("Staff-list", this.e) + 5.0f + 2.0f;
        float contentY = y + this.d + 3.0f;
        boolean active = false;
        for (StaffConstructor staff : HydrogenClient.h().d().f().a()) {
            if (staff.b().c() > 0.0f) {
                targetWidth = Math.max(targetWidth, 19.0f + Fonts.e.a(staff.a(), 6.5f) + 8.0f + Fonts.e.a(a(staff.a()) ? "Near" : "Online", 6.5f) + 5.0f + 2.0f);
                active = true;
            }
        }
        float width = MathUtil.c(j().f(), targetWidth, 0.5f);
        j().c(width);
        if (a() > 0.0f) {
            a(event, "i", "Staff-list", width, a());
        }
        for (StaffConstructor staff2 : HydrogenClient.h().d().f().a()) {
            AnimationUtil animationUtil = staff2.b();
            animationUtil.a(0.0f, 1.0f, 0.3f, EasingList.g, event.g());
            float animation = animationUtil.c() * a();
            if (animation > 0.0f) {
                float offsetX = (-8.0f) * (1.0f - animation);
                float offsetY = -(1.0f - animation);
                float drawY = contentY + offsetY;
                float textY = (drawY + ((11.5f - Fonts.e.a(6.5f)) / 2.0f)) - 0.5f;
                a(event, x + offsetX, drawY, width, 11.5f, false, animation);
                a(event, x + offsetX + 15.0f, drawY, 11.5f, animation);
                PlayerListEntry entry = aM_.getNetworkHandler() == null ? null : (PlayerListEntry) aM_.getNetworkHandler().getPlayerList().stream().filter(e -> {
                    return e.getProfile().getName().equalsIgnoreCase(staff2.a());
                }).findFirst().orElse(null);
                if (entry != null) {
                    event.d().a(event.h(), x + offsetX + 5.0f, drawY + 2.0f, 7.5f, 7.5f, 2.0f, ColorUtil.a(-1, animation), 0.125f, 0.125f, 0.125f, 0.125f, aM_.getTextureManager().getTexture(entry.getSkinTextures().texture()).getGlId());
                } else {
                    Fonts.a.a(event.h(), "y", x + offsetX + 5.0f, drawY + ((11.5f - Fonts.a.a(8.0f)) / 2.0f), 8.0f, ColorUtil.a(HydrogenClient.h().d().o().a(ThemeInfo.PRIMARY).a(), animation));
                }
                Fonts.e.a(event.h(), staff2.a(), x + offsetX + 19.0f, textY, 6.5f, ColorUtil.a(-1, animation));
                boolean near = a(staff2.a());
                Fonts.e.a(event.h(), near ? "Near" : "Online", ((((x + offsetX) + width) - 5.0f) - Fonts.e.a(near ? "Near" : "Online", 6.5f)) - 1.0f, textY, 6.5f, ColorUtil.a(near ? -1529792 : -9711765, animation));
                contentY += 13.5f * animation;
            }
        }
        j().d(active ? (contentY - y) - 2.0f : this.d);
        super.a(event);
    }

    @Override
    public void a(GlobalEvent event) {
        boolean visible = aM_.currentScreen instanceof ChatScreen;
        for (StaffConstructor staff : HydrogenClient.h().d().f().a()) {
            staff.b().a((aM_.getNetworkHandler() != null && aM_.getNetworkHandler().getPlayerList().stream().anyMatch(e -> {
                return e.getProfile().getName().equalsIgnoreCase(staff.a());
            })) || a(staff.a()));
            if (staff.b().c() > 0.0f) {
                visible = true;
            }
        }
        d().a(visible);
        super.a(event);
    }

    private boolean a(String name) {
        return aM_.world != null && aM_.world.getPlayers().stream().anyMatch(playerEntity -> {
            return playerEntity.getName().getString().equalsIgnoreCase(name);
        });
    }
}
