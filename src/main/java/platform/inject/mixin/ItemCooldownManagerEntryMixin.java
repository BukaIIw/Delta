package platform.inject.mixin;


import hydrogen.render.AnimationUtil;
import hydrogen.mixin.IItemCooldownManager;
import lombok.Generated;
import net.minecraft.entity.player.ItemCooldownManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "net.minecraft.entity.player.ItemCooldownManager$Entry")
public class ItemCooldownManagerEntryMixin implements IItemCooldownManager {

    @Unique
    private final AnimationUtil animation = new AnimationUtil();

    @Override
    @Generated
    public AnimationUtil getAnimation() {
        return this.animation;
    }
}
