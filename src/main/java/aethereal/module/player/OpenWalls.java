package aethereal.module.player;

import aethereal.core.Delta;
import aethereal.core.Module;

import aethereal.core.Category;
import aethereal.core.ModuleRegister;

import java.util.Set;
import lombok.Generated;
import net.minecraft.world.BlockView;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BeaconBlock;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.LoomBlock;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlastFurnaceBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.SmokerBlock;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.CampfireBlock;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.client.network.ClientPlayerEntity;

@ModuleRegister(a = "Open Walls", b = "Позволяет открывать хранилища сквозь стены", c = Category.Player)
public class OpenWalls extends Module {
    private final Set<Class<?>> b = Set.of(new Class[]{AbstractChestBlock.class, FurnaceBlock.class, CraftingTableBlock.class, SpawnerBlock.class, ShulkerBoxBlock.class, AnvilBlock.class, BeaconBlock.class, BlastFurnaceBlock.class, BrewingStandBlock.class, CampfireBlock.class, CartographyTableBlock.class, GrindstoneBlock.class, LecternBlock.class, LoomBlock.class, SmokerBlock.class, StonecutterBlock.class, BarrelBlock.class});

    @Generated
    public Set<Class<?>> q() {
        return this.b;
    }

    public BlockHitResult a(ClientPlayerEntity player) {
        Vec3d start = player.getCameraPosVec(1.0f);
        Vec3d end = start.add(player.getRotationVec(1.0f).multiply(player.getBlockInteractionRange()));
        return player.getWorld().raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player) {
            public VoxelShape getBlockShape(BlockState state, BlockView world, BlockPos pos) {
                for (Class<?> clazz : Delta.h().d().t().a().q()) {
                    if (clazz.isInstance(state.getBlock())) {
                        return super.getBlockShape(state, world, pos);
                    }
                }
                return VoxelShapes.empty();
            }
        });
    }
}
