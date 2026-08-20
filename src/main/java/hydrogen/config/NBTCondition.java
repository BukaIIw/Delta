package hydrogen.config;


import hydrogen.util.StringUtils;
import static hydrogen.core.Interface.aM_;
import hydrogen.core.Interface;

import lombok.Generated;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;

public class NBTCondition {
    private final String a;

    @Generated
    public String a() {
        return this.a;
    }

    public NBTCondition(String value) {
        this.a = value;
    }

    public boolean a(ItemStack stack) {
        NbtElement nbt = stack.toNbt(Interface.aM_.world.getRegistryManager());
        return nbt != null && nbt.toString().replaceAll("§.", "").toLowerCase().replaceAll("\\s+", StringUtils.a).trim().contains(this.a.replaceAll("§.", "").toLowerCase().replaceAll("\\s+", StringUtils.a).trim());
    }
}
