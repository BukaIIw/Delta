package platform.inject.invokers;


import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Entity.class})
public interface EntityCollisionPredictionInvoker {
    @Invoker("findCollisionsForMovement")
    static List<VoxelShape> findCollisionsForMovement(Entity entity, World world, List<VoxelShape> reusable, Box box) {
        throw new AssertionError();
    }

    @Invoker("adjustMovementForCollisions")
    static Vec3d adjustMovementForCollisions(Entity entity, Vec3d movement, Box boundingBox, World world, List<VoxelShape> collisions) {
        throw new AssertionError();
    }
}
