package hydrogen.module.player;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;
import hydrogen.util.ChatUtil;

import hydrogen.core.Category;
import hydrogen.core.Interface;
import hydrogen.core.ModuleRegister;

import hydrogen.setting.BindSetting;
import lombok.Generated;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.client.network.ClientPlayerEntity;

@ModuleRegister(a = "Click Action", b = "Выполняет действие, привязанное к выбранной клавише", c = Category.Player)
public class ClickAction extends Module implements Interface {
    private final BindSetting b = new BindSetting("Эндер-жемчуг", -1).a(() -> {
        HydrogenClient.h().d().v().b().a(Items.ENDER_PEARL.getDefaultStack());
    });
    private final BindSetting c = new BindSetting("Добавление друга", -1).a(() -> {
        AbstractClientPlayerEntity class_746Var;
        EntityHitResult class_3966Var = aM_.crosshairTarget instanceof EntityHitResult ? (EntityHitResult) aM_.crosshairTarget : null;
        if (class_3966Var instanceof EntityHitResult) {
            EntityHitResult hit = class_3966Var;
            if (hit.getEntity() instanceof AbstractClientPlayerEntity class_746VarMethod_17782) {
                class_746Var = class_746VarMethod_17782;
                if (class_746Var != aM_.player) {
                    String name = class_746Var.getName().getString();
                    if (HydrogenClient.h().d().e().d(name)) {
                        HydrogenClient.h().d().e().c(name);
                        HydrogenClient.h().d().e().unSetup();
                        ChatUtil.a((Object) ("Товарищ " + name + " был успешно удален из списка друзей."));
                    } else {
                        HydrogenClient.h().d().e().b(name);
                        HydrogenClient.h().d().e().unSetup();
                        ChatUtil.a((Object) ("Товарищ " + name + " был успешно добавлен в список друзей."));
                    }
                }
            }
        }
    });

    @Generated
    public BindSetting q() {
        return this.b;
    }

    @Generated
    public BindSetting r() {
        return this.c;
    }

    public ClickAction() {
        a(this.b, this.c);
    }
}
