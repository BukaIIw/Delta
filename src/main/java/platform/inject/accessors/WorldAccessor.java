package platform.inject.accessors;


import java.util.List;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockEntityTickInvoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({World.class})
public interface WorldAccessor {
    @Accessor("blockEntityTickers")
    List<BlockEntityTickInvoker> getBlockEntityTickers();
}
