package hydrogen.module.misc;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.HydrogenClient;
import hydrogen.core.InterfaceC0020Opcode;
import hydrogen.core.Module;
import hydrogen.util.ChatUtil;
import hydrogen.render.ColorUtil;
import hydrogen.util.InventoryUtil;
import hydrogen.util.Look;
import hydrogen.util.MathUtil;
import hydrogen.util.MoveUtil;
import hydrogen.util.ServerUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.ModuleRegister;
import hydrogen.event.DrawEvent;
import hydrogen.event.InputEvent;
import hydrogen.event.TickEvent;
import hydrogen.module.misc.MineAssistant;
import hydrogen.util.Rotation;

import hydrogen.setting.BooleanSetting;
import hydrogen.setting.ModeSetting;
import hydrogen.setting.SliderSetting;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;

@ModuleRegister(a = "Nuker", b = "Автоматически разрушает блоки в радиусе досягаемости", c = Category.Misc)
public class Nuker extends Module {
    private final ModeSetting b = new ModeSetting("Режим копания территории", "Шахта ФанТайм", "Шахта ФанТайм", "Общий");
    private final SliderSetting c = new SliderSetting("Дистанция копания", 4.0f, 1.0f, 6.0f, 0.5f);
    private final SliderSetting d = new SliderSetting("Скорость копания", 1.0f, 1.0f, 5.0f, 1.0f);
    private final BooleanSetting e = new BooleanSetting("Не копать под себя", true);
    private BlockPos f;

    public Nuker() {
        a(this.b, this.c, this.d, this.e);
    }

    @Override
    public void b() {
        super.b();
        this.f = null;
    }

    @Override
    public void c() {
        super.c();
        this.f = null;
    }

    @EventTarget
    public void a(TickEvent event) {
        MineAssistant assistant = HydrogenClient.h().d().t().ak();
        this.f = null;
        ItemStack tool = aM_.player.getMainHandStack();
        if (tool.isDamageable() && tool.getMaxDamage() - tool.getDamage() < 50) {
            ChatUtil.a((Object) "Работа прекращена во избежание поломки кирки.");
            a();
            return;
        }
        if (this.b.l("Шахта ФанТайм") && ServerUtil.a.a()) {
            assistant.q();
        }
        boolean pickaxe = tool.getItem() instanceof PickaxeItem;
        int minY = this.e.c().booleanValue() ? aM_.player.getBlockY() + ((pickaxe && InventoryUtil.a(tool, "Бульдозер")) ? 1 : 0) : Integer.MIN_VALUE;
        float range = this.c.h().floatValue();
        Box scan = aM_.player.getBoundingBox().expand(range + 1.0f);
        Vec3d eye = aM_.player.getEyePos();
        Vec3d look = aM_.player.getRotationVec(1.0f);
        double bestScore = 1.7976922776554427E308d;
        Direction face = null;
        for (BlockPos pos : BlockPos.iterate(BlockPos.ofFloored(scan.minX, scan.minY, scan.minZ), BlockPos.ofFloored(scan.maxX, scan.maxY, scan.maxZ))) {
            if (pos.getY() >= minY && a(pos, pickaxe, assistant.r())) {
                Vec3d center = pos.toCenterPos();
                Vec3d diff = center.subtract(eye);
                double along = diff.dotProduct(look);
                Vec3d hit = along > 0.0d ? (Vec3d) new Box(pos).raycast(eye, center).orElse(null) : null;
                if (hit != null && eye.squaredDistanceTo(hit) <= range * range && a(eye, pos)) {
                    double score = diff.subtract(look.multiply(along)).lengthSquared();
                    if (score < bestScore) {
                        bestScore = score;
                        this.f = pos.toImmutable();
                        face = Direction.getFacing(hit.subtract(center));
                    }
                }
            }
        }
        if (this.f != null) {
            Rotation base = Rotation.a(eye, this.f.toCenterPos());
            HydrogenClient.h().d().k().a(new Rotation(MathHelper.wrapDegrees(base.c() + MathUtil.a(-3.0f, 3.0f)), MathHelper.clamp(base.d() + MathUtil.a(-3.0f, 3.0f), -90.0f, 90.0f)), 180.0f, 1, 1);
            if (Rotation.b().a(base) <= 20.0d) {
                for (int i = 0; i < this.d.h().intValue(); i++) {
                    aM_.interactionManager.updateBlockBreakingProgress(this.f, face);
                }
                aM_.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    @EventTarget
    public void a(InputEvent event) {
        if (this.f != null) {
            MoveUtil.a(event, Look.b(), 5);
        }
    }

    @EventTarget
    public void a(DrawEvent event) {
        if (event.c() && this.f != null) {
            event.e().a(event.h(), new Box(this.f), ColorUtil.a(255, 0, 0, InterfaceC0020Opcode.aN), 2.0f);
        }
    }

    private boolean a(BlockPos pos, boolean pickaxe, Box mineBox) {
        BlockState state = aM_.world.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        return !this.b.l("Шахта ФанТайм") || (pickaxe && mineBox.contains(pos.toCenterPos()) && state.calcBlockBreakingDelta(aM_.player, aM_.world, pos) >= 1.0f);
    }

    private boolean a(Vec3d eye, BlockPos pos) {
        Box box = new Box(pos).contract(0.05000000385685581d);
        for (int i = -1; i < 8; i++) {
            Vec3d point = i < 0 ? box.getCenter() : new Vec3d((i & 1) == 0 ? box.minX : box.maxX, (i & 2) == 0 ? box.minY : box.maxY, (i & 4) == 0 ? box.minZ : box.maxZ);
            if (aM_.world.raycast(new RaycastContext(eye, point, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, aM_.player)).getBlockPos().equals(pos)) {
                return true;
            }
        }
        return false;
    }
}
