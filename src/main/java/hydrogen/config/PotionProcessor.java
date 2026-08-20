package hydrogen.config;

import hydrogen.autobuy.ItemFilter;
import hydrogen.config.PotionCondition;

import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.item.ItemStack;

public class PotionProcessor implements ItemFilter {
    private final List<PotionCondition> a = new ArrayList();

    @Generated
    public List<PotionCondition> a() {
        return this.a;
    }

    public PotionProcessor a(PotionCondition condition) {
        this.a.add(condition);
        return this;
    }

    @Override
    public boolean a(ItemStack stack) {
        return this.a.stream().allMatch(condition -> {
            return condition.a(stack);
        });
    }
}
