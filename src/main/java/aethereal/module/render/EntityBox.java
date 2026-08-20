package aethereal.module.render;

import aethereal.core.Interface;

import static aethereal.core.Interface.aM_;
import aethereal.core.HydrogenClient;
import aethereal.core.Module;
import aethereal.render.ColorUtil;
import aethereal.util.MathUtil;
import aethereal.util.ProjectUtil;
import aethereal.util.ServerUtil;

import aethereal.config.ThemeInfo;
import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.ModuleRegister;
import aethereal.event.DrawEvent;

import aethereal.setting.BooleanSetting;
import aethereal.setting.ColorSetting;
import aethereal.setting.ModeSetting;
import aethereal.setting.MultiModeSetting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2f;

@ModuleRegister(a = "Entity Box", b = "Отображает боксы вокруг сущностей", c = Category.Render)
public class EntityBox extends Module {
    private final ModeSetting b = new ModeSetting("Тип визуализации", "Квадрат", "Квадрат", "Углы", "Заливка", "Отключен");
    private final ModeSetting c = new ModeSetting("Источник цвета", "Клиентский", "Клиентский", "Статичный");
    private final ModeSetting d = (ModeSetting) new ModeSetting("Бар здоровья", "Отключен", "Отключен", "Стандартный").a(() -> {
        return Boolean.valueOf(this.b.l("Квадрат") || this.b.l("Углы"));
    });
    private final ColorSetting e = (ColorSetting) new ColorSetting("Цвет визуализации", Integer.valueOf(ColorUtil.a(255, 255, 255, 255))).a(() -> {
        return Boolean.valueOf(this.c.l("Статичный"));
    });
    private final MultiModeSetting f = new MultiModeSetting("Отслеживаемые сущности",
            new BooleanSetting("Игроки", true),
            new BooleanSetting("Животные", true),
            new BooleanSetting("Мобы", true),
            new BooleanSetting("Предметы", true));

    public EntityBox() {
        a(this.b, this.c, this.d, this.e, this.f);
    }

    @EventTarget
    public void a(DrawEvent event) {
        if (aM_.world == null || aM_.player == null || this.b.l("Отключен")) {
            return;
        }

        int boxColor = q();

        if (this.b.l("Заливка")) {
            if (!event.c()) {
                return;
            }
            for (Entity entity : aM_.world.getEntities()) {
                Box box = b(entity, event.g());
                if (box == null) {
                    continue;
                }
                event.e().a(event.h(), box, ColorUtil.a(boxColor, 120), 0.75f);
                a(event, box, boxColor);
            }
            return;
        }

        if (!event.b() || !(this.b.l("Квадрат") || this.b.l("Углы"))) {
            return;
        }

        boolean corners = this.b.l("Углы");
        MatrixStack matrices = event.h();
        for (Entity entity : aM_.world.getEntities()) {
            Box box = b(entity, event.g());
            if (box == null) {
                continue;
            }

            float[] bounds = c(box);
            if (bounds == null) {
                continue;
            }

            boolean healthBar = !this.d.l("Отключен") && entity instanceof LivingEntity;
            float percent = 0.0f;
            int healthColor = ColorUtil.a(0, 255, 0, 255);
            if (healthBar) {
                LivingEntity living = (LivingEntity) entity;
                percent = Math.min(Math.max(0.0f, ServerUtil.a.a(living)) / Math.max(1.0f, living.getMaxHealth()), 1.0f);
                healthColor = ColorUtil.b(ColorUtil.a(255, 0, 0, 255), ColorUtil.a(0, 255, 0, 255), percent);
            }

            a(event, matrices, bounds[0], bounds[1], bounds[2], bounds[3], boxColor, corners, healthBar, percent, healthColor);
        }
    }

    private int q() {
        int raw = this.c.l("Статичный")
                ? this.e.c().intValue()
                : HydrogenClient.h().d().o().a(ThemeInfo.PRIMARY).a();
        return ColorUtil.a((raw >> 16) & 255, (raw >> 8) & 255, raw & 255, 255);
    }

    private Box b(Entity entity, float tickDelta) {
        if (!a(entity)) {
            return null;
        }
        Vec3d interpolated = MathUtil.a(entity, tickDelta);
        Vec3d camera = aM_.gameRenderer.getCamera().getPos();
        Vec3d look = aM_.player.getRotationVec(tickDelta);
        if (interpolated.add(0.0d, entity.getHeight() * 0.5d, 0.0d).subtract(camera).dotProduct(look) <= 0.05d) {
            return null;
        }
        return entity.getBoundingBox().offset(interpolated.subtract(entity.getPos()));
    }

    private boolean a(Entity entity) {
        if (entity == null || entity.isRemoved() || aM_.player == null) {
            return false;
        }
        if (entity == aM_.player && aM_.options.getPerspective().isFirstPerson()) {
            return false;
        }

        String key;
        if (entity instanceof PlayerEntity) {
            key = "Игроки";
        } else if (entity instanceof HostileEntity) {
            key = "Мобы";
        } else if (entity instanceof AnimalEntity || entity instanceof ShulkerEntity || entity instanceof VillagerEntity) {
            key = "Животные";
        } else if (entity instanceof ItemEntity) {
            key = "Предметы";
        } else {
            return false;
        }

        BooleanSetting setting = this.f.a(key);
        return setting != null && setting.c().booleanValue();
    }

    private float[] c(Box box) {
        float[] projected = ProjectUtil.a(box);
        if (projected != null && projected.length >= 4
                && Float.isFinite(projected[0]) && Float.isFinite(projected[1])
                && Float.isFinite(projected[2]) && Float.isFinite(projected[3])
                && Math.abs(projected[2] - projected[0]) >= 1.0f
                && Math.abs(projected[3] - projected[1]) >= 1.0f) {
            return projected;
        }

        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        int visible = 0;

        double[] xs = {box.minX, box.maxX};
        double[] ys = {box.minY, box.maxY};
        double[] zs = {box.minZ, box.maxZ};
        for (double x : xs) {
            for (double y : ys) {
                for (double z : zs) {
                    Vector2f screen = ProjectUtil.a(x, y, z);
                    if (screen == null || !ProjectUtil.a(screen)) {
                        continue;
                    }
                    visible++;
                    minX = Math.min(minX, screen.x());
                    minY = Math.min(minY, screen.y());
                    maxX = Math.max(maxX, screen.x());
                    maxY = Math.max(maxY, screen.y());
                }
            }
        }

        if (visible == 0 || !Float.isFinite(minX) || maxX - minX < 1.0f || maxY - minY < 1.0f) {
            return null;
        }
        return new float[]{minX, minY, maxX, maxY};
    }

    private void a(DrawEvent event, MatrixStack matrices, float x1, float y1, float x2, float y2, int color, boolean corners, boolean healthBar, float percent, int healthColor) {
        float x = Math.min(x1, x2);
        float y = Math.min(y1, y2);
        float w = Math.max(2.0f, Math.abs(x2 - x1));
        float h = Math.max(2.0f, Math.abs(y2 - y1));
        float t = 1.5f;

        if (corners) {
            float len = Math.max(4.0f, Math.min(10.0f, Math.min(w, h) * 0.28f));
            event.d().a(matrices, x, y, len, t, 0.0f, color);
            event.d().a(matrices, x, y, t, len, 0.0f, color);
            event.d().a(matrices, x + w - len, y, len, t, 0.0f, color);
            event.d().a(matrices, x + w - t, y, t, len, 0.0f, color);
            event.d().a(matrices, x, y + h - t, len, t, 0.0f, color);
            event.d().a(matrices, x, y + h - len, t, len, 0.0f, color);
            event.d().a(matrices, x + w - len, y + h - t, len, t, 0.0f, color);
            event.d().a(matrices, x + w - t, y + h - len, t, len, 0.0f, color);
        } else {
            event.d().a(matrices, x, y, w, t, 0.0f, color);
            event.d().a(matrices, x, y + h - t, w, t, 0.0f, color);
            event.d().a(matrices, x, y, t, h, 0.0f, color);
            event.d().a(matrices, x + w - t, y, t, h, 0.0f, color);
        }

        if (healthBar) {
            float barW = 2.0f;
            float barH = h * Math.min(Math.max(percent, 0.0f), 1.0f);
            event.d().a(matrices, x - barW - 2.0f, y, barW, h, 0.0f, ColorUtil.a(0, 0, 0, 180));
            event.d().a(matrices, x - barW - 2.0f, y + h - barH, barW, Math.max(1.0f, barH), 0.0f, healthColor);
        }
    }

    private void a(DrawEvent event, Box box, int color) {
        Vec3d a = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d b = new Vec3d(box.maxX, box.minY, box.minZ);
        Vec3d c = new Vec3d(box.maxX, box.minY, box.maxZ);
        Vec3d d = new Vec3d(box.minX, box.minY, box.maxZ);
        Vec3d e = new Vec3d(box.minX, box.maxY, box.minZ);
        Vec3d f = new Vec3d(box.maxX, box.maxY, box.minZ);
        Vec3d g = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d h = new Vec3d(box.minX, box.maxY, box.maxZ);

        event.e().a(event.h(), a, b, null, color, 1.5f);
        event.e().a(event.h(), b, c, null, color, 1.5f);
        event.e().a(event.h(), c, d, null, color, 1.5f);
        event.e().a(event.h(), d, a, null, color, 1.5f);
        event.e().a(event.h(), e, f, null, color, 1.5f);
        event.e().a(event.h(), f, g, null, color, 1.5f);
        event.e().a(event.h(), g, h, null, color, 1.5f);
        event.e().a(event.h(), h, e, null, color, 1.5f);
        event.e().a(event.h(), a, e, null, color, 1.5f);
        event.e().a(event.h(), b, f, null, color, 1.5f);
        event.e().a(event.h(), c, g, null, color, 1.5f);
        event.e().a(event.h(), d, h, null, color, 1.5f);
    }
}