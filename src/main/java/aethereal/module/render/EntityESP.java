package aethereal.module.render;

import aethereal.core.Interface;

import static aethereal.core.Interface.aM_;
import aethereal.core.Delta;
import aethereal.core.InterfaceC0020Opcode;
import aethereal.core.Module;
import aethereal.render.Fonts;
import aethereal.render.ColorUtil;
import aethereal.util.InventoryUtil;
import aethereal.util.MathUtil;
import aethereal.util.ProjectUtil;
import aethereal.util.ServerUtil;

import aethereal.core.Category;
import aethereal.core.EventTarget;
import aethereal.core.ModuleRegister;
import aethereal.event.DrawEvent;
import aethereal.module.misc.StreamerMode;
import aethereal.setting.BooleanSetting;

import aethereal.setting.MultiModeSetting;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import org.joml.Vector2f;

@ModuleRegister(a = "Entity ESP", b = "Отображает информацию о сущностях над их головой", c = Category.Render)
public class EntityESP extends Module {
    private final MultiModeSetting b = new MultiModeSetting("Отслеживаемые сущности", new BooleanSetting("Игроки", true), new BooleanSetting("Животные", false), new BooleanSetting("Мобы", false), new BooleanSetting("Предметы", false));
    private final List<a> c = new ArrayList();

    @Generated
    public List<a> q() {
        return this.c;
    }

    public EntityESP() {
        a(this.b);
    }

    @EventTarget
    public void a(DrawEvent event) {
        if (!event.b() || aM_.world == null || aM_.player == null) {
            return;
        }

        MatrixStack matrices = event.h();
        for (Entity entity : aM_.world.getEntities()) {
            if (entity == null || entity == aM_.player || entity.isRemoved()) {
                continue;
            }

            String key;
            if (entity instanceof PlayerEntity) {
                key = "Игроки";
            } else if (entity instanceof HostileEntity) {
                key = "Мобы";
            } else if (entity instanceof AnimalEntity || entity instanceof ShulkerEntity || entity instanceof VillagerEntity) {
                key = "Животные";
            } else if (entity instanceof ItemEntity || entity instanceof ArrowEntity) {
                key = "Предметы";
            } else {
                continue;
            }

            BooleanSetting setting = this.b.a(key);
            if (setting == null || !setting.c().booleanValue()) {
                continue;
            }

            boolean friend = entity instanceof PlayerEntity && Delta.h().d().e().d(entity.getName().getString());
            int alpha = Math.max(160, InterfaceC0020Opcode.bN);
            int color = friend ? ColorUtil.a(0, 100, 0, alpha) : ColorUtil.a(0, 0, 0, alpha);

            Vec3d interpolated = MathUtil.a(entity, event.g());
            Vec3d entityPos = interpolated.add(0.0d, entity.getHeight() + 0.25f, 0.0d);
            Vector2f screenPos = ProjectUtil.a(entityPos.getX(), entityPos.getY(), entityPos.getZ());
            if (screenPos == null || !ProjectUtil.a(screenPos)) {
                continue;
            }

            if (entity instanceof ItemEntity || entity instanceof ArrowEntity) {
                c(entity, event, matrices, screenPos, 7.5f, 2.0f, color);
            } else {
                a(entity, event, matrices, screenPos, 7.5f, 2.0f, color);
                Vector2f feetPos = ProjectUtil.a(interpolated.x, interpolated.y - 0.25d, interpolated.z);
                if (feetPos != null && ProjectUtil.a(feetPos)) {
                    b(entity, event, matrices, feetPos, 7.5f, 2.0f, color);
                }
            }
        }
    }

    private void a(Entity entity, DrawEvent event, MatrixStack matrices, Vector2f screenPos, float fontSize, float padding, int color) {
        String name = entity.getName().getString();
        StreamerMode streamerMode = Delta.h().d().t().aE();
        if (streamerMode != null && streamerMode.m() && streamerMode.r().c().booleanValue()) {
            name = streamerMode.a(name);
        }
        if (entity.getScoreboardTeam() != null) {
            name = entity.getScoreboardTeam().getPrefix().getString() + name;
        }
        if (name == null || name.isBlank()) {
            name = entity.getType().getName().getString();
        }

        String hp = "";
        if (entity instanceof LivingEntity living) {
            hp = " " + ((int) ServerUtil.a.a(living));
        }

        float nameWidth = Fonts.e.a(name, fontSize);
        float hpWidth = hp.isEmpty() ? 0.0f : Fonts.e.a(hp, fontSize);
        float textWidth = nameWidth + hpWidth;
        float textHeight = Fonts.e.d().lineHeight() * fontSize;
        float textX = screenPos.x() - (textWidth / 2.0f);
        float textY = screenPos.y();
        float bgX = textX - padding;
        float bgWidth = textWidth + (padding * 2.0f);

        event.d().a(matrices, bgX, textY, bgWidth, textHeight, 0.0f, color);
        a(matrices, name, textX, textY, fontSize, ColorUtil.a(-1, 1.0f));
        if (!hp.isEmpty()) {
            a(matrices, hp, textX + nameWidth, textY, fontSize, ColorUtil.a(16711680, 1.0f));
        }
        a(entity, event, matrices, bgWidth, bgX, textY, color, textHeight);
    }

    private void a(Entity entity, DrawEvent event, MatrixStack matrices, float nameTagWidth, float nameTagX, float nameTagY, int color, float textHeight) {
        if (!(entity instanceof PlayerEntity player) || event.i() == null) {
            return;
        }

        float spacing = textHeight * 0.3f;
        ItemStack[] stacks = {
                player.getMainHandStack(),
                player.getEquippedStack(EquipmentSlot.HEAD),
                player.getEquippedStack(EquipmentSlot.CHEST),
                player.getEquippedStack(EquipmentSlot.LEGS),
                player.getEquippedStack(EquipmentSlot.FEET),
                player.getOffHandStack()
        };

        int count = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                count++;
            }
        }
        if (count == 0) {
            return;
        }

        float x = nameTagX + ((nameTagWidth - ((count * textHeight) + ((count - 1) * spacing))) / 2.0f);
        float y = (nameTagY - textHeight) - spacing;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                event.d().a(matrices, x, y, textHeight, textHeight, 0.0f, color);
                event.e().a(event.i(), InventoryUtil.a(stack), x, y, 0, 1.0f, textHeight / 16.0f, true);
                x += textHeight + spacing;
            }
        }
    }

    private void b(Entity entity, DrawEvent event, MatrixStack matrices, Vector2f screenPos, float fontSize, float padding, int color) {
        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        List<a> trackers = new ArrayList<>();
        for (int i = this.c.size() - 1; i >= 0; i--) {
            a entry = this.c.get(i);
            if (entry.b() == living.getId()) {
                if (living.age < entry.c()) {
                    this.c.remove(i);
                } else {
                    trackers.add(entry);
                }
            }
        }

        List<StatusEffectInstance> effects;
        if (!trackers.isEmpty()) {
            effects = new ArrayList<>();
            for (a tracker : trackers) {
                for (StatusEffectInstance effectInstance : tracker.a()) {
                    int remaining = effectInstance.getDuration() - Math.max(0, living.age - tracker.c());
                    if (remaining <= 0) {
                        continue;
                    }

                    StatusEffectInstance remainingEffect = effectInstance.getDuration() > 1000000
                            ? effectInstance
                            : new StatusEffectInstance(effectInstance.getEffectType(), remaining, effectInstance.getAmplifier());

                    StatusEffectInstance existing = null;
                    for (StatusEffectInstance instance : effects) {
                        if (instance.getEffectType().equals(effectInstance.getEffectType())) {
                            existing = instance;
                            break;
                        }
                    }

                    if (existing == null) {
                        effects.add(remainingEffect);
                    } else if (remainingEffect.getAmplifier() > existing.getAmplifier()
                            || (remainingEffect.getAmplifier() == existing.getAmplifier() && remainingEffect.getDuration() > existing.getDuration())) {
                        effects.remove(existing);
                        effects.add(remainingEffect);
                    }
                }
            }
            if (effects.isEmpty()) {
                effects = new ArrayList<>(living.getStatusEffects());
            }
        } else {
            effects = new ArrayList<>(living.getStatusEffects());
        }

        if (effects.isEmpty()) {
            return;
        }

        float lineHeight = Fonts.e.d().lineHeight() * fontSize;
        float maxWidth = 0.0f;
        List<String> lines = new ArrayList<>(effects.size());
        for (StatusEffectInstance effect : effects) {
            int seconds = effect.getDuration() / 20;
            String line = Text.translatable(((StatusEffect) effect.getEffectType().value()).getTranslationKey()).getString()
                    + " " + MathUtil.a(effect.getAmplifier())
                    + (effect.getDuration() > 1000000 ? " ∞" : " - " + (seconds / 60) + ":" + String.format("%02d", seconds % 60));
            lines.add(line);
            maxWidth = Math.max(maxWidth, Fonts.e.a(line, fontSize));
        }

        float textX = screenPos.x() - (maxWidth / 2.0f);
        float textY = screenPos.y() + padding;
        event.d().a(matrices, textX - padding, textY, maxWidth + (padding * 2.0f), effects.size() * lineHeight, 0.0f, color);

        float lineY = textY;
        for (int i = 0; i < effects.size(); i++) {
            StatusEffectInstance effect = effects.get(i);
            String line = lines.get(i);
            a(matrices, line, screenPos.x() - (Fonts.e.a(line, fontSize) / 2.0f), lineY, fontSize, ColorUtil.a(((StatusEffect) effect.getEffectType().value()).getColor(), 1.0f));
            lineY += lineHeight;
        }
    }

    private void c(Entity entity, DrawEvent event, MatrixStack matrices, Vector2f screenPos, float fontSize, float padding, int color) {
        String text = entity instanceof ItemEntity itemEntity
                ? itemEntity.getStack().getName().getString()
                : entity.getName().getString();
        if (entity instanceof ItemEntity item && item.getStack().getCount() > 1) {
            text += " x" + item.getStack().getCount();
        }
        if (text == null || text.isBlank()) {
            text = entity.getType().getName().getString();
        }

        float textWidth = Fonts.e.a(text, fontSize);
        float textHeight = Fonts.e.d().lineHeight() * fontSize;
        float textX = screenPos.x() - (textWidth / 2.0f);
        float textY = screenPos.y();
        event.d().a(matrices, textX - padding, textY, textWidth + (padding * 2.0f), textHeight, 0.0f, color);
        a(matrices, text, textX, textY, fontSize, ColorUtil.a(-1, 1.0f));
    }

    private void a(MatrixStack matrices, String text, float x, float y, float size, int color) {
        Fonts.e.a(matrices, text, x + 0.6f, y + 0.6f, size, ColorUtil.a(ColorUtil.a(0, 0, 0, 220), 1.0f), 0.0f);
        Fonts.e.a(matrices, text, x, y, size, color, 0.0f);
    }

    public static final class a {
        private final List<StatusEffectInstance> a;
        private final int b;
        private final int c;

        public a(List<StatusEffectInstance> effects, int id, int age) {
            this.a = effects;
            this.b = id;
            this.c = age;
        }

        public List<StatusEffectInstance> a() {
            return this.a;
        }

        public int b() {
            return this.b;
        }

        public int c() {
            return this.c;
        }
    }
}