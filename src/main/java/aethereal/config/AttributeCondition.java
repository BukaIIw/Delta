package aethereal.config;


import lombok.Generated;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.DataComponentTypes;

public class AttributeCondition {
    private final RegistryEntry<EntityAttribute> a;
    private final EntityAttributeModifier.Operation b;
    private final double c;

    @Generated
    public RegistryEntry<EntityAttribute> a() {
        return this.a;
    }

    @Generated
    public EntityAttributeModifier.Operation b() {
        return this.b;
    }

    @Generated
    public double c() {
        return this.c;
    }

    public AttributeCondition(RegistryEntry<EntityAttribute> attribute, double expectedAmount, EntityAttributeModifier.Operation expectedOperation) {
        if (attribute == null) {
            throw new IllegalArgumentException("Attribute cannot be null");
        }
        this.a = attribute;
        this.c = expectedAmount;
        this.b = expectedOperation;
    }

    public boolean a(RegistryEntry<EntityAttribute> currentAttr, EntityAttributeModifier modifier) {
        return currentAttr.equals(this.a) && Math.abs(modifier.value() - this.c) < 9.9999942618434E-4d && modifier.operation() == this.b;
    }

    public boolean a(ItemStack stack) {
        AttributeModifiersComponent modifiersComponent = (AttributeModifiersComponent) stack.getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
        for (AttributeModifiersComponent.Entry entry : modifiersComponent.modifiers()) {
            if (a(entry.attribute(), entry.modifier())) {
                return true;
            }
        }
        return false;
    }
}
