package hydrogen.ui.widget;

import hydrogen.util.KeyUtil;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.InterfaceC0020Opcode;
import hydrogen.render.EasingList;
import hydrogen.render.Fonts;
import hydrogen.render.ColorUtil;
import hydrogen.util.InventoryUtil;
import hydrogen.util.MathUtil;

import hydrogen.core.GlobalEvent;
import hydrogen.core.Interface;
import hydrogen.event.DrawEvent;
import hydrogen.module.misc.ServerAssistant;
import hydrogen.ui.element.DragInfo;
import hydrogen.ui.widget.Widget;

import java.util.List;
import java.util.Locale;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.registry.Registries;
import platform.inject.accessors.ItemCooldownManagerAccessor;

public class ItemsWidget extends Widget implements Interface {
    public ItemsWidget() {
        super(new DragInfo("Предметы", 0.0f, 0.0f, 0.0f, 0.0f));
        j().a(this);
    }

    @Override
    public void a(DrawEvent event) {
        d().a(0.0f, 1.0f, 0.3f, EasingList.g, event.g());
        ServerAssistant assistant = HydrogenClient.h().d().t().aj();
        List<ServerAssistant.b> providers = assistant.q();
        boolean active = false;
        for (ServerAssistant.b provider : providers) {
            provider.a().a(0.0f, 1.0f, 0.3f, EasingList.g, event.g());
            active |= provider.a().c() > 0.0f;
        }
        boolean example = !active && a() > 0.0f;
        float y = j().b();
        float x = j().a();
        float contentX = x;
        ItemCooldownManagerAccessor accessor = (ItemCooldownManagerAccessor) (ItemCooldownManagerAccessor) aM_.player.getItemCooldownManager();
        int i = 0;
        while (i < providers.size()) {
            ServerAssistant.b provider2 = providers.get(i);
            float animation = example ? i < 3 ? a() : 0.0f : provider2.a().c() * a();
            if (animation > 0.0f) {
                Identifier itemId = Registries.ITEM.getId(provider2.c());
                Object entry = accessor.getEntries().get(itemId);
                int remaining = entry != null ? Math.max(((platform.inject.accessors.ItemCooldownEntryAccessor) entry).getEndTick() - accessor.getTick(), 0) : 0;
                String label = remaining > 0 ? String.format(Locale.US, "%.1f", Float.valueOf(remaining / 20.0f)) : KeyUtil.b(provider2.b().c().intValue());
                float width = 19.5f + Fonts.e.a(label, 6.5f) + 5.0f;
                float textY = (y + ((this.d - Fonts.e.a(6.5f)) / 2.0f)) - 0.5f;
                a(event, contentX, y, width, this.d, true, animation);
                HydrogenClient.h().d().j().a(event.i(), provider2.c().getDefaultStack(), contentX + 3.0f, y + ((this.d - 16.0f) / 2.0f) + 3.0f, InterfaceC0020Opcode.aN, animation, 0.6f, false);
                a(event, contentX + 15.5f, y, this.d, animation);
                Fonts.e.a(event.h(), label, contentX + 19.5f, textY, 6.5f, ColorUtil.a(-1, animation));
                contentX += (width + 2.0f) * animation;
            }
            i++;
        }
        j().c(MathUtil.c(j().f(), Math.max(0.0f, (contentX - x) - 2.0f), 0.5f));
        j().d((active || example) ? this.d : 0.0f);
        super.a(event);
    }

    @Override
    public void a(GlobalEvent event) {
        ServerAssistant assistant = HydrogenClient.h().d().t().aj();
        boolean visible = aM_.currentScreen instanceof ChatScreen;
        for (ServerAssistant.b provider : assistant.q()) {
            provider.a().a(assistant.m() && provider.b().c().intValue() != -1 && provider.b().e().get().booleanValue() && InventoryUtil.b(provider.c()) != -1);
            if (provider.a().c() > 0.0f) {
                visible = true;
            }
        }
        d().a(visible);
        super.a(event);
    }
}
