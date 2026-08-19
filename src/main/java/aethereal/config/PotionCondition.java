package aethereal.config;


import java.util.stream.StreamSupport;
import lombok.Generated;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.component.DataComponentTypes;

public class PotionCondition {
    private final RegistryEntry<StatusEffect> a;
    private final int b;
    private final int c;

    @Generated
    public RegistryEntry<StatusEffect> a() {
        return this.a;
    }

    @Generated
    public int b() {
        return this.b;
    }

    @Generated
    public int c() {
        return this.c;
    }

    public PotionCondition(RegistryEntry<StatusEffect> effect, int requiredLevel, int requiredDuration) {
        if (effect == null) {
            throw new IllegalArgumentException("Effect cannot be null");
        }
        this.a = effect;
        this.b = requiredLevel;
        this.c = requiredDuration;
    }

    public boolean a(ItemStack stack) {
        PotionContentsComponent contents = (PotionContentsComponent) stack.get(DataComponentTypes.POTION_CONTENTS);
        if (contents == null) {
            return false;
        }
        return StreamSupport.stream(contents.getEffects().spliterator(), false).anyMatch(effect -> {
            return effect.getEffectType().equals(this.a) && effect.getAmplifier() + 1 == this.b && effect.getDuration() >= this.c;
        });
    }

    public String toString() {
        return "PotionCondition{effect=" + ((String) this.a.getKey().map(k -> {
            return k.getValue().toString();
        }).orElse("unknown")) + ", requiredLevel=" + this.b + ", requiredDuration=" + this.c + "}";
    }
}
