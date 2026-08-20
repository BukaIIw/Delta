package hydrogen.module.combat;

import hydrogen.ai.AiAimModel;
import hydrogen.ai.AiFeatures;
import hydrogen.ai.AiNamedRecorder;
import hydrogen.ui.screen.AssistantScreen;
import hydrogen.ui.screen.GUIScreen;

import hydrogen.core.HydrogenClient;
import hydrogen.core.Module;
import hydrogen.lib.log4j.LoggerFactory;
import hydrogen.util.ChatUtil;
import hydrogen.util.InventoryUtil;
import hydrogen.util.Look;
import hydrogen.util.MathUtil;
import hydrogen.util.MoveUtil;
import hydrogen.util.ServerUtil;

import hydrogen.core.Category;
import hydrogen.core.EventTarget;
import hydrogen.core.GlobalEvent;
import hydrogen.core.ModuleRegister;
import hydrogen.event.InputEvent;
import hydrogen.event.WillLandEvent;
import hydrogen.util.Rotation;

import hydrogen.setting.BooleanSetting;
import hydrogen.lib.log4j.Logger_2;
import hydrogen.setting.ModeSetting;
import hydrogen.setting.MultiModeSetting;
import hydrogen.setting.SliderSetting;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.Generated;
import net.minecraft.util.Hand;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.passive.AllayEntity;

@ModuleRegister(a = "Aura", b = "Автоматически атакует цели рядом с вами", c = Category.Combat)
public class Aura extends Module {

    @Generated
    private static final Logger_2 g = LoggerFactory.a((Class<?>) Aura.class);
    private LivingEntity t;
    boolean d;
    private final ModeSetting h = new ModeSetting("Выберите тип наведения", "ФанТайм", "ФанТайм", "ФанТайм ФОВ", "Легит", "SpookyTime", "AI");
    private String aiDataset = "auto";
    private boolean aiRecord;
    private final MultiModeSetting i = new MultiModeSetting("Цели для атаки", new BooleanSetting("Без брони", true), new BooleanSetting("Враждебные мобы", false), new BooleanSetting("Животные", false), new BooleanSetting("Друзья", false), new BooleanSetting("Игроки", true));
    private final SliderSetting j = new SliderSetting("Дистанция атаки", 3.0f, 0.1f, 6.0f, 0.1f);
    private final SliderSetting k = new SliderSetting("Дополнительная дистанция", 0.5f, 0.1f, 3.0f, 0.1f);
    private final BooleanSetting l = new BooleanSetting("Только критические удары", true);
    private final BooleanSetting m = (BooleanSetting) new BooleanSetting("Адаптивные удары", true).a(() -> {
        return this.l.c();
    });
    private final MultiModeSetting n = new MultiModeSetting("Не бить когда", new BooleanSetting("Используется предмет", true), new BooleanSetting("Открыт контейнер", true), new BooleanSetting("Враг за стеной", true));
    private final BooleanSetting o = new BooleanSetting("Пробитие щита", true);
    private final BooleanSetting p = new BooleanSetting("Умный спринт", false);
    private final ModeSetting q = new ModeSetting("Приоритет цели", "Прицел", "Прицел", "Дистанция", "ХП");
    private final ModeSetting r = new ModeSetting("Коррекция движения", "Фокус", "Фокус", "Свободно");
    private final ModeSetting s = new ModeSetting("Визуализация цели", "Сферы", "Сферы", "Круг", "Тест");
    public int b = 0;
    float[] c = {-1.0f, -1.0f, -1.0f, -1.0f, 0.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f};
    private final float[] u = new float[30];
    int[] e = {-1, -1};
    boolean f = false;
    private long stReactUntil;
    private float stHoldYaw = Float.NaN;
    private float stHoldPitch;
    private float stAccel;
    private float stTurnSpeed;
    private long stPreHitUntil;
    private long stHitPauseUntil;
    private boolean stNoiseOn;
    private long stNoiseUntil;
    private float stOrbit;
    private float stOrbitR;
    private long stLastPitchMove;
    private float stPitchMark;
    private long stNudgeEnd;
    private long stNudgeWaitUntil;
    private float stNudgeAmp;
    private float stFreezeYaw = Float.NaN;
    private boolean stYawLocked;
    private boolean stWasOnBox;
    private boolean stLookUpOn;
    private float stLookUpAmt;
    private int stLookUpPhase;
    private float stSentYaw;
    private float stSentPitch;
    private boolean stHasSent;
    private float stNoiseYaw;
    private float stNoisePitch;

    @Generated
    public ModeSetting r() {
        return this.s;
    }

    @Generated
    public LivingEntity s() {
        return this.t;
    }

    public Aura() {
        a(this.j, this.k, this.h, this.r, this.s, this.q, this.i, this.n, this.l, this.m, this.o, this.p);
    }

    @Override
    public void b() {
        if (this.c[9] == -1.0f) {
            this.c[9] = (int) MathUtil.a(9.0f, 13.0f);
        }
        super.b();
        this.c[8] = 2.0f;
        this.c[10] = 0.0f;
        this.c[11] = 0.0f;
        Arrays.fill(this.u, aM_.player != null ? aM_.player.getPitch() : 0.0f);
        this.t = null;
        x();
    }

    @Override
    public void c() {
        super.c();
        this.c[10] = 0.0f;
        this.t = null;
        AiNamedRecorder.flush();
        x();
    }

    private void x() {
        this.stReactUntil = 0L;
        this.stHoldYaw = Float.NaN;
        this.stHoldPitch = 0.0f;
        this.stAccel = 0.0f;
        this.stTurnSpeed = MathUtil.a(12.0f, 18.0f);
        this.stPreHitUntil = 0L;
        this.stHitPauseUntil = 0L;
        this.stNoiseOn = true;
        this.stNoiseUntil = System.currentTimeMillis() + (int) MathUtil.a(55.0f, 100.0f);
        this.stOrbit = MathUtil.a(0.0f, 6.28f);
        this.stOrbitR = MathUtil.a(1.10f, 2.40f);
        this.c[11] = MathUtil.a(0.0f, 6.28f);
        this.stLastPitchMove = System.currentTimeMillis();
        this.stPitchMark = aM_.player != null ? aM_.player.getPitch() : 0.0f;
        this.stNudgeEnd = 0L;
        this.stNudgeWaitUntil = System.currentTimeMillis() + 700L;
        this.stNudgeAmp = 0.0f;
        this.stFreezeYaw = aM_.player != null ? aM_.player.getYaw() : Float.NaN;
        this.stYawLocked = true;
        this.stWasOnBox = false;
        this.stLookUpOn = false;
        this.stLookUpAmt = 0.0f;
        this.stLookUpPhase = 0;
        this.stHasSent = false;
    }

    @EventTarget
    public void a(InputEvent e) {
        if (this.t != null) {
            MoveUtil.a(e, !this.r.l("Фокус") ? Look.b() : this.c[1], 2);
        }
        if (this.c[0] > 0.0f && this.t != null && AuraUtil.a(this.t, this.j.c().floatValue())) {
            e.a(0.0f);
            e.b(0.0f);
            float[] fArr = this.c;
            fArr[0] = fArr[0] - 1.0f;
        }
    }

    @EventTarget
    public void a(GlobalEvent e) {
        if (this.t == null || !b(this.t) || (MaceUtil.a() && !this.d && !aM_.player.getItemCooldownManager().isCoolingDown(Items.MACE.getDefaultStack()))) {
            LivingEntity prev = this.t;
            boolean fresh = prev == null || !b(prev);
            this.t = (fresh && this.n.a("Враг за стеной").c().booleanValue()) ? d(false).or(() -> {
                return d(true);
            }).orElse(null) : v().orElse(null);
            if (this.t != prev && this.t != null) {
                this.c[10] = 0.0f;
                this.c[11] = MathUtil.a(0.0f, 6.28f);
                Arrays.fill(this.u, aM_.player != null ? aM_.player.getPitch() : 0.0f);
                this.stReactUntil = System.currentTimeMillis() + (int) MathUtil.a(45.0f, 75.0f);
                this.stHoldYaw = aM_.player.getYaw();
                this.stHoldPitch = aM_.player.getPitch();
                this.stAccel = 0.0f;
                this.stTurnSpeed = MathUtil.a(12.0f, 18.0f);
                this.stPreHitUntil = 0L;
                this.stNoiseOn = true;
                this.stNoiseUntil = System.currentTimeMillis() + (int) MathUtil.a(55.0f, 100.0f);
                this.stOrbit = MathUtil.a(0.0f, 6.28f);
                this.stOrbitR = MathUtil.a(1.10f, 2.40f);
                this.stLastPitchMove = System.currentTimeMillis();
                this.stPitchMark = aM_.player.getPitch();
                this.stFreezeYaw = aM_.player.getYaw();
                this.stYawLocked = true;
                this.stWasOnBox = false;
                this.stLookUpOn = false;
                this.stLookUpPhase = 0;
                this.stHasSent = false;
            }
        }
        t();
        if (this.t != null) {
            u();
            w();
            u();
            return;
        }
        this.c[8] = 1.0f;
    }

    @EventTarget
    public void a(WillLandEvent e) {
        this.d = e.b() && !aM_.player.isOnGround();
    }

    private int a(int from, int to) {
        for (int i = from; i < to; i++) {
            if (aM_.player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                return i;
            }
        }
        return -1;
    }

    private boolean y() {
        Item item = aM_.player.getMainHandStack().getItem();
        return (item instanceof SwordItem) || (item instanceof AxeItem) || item == Items.MACE;
    }

    private boolean z() {
        return this.h.c().equals("SpookyTime");
    }

    private int stNextHit() {
        return ((int) this.c[2]) + 2;
    }

    private boolean stLookUpHit() {
        int n = this.stNextHit();
        return n == 3 || n == 5 || n == 6 || n == 7 || n == 8;
    }

    private boolean stAimOk(float yaw, float pitch) {
        if (this.t == null) {
            return false;
        }
        return AuraUtil.a(yaw, pitch, this.j.c().floatValue(), this.t, !this.n.a("Враг за стеной").c().booleanValue());
    }

    private void t() {
        if (this.o.c().booleanValue() && this.t != null && this.t.isBlocking()) {
            return;
        }
        if (this.e[0] != -1) {
            aM_.player.getInventory().selectedSlot = this.e[0];
            this.e[0] = -1;
        }
        if (this.e[1] != -1 && HydrogenClient.h().d().v().a().a().isEmpty()) {
            HydrogenClient.h().d().v().a().a(aM_.player.getInventory().selectedSlot, this.e[1], 1);
            this.e[1] = -1;
        }
    }

    private void u() {
        float hitYaw = this.stHasSent ? this.stSentYaw : aM_.player.getYaw();
        float hitPitch = this.stHasSent ? this.stSentPitch : aM_.player.getPitch();
        if (!this.stAimOk(hitYaw, hitPitch) && !this.stAimOk(aM_.player.getYaw(), aM_.player.getPitch())) {
            return;
        }
        if (this.o.c().booleanValue() && this.t.isBlocking()) {
            if (aM_.player.getMainHandStack().getItem() instanceof AxeItem) {
                aM_.interactionManager.attackEntity(aM_.player, this.t);
                aM_.player.swingHand(Hand.MAIN_HAND);
            }
            int hotbar = a(0, 9);
            if (hotbar != -1) {
                if (aM_.player.getInventory().selectedSlot != hotbar) {
                    if (this.e[0] == -1) {
                        this.e[0] = aM_.player.getInventory().selectedSlot;
                    }
                    aM_.player.getInventory().selectedSlot = hotbar;
                }
            } else {
                int inventory = a(9, 36);
                if (inventory != -1 && this.e[1] == -1 && HydrogenClient.h().d().v().a().a().isEmpty()) {
                    this.e[1] = inventory;
                    HydrogenClient.h().d().v().a().a(inventory, aM_.player.getInventory().selectedSlot, 1);
                }
            }
        }
        if (q()) {
            boolean skip = false;
            if ((HydrogenClient.h().d().t().H().e || (aM_.player.fallDistance > 2.0f && HydrogenClient.h().d().t().H().c.c().booleanValue())) && InventoryUtil.b(Items.MACE) != -1) {
                if (aM_.player.fallDistance < 1.5f) {
                    return;
                }
                double landDist = ((Double) MaceUtil.a(aM_.player, (World) aM_.world).map(pos -> {
                    return Double.valueOf(pos.distanceTo(this.t.getPos()));
                }).orElse(Double.valueOf(33.0d))).doubleValue();
                boolean hitNow = landDist > 2.0d;
                if ((!this.d && !MaceUtil.b() && HydrogenClient.h().d().t().H().b.c().booleanValue() && !hitNow) || !MaceUtil.a() || aM_.player.isGliding()) {
                    return;
                } else {
                    skip = true;
                }
            }
            if (((platform.inject.accessors.ClientPlayerEntityAccessor) aM_.player).getWasSprinting() && !aM_.player.isTouchingWater() && !aM_.player.isInLava() && !aM_.player.isSwimming() && !aM_.player.isOnGround() && !skip) {
                if (!this.p.c().booleanValue()) {
                    ((platform.inject.accessors.ClientPlayerEntityAccessor) aM_.player).setWasSprinting(false);
                    aM_.player.setSprinting(false);
                    aM_.player.networkHandler.sendPacket(new ClientCommandC2SPacket(aM_.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                    this.c[0] = 1.0f;
                } else {
                    this.c[0] = 1.0f;
                    if (((platform.inject.accessors.ClientPlayerEntityAccessor) aM_.player).getWasSprinting()) {
                        return;
                    }
                }
            }
            if (aM_.interactionManager != null) {
                this.c[3] = 0.0f;
                aM_.interactionManager.attackEntity(aM_.player, this.t);
                aM_.player.swingHand(Hand.MAIN_HAND);
                this.b = 0;
                this.c[5] = MathUtil.a(8.0f, 10.0f);
                this.c[9] = (int) MathUtil.a(9.0f, 13.0f);
                if (this.c[2] == -1.0f) {
                    this.c[4] = (int) MathUtil.a(30.0f, 35.0f);
                }
                float[] fArr = this.c;
                fArr[2] = fArr[2] + 1.0f;
                this.stPreHitUntil = 0L;
                this.stLookUpOn = false;
                this.stLookUpPhase = 0;
                if (z()) {
                    this.stHitPauseUntil = System.currentTimeMillis() + (int) MathUtil.a(20.0f, 45.0f);
                }
            }
        }
    }

    public boolean q() {
        if (this.n.a("Используется предмет") != null && this.n.a("Используется предмет").c().booleanValue() && aM_.player.isUsingItem() && aM_.player.getItemUseTimeLeft() > 0 && this.b >= 8) {
            this.b = 8;
            return false;
        }
        if ((this.n.a("Открыт контейнер") != null && this.n.a("Открыт контейнер").c().booleanValue() && aM_.currentScreen != null && !(aM_.currentScreen instanceof GUIScreen) && !(aM_.currentScreen instanceof AssistantScreen)) || !AuraUtil.a(this.t, this.j.c().floatValue())) {
            return false;
        }
        if (HydrogenClient.h().d().t().H().e) {
            if (aM_.player.getItemCooldownManager().isCoolingDown(aM_.player.getMainHandStack())) {
                return false;
            }
        } else if (aM_.player.fallDistance > 1.5f) {
            if (aM_.player.getItemCooldownManager().isCoolingDown(aM_.player.getMainHandStack()) || this.b <= 3) {
                return false;
            }
        } else if (MaceUtil.a()) {
            if (aM_.player.getItemCooldownManager().isCoolingDown(aM_.player.getMainHandStack()) || aM_.player.getAttackCooldownProgress(0.5f) < 0.9f) {
                return false;
            }
        } else if (aM_.player.getAttackCooldownProgress(0.5f) < 0.9f || this.b < 10) {
            return false;
        }
        if (z()) {
            long now = System.currentTimeMillis();
            if (now < this.stHitPauseUntil) {
                return false;
            }
            if (!y()) {
                return false;
            }
            if (this.t != null && this.t.hurtTime > 7) {
                return false;
            }
            if (this.stLookUpOn && this.stLookUpPhase == 0) {
                return false;
            }
            if (!this.stAimOk(this.stHasSent ? this.stSentYaw : aM_.player.getYaw(), this.stHasSent ? this.stSentPitch : aM_.player.getPitch())
                    && !this.stAimOk(aM_.player.getYaw(), aM_.player.getPitch())) {
                return false;
            }
        }
        return AuraUtil.c() || (this.m.c().booleanValue() && aM_.player.isOnGround() && !aM_.player.input.playerInput.jump()) || !AuraUtil.b();
    }

    private boolean a(LivingEntity entity) {
        if (HydrogenClient.h().d().t().G().m() && aM_.player.isGliding()) {
            return true;
        }
        return AuraUtil.a((Entity) entity, ((double) (this.j.c().floatValue() + this.k.c().floatValue())) + (aM_.player.getVelocity().length() * 3.0d) + ((double) ((InventoryUtil.b(Items.MACE) == -1 || ((double) aM_.player.fallDistance) <= 1.5d) ? 0.0f : 1.5f)) + ((double) ((HydrogenClient.h().d().t().H().m() && InventoryUtil.b(Items.MACE) != -1 && ((Boolean) MaceUtil.a(aM_.player, (World) aM_.world).map(p -> {
            return Boolean.valueOf(aM_.player.getY() - p.getY() > 2.0d);
        }).orElse(false)).booleanValue()) ? 10 : 0)));
    }

    private Optional<LivingEntity> v() {
        return d(true);
    }

    private Optional<LivingEntity> d(boolean allowBehindWalls) {
        Comparator<LivingEntity> comparatorComparingDouble;
        Comparator<LivingEntity> order;
        if (aM_.world == null || aM_.player == null) {
            return Optional.empty();
        }
        Vec3d eye = aM_.player.getEyePos();
        double reach = this.j.c().floatValue() + this.k.c().floatValue();
        if (MaceUtil.a()) {
            Vec3d landing = MaceUtil.a(aM_.player, (World) aM_.world).orElse(null);
            Vec3d landingEye = landing != null ? landing.add(0.0d, aM_.player.getStandingEyeHeight(), 0.0d) : null;
            order = Comparator.comparing((LivingEntity e) ->
                    Boolean.valueOf(!AuraUtil.a(eye, e, reach) && (landingEye == null || !AuraUtil.a(landingEye, e, reach)))
            ).thenComparing((LivingEntity e2) ->
                    Boolean.valueOf(aM_.player.fallDistance > 1.0f && !c(e2))
            ).thenComparingDouble((LivingEntity v0) -> AuraUtil.a(v0));
        } else {
            switch (this.q.c()) {
                case "Дистанция":
                    comparatorComparingDouble = Comparator.comparingDouble((v0) -> {
                        return AuraUtil.a(v0);
                    });
                    break;
                case "ХП":
                    comparatorComparingDouble = Comparator.comparingDouble((v0) -> {
                        return v0.getHealth();
                    });
                    break;
                default:
                    comparatorComparingDouble = Comparator.comparingDouble(e3 -> {
                        return Math.acos(MathHelper.clamp(Vec3d.fromPolar(aM_.player.getPitch(), aM_.player.getYaw()).dotProduct(e3.getBoundingBox().getCenter().subtract(eye).normalize()), -1.0d, 1.0d));
                    });
                    break;
            }
            order = comparatorComparingDouble;
        }
        Stream<LivingEntity> stream2 = StreamSupport.stream(aM_.world.getEntities().spliterator(), false)
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .filter(e4 -> e4 != aM_.player && e4.isAlive())
                .filter(this::a)
                .filter(this::b);
        if (!allowBehindWalls) {
            stream2 = stream2.filter(e5 -> {
                return AuraUtil.a(eye, e5, reach);
            });
        }
        return stream2.min(order);
    }

    private boolean b(LivingEntity entity) {
        if (entity == null || !entity.isAlive() || !a(entity)) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            boolean isFriend = HydrogenClient.h().d().e().d(entity.getName().getString());
            boolean naked = Stream.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).noneMatch(slot -> {
                return entity.getEquippedStack(slot).getItem() instanceof ArmorItem;
            });
            if (!a("Игроки")) {
                return false;
            }
            if (isFriend) {
                return a("Друзья");
            }
            return !naked || a("Без брони");
        }
        if ((entity instanceof HostileEntity) || (entity instanceof SlimeEntity) || (entity instanceof FlyingEntity) || (entity instanceof EnderDragonEntity)) {
            return a("Враждебные мобы");
        }
        if ((entity instanceof PassiveEntity) || (entity instanceof GolemEntity) || (entity instanceof AllayEntity) || (entity instanceof AmbientEntity)) {
            return a("Животные");
        }
        return false;
    }

    private boolean a(String name) {
        BooleanSetting setting = this.i.a(name);
        return setting != null && setting.c().booleanValue();
    }

    private boolean c(LivingEntity entity) {
        return Stream.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET).anyMatch(s -> {
            return entity.getEquippedStack(s).getItem() instanceof ArmorItem;
        });
    }

    private void w() {
        Vec3d class_243VarMethod_33571 = aM_.player.getEyePos();
        LivingEntity class_1309Var = this.t;
        double dFloatValue = this.j.c().floatValue();
        boolean z = this.h.c().contains("ФанТайм") || !this.n.a("Враг за стеной").c().booleanValue();
        Vec3d targetPosition = AuraUtil.a(class_243VarMethod_33571, class_1309Var, dFloatValue, z);
        float yawToTarget = targetPosition == Vec3d.ZERO ? Look.b() : (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(targetPosition.z, targetPosition.x)) - 90.0d);
        float pitchToTarget = targetPosition == Vec3d.ZERO ? Look.c() : (float) (-Math.toDegrees(Math.atan2(targetPosition.y, Math.hypot(targetPosition.x, targetPosition.z))));
        System.arraycopy(this.u, 0, this.u, 1, 29);
        this.u[0] = pitchToTarget;
        if (this.t != null && this.b >= 2 && ((ServerUtil.a.a(this.t) > 6.0f || this.c[2] > 43.0f) && this.c[2] >= 33.0f && ((this.b == 4 || Math.random() > 0.5d) && (!this.f || !AuraUtil.a(aM_.player.getYaw(), aM_.player.getPitch(), 3.0d, this.t, false))))) {
            ((platform.inject.invokers.MinecraftClientInvoker) aM_).invokeDoAttack();
            ChatUtil.a(Boolean.valueOf(this.f));
            if (Math.random() > 0.5d) {
                this.f = !this.f;
            }
            this.c[2] = (int) MathUtil.a(-10.0f, 10.0f);
        }
        boolean skip = (this.n.a("Используется предмет").c().booleanValue() && aM_.player.isUsingItem() && aM_.player.getItemUseTimeLeft() > 0 && this.b >= 8) || !(this.n.a("Открыт контейнер") == null || !this.n.a("Открыт контейнер").c().booleanValue() || aM_.currentScreen == null || (aM_.currentScreen instanceof GUIScreen) || (aM_.currentScreen instanceof AssistantScreen));
        if ((this.c[3] <= 0.0f && q()) || AuraUtil.a(this.b, this.t, skip)) {
            this.c[3] = 1.0f;
            if (!aM_.player.isTouchingWater() && this.p.c().booleanValue() && !aM_.player.isOnGround()) {
                this.c[0] = 1.0f;
            }
        }
        if (HydrogenClient.h().d().t().F().m() && q() && AuraUtil.a(this.t, 3.0d) && aM_.player.isGliding()) {
            HydrogenClient.h().d().k().a(new Rotation(yawToTarget, pitchToTarget), 180.0f, 0, 3);
        }
        if (!this.h.c().contains("ФанТайм") && !this.h.c().equals("SpookyTime") && (InventoryUtil.b(Items.MACE) != -1 || (HydrogenClient.h().d().t().H().e && aM_.player.fallDistance > 3.0f && ((Double) MaceUtil.a(aM_.player, (World) aM_.world).map(pos -> {
            return Double.valueOf(pos.distanceTo(aM_.player.getPos()));
        }).orElse(Double.valueOf(0.0d))).doubleValue() > 2.0d && AuraUtil.a(this.t, 4.0d + (aM_.player.getVelocity().length() * 3.0d))))) {
            float t = aM_.player.age + aM_.getRenderTickCounter().getTickDelta(false);
            float smoothW = ((float) ((((Math.sin(t * 0.31f) * 0.5d) + (Math.sin((t * 0.73f) + 1.1f) * 0.3000000314327426d)) + (Math.sin((t * 1.7f) + 2.6f) * 0.2000000098386085d)) * 8.0d)) / 8.0f;
            float finalYaw = AuraUtil.a(aM_.player.getYaw(), yawToTarget, 0.8f);
            float finalPitch = AuraUtil.a(aM_.player.getPitch(), pitchToTarget, 0.8f);
            HydrogenClient.h().d().k().a(new Rotation(finalYaw + smoothW, finalPitch + smoothW), 180.0f, 1, 2);
        }
        switch (this.h.c()) {
            case "ФанТайм":
            case "ФанТайм ФОВ":
                a(yawToTarget, pitchToTarget, targetPosition);
                break;
            case "Легит":
                b(yawToTarget, pitchToTarget, targetPosition);
                break;
            case "SpookyTime":
                spooky(yawToTarget, pitchToTarget, targetPosition);
                break;
            case "AI":
                aiAim(yawToTarget, pitchToTarget);
                break;
        }
        float[] fArr = this.c;
        fArr[3] = fArr[3] - 1.0f;
        float[] fArr2 = this.c;
        fArr2[5] = fArr2[5] - 1.0f;
        float[] fArr3 = this.c;
        fArr3[8] = fArr3[8] - 1.0f;
        this.c[1] = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(this.t.getZ() - aM_.player.getZ(), this.t.getX() - aM_.player.getX())) - 90.0d);
    }

    private void a(float yawToTarget, float pitchToTarget, Vec3d vec3d) {
        float t = aM_.player.age + aM_.getRenderTickCounter().getTickDelta(false);
        float smoothW = (float) ((Math.sin(((double) t) * 0.4000000008323731d) * 3.0d) + (Math.sin((((double) t) * 0.9500002390239708d) + 1.4000004888461306d) * 2.0d));
        float smoothH = (float) ((Math.cos((((double) t) * 0.5d) + 0.7000001555309916d) * 0.5d) + (Math.cos((((double) t) * 0.7800000620494261d) + 3.10000031689524d) * 1.5d));
        float finalPitch = AuraUtil.a(aM_.player.getPitch(), this.u[MathHelper.clamp(10 - this.b, 0, 29)] + (smoothH * 1.5f), MathUtil.a(0.1f, 0.5f));
        float finalYaw = AuraUtil.a(aM_.player.getYaw(), yawToTarget + smoothW, MathUtil.a(0.1f, 0.4f));
        if (this.c[3] >= 0.0f) {
            if (!AuraUtil.a(aM_.player.getYaw(), aM_.player.getPitch(), this.j.c().floatValue(), this.t, true) && this.c[8] <= 0.0f) {
                finalYaw = yawToTarget;
            }
            if (!AuraUtil.a(yawToTarget, finalPitch, this.j.c().floatValue(), this.t, true) && this.c[8] <= 0.0f) {
                finalPitch = pitchToTarget;
            }
            if (!AuraUtil.a(aM_.player.getYaw() + smoothW, aM_.player.getYaw() + smoothH, this.j.c().floatValue(), this.t, true) && AuraUtil.a(aM_.player.getYaw(), aM_.player.getPitch(), this.j.c().floatValue(), this.t, true)) {
                smoothW = MathHelper.clamp(smoothW, -0.05f, 0.05f);
                smoothH = MathHelper.clamp(smoothH, -0.05f, 0.05f);
            }
        }
        if (this.b <= 4 && this.c[2] % 2.0f == 0.0f) {
            finalYaw = aM_.player.getYaw();
        }
        HydrogenClient.h().d().k().a(new Rotation(finalYaw + smoothW, (this.h.c().equals("ФанТайм") ? finalPitch : Look.c()) + smoothH), 220.0f, 1, 1);
    }

    private void b(float yawToTarget, float pitchToTarget, Vec3d vec3d) {
        float t = aM_.player.age + aM_.getRenderTickCounter().getTickDelta(false);
        float fSin = ((float) (((Math.sin(t * 0.31f) * 0.5d) + (Math.sin((t * 1.7f) + 2.6f) * 0.2000000098386085d)) * 8.0d)) / 4.0f;
        float smoothW = fSin;
        float smoothH = fSin;
        float finalYaw = AuraUtil.a(aM_.player.getYaw(), yawToTarget, MathUtil.a(0.2f, 0.35f));
        float finalPitch = AuraUtil.a(aM_.player.getPitch(), pitchToTarget, MathUtil.a(0.15f, 0.25f));
        if (this.c[3] >= 0.0f) {
            finalPitch = AuraUtil.a(aM_.player.getPitch(), pitchToTarget, 0.35f);
            smoothH /= 3.0f;
            smoothW /= 3.0f;
            if (!AuraUtil.a(aM_.player.getYaw(), aM_.player.getPitch(), this.j.c().floatValue(), this.t, true)) {
                finalYaw = AuraUtil.a(aM_.player.getYaw(), yawToTarget, MathUtil.a(0.7f, 1.0f));
            }
        }
        if (!AuraUtil.a(finalYaw + smoothW, finalPitch + smoothH, this.j.c().floatValue(), this.t, true) && AuraUtil.a(yawToTarget, pitchToTarget, this.j.c().floatValue(), this.t, true)) {
            smoothW = MathHelper.clamp(smoothW, -0.15f, 0.15f);
            smoothH = MathHelper.clamp(smoothH, -0.15f, 0.15f);
        }
        if (this.c[5] >= 0.0f) {
            smoothW *= 8.0f;
            if (this.b >= 1 && this.c[2] % 5.0f == 0.0f) {
                finalPitch = AuraUtil.a(aM_.player.getPitch(), -pitchToTarget, 0.05f);
            }
        }
        HydrogenClient.h().d().k().a(new Rotation(finalYaw + smoothW, finalPitch + smoothH), 180.0f, 1, 1);
    }

    private float stClamp(float current, float next, float maxStep) {
        return current + MathHelper.clamp(MathHelper.wrapDegrees(next - current), -maxStep, maxStep);
    }

    private float iosEase(float t) {
        t = MathHelper.clamp(t, 0.0f, 1.0f);
        return (float) ((1.0d - Math.cos(t * Math.PI)) * 0.5d);
    }

    private float iosSin(float tick, float freq, float amp, float phase) {
        float wave = (float) Math.sin((tick * freq) + phase);
        float shaped = (this.iosEase((wave + 1.0f) * 0.5f) * 2.0f) - 1.0f;
        return shaped * amp;
    }

    private boolean stIdle() {
        double h = (aM_.player.getVelocity().x * aM_.player.getVelocity().x) + (aM_.player.getVelocity().z * aM_.player.getVelocity().z);
        return h < 0.0045d;
    }

    private boolean stStanding() {
        return this.stIdle() && aM_.player.isOnGround();
    }

    private boolean stJumping() {
        return !aM_.player.isOnGround() || aM_.player.input.playerInput.jump();
    }

    private void stTickNoise(long now, float curYaw) {
        if (now < this.stNoiseUntil) {
            return;
        }
        this.stNoiseOn = !this.stNoiseOn;
        if (this.stNoiseOn) {
            this.stNoiseUntil = now + (int) MathUtil.a(70.0f, 130.0f);
            this.c[11] = MathUtil.a(0.0f, 6.28f);
            this.stOrbitR = MathUtil.a(1.10f, 2.40f);
            this.stFreezeYaw = curYaw;
            this.stYawLocked = true;
        } else {
            this.stNoiseUntil = now + (int) MathUtil.a(45.0f, 85.0f);
            this.stYawLocked = false;
        }
    }

    private float stNoiseMul(long now) {
        if (!this.stNoiseOn) {
            return 0.55f;
        }
        long left = Math.max(0L, this.stNoiseUntil - now);
        float rise = this.iosEase(MathHelper.clamp((130.0f - (float) left) / 14.0f, 0.0f, 1.0f));
        float fall = this.iosEase(MathHelper.clamp((float) left / 12.0f, 0.0f, 1.0f));
        return 0.85f + (0.55f * rise * fall);
    }

    private float stPitchNudge(long now) {
        if (this.stNudgeEnd > 0L && now < this.stNudgeEnd) {
            float elapsed = 70.0f - (float) (this.stNudgeEnd - now);
            float p = MathHelper.clamp(elapsed / 70.0f, 0.0f, 1.0f);
            return this.stNudgeAmp * (float) Math.sin(p * Math.PI);
        }
        if (this.stNudgeEnd > 0L && now >= this.stNudgeEnd) {
            this.stNudgeEnd = 0L;
            this.stNudgeWaitUntil = now + 700L;
        }
        if (now >= this.stNudgeWaitUntil) {
            this.stNudgeEnd = now + 70L;
            this.stNudgeWaitUntil = Long.MAX_VALUE;
            this.stNudgeAmp = MathUtil.a(0.90f, 1.90f);
            if (Math.random() < 0.5d) {
                this.stNudgeAmp = -this.stNudgeAmp;
            }
        }
        return 0.0f;
    }

    private void stMarkPitch(long now, float pitch) {
        if (Math.abs(pitch - this.stPitchMark) > 0.12f) {
            this.stLastPitchMove = now;
            this.stPitchMark = pitch;
        }
    }

    private void stLook(float yaw, float pitch) {
        pitch = MathHelper.clamp(pitch, -90.0f, 90.0f);
        long now = 60;
        this.stMarkPitch(now, pitch);
        this.stSentYaw = yaw;
        this.stSentPitch = pitch;
        this.stHasSent = true;
        HydrogenClient.h().d().k().a(new Rotation(yaw, pitch), 30.0f, 1, 1);
    }

    private void spooky(float yawToTarget, float pitchToTarget, Vec3d vec3d) {
        float curYaw = aM_.player.getYaw();
        float curPitch = aM_.player.getPitch();
        float reach = this.j.c().floatValue();
        boolean onBox = this.stAimOk(curYaw, curPitch);
        boolean swingReady = aM_.player.getAttackCooldownProgress(0.5f) >= 0.88f && this.b >= 8;

        this.stTickNoise(onBox, swingReady);
        float noiseMultiplier = swingReady ? 0.35f : 1.0f;
        float aimYaw = yawToTarget + (this.stNoiseYaw * noiseMultiplier);
        float aimPitch = MathHelper.clamp(pitchToTarget + (this.stNoisePitch * noiseMultiplier), -90.0f, 90.0f);

        float targetYawDelta = MathHelper.wrapDegrees(yawToTarget - curYaw);
        float targetPitchDelta = pitchToTarget - curPitch;
        float totalError = Math.abs(targetYawDelta) + Math.abs(targetPitchDelta);
        float wantedTurnSpeed = MathHelper.clamp(22.0f + (totalError * 0.45f), 24.0f, 50.0f);
        if (swingReady) {
            wantedTurnSpeed = Math.max(wantedTurnSpeed, 42.0f);
        }

        this.stAccel = Math.min(1.0f, this.stAccel + MathUtil.a(0.20f, 0.28f));
        this.stTurnSpeed = MathHelper.lerp(0.40f, this.stTurnSpeed, wantedTurnSpeed);
        float accelMultiplier = 0.55f + (this.stAccel * 0.45f);

        float yawDelta = MathHelper.wrapDegrees(aimYaw - curYaw);
        float pitchDelta = aimPitch - curPitch;
        float maxYawStep = MathHelper.clamp((8.0f + (Math.abs(yawDelta) * 0.65f)) * accelMultiplier, 10.0f, this.stTurnSpeed);
        float maxPitchLimit = Math.min(34.0f, this.stTurnSpeed * 0.80f);
        float maxPitchStep = MathHelper.clamp((6.0f + (Math.abs(pitchDelta) * 0.55f)) * accelMultiplier, 8.0f, maxPitchLimit);

        if (onBox && !swingReady) {
            maxYawStep = Math.min(maxYawStep, 16.0f);
            maxPitchStep = Math.min(maxPitchStep, 12.0f);
        }

        float finalYaw = this.stClamp(curYaw, aimYaw, maxYawStep);
        float finalPitch = MathHelper.clamp(this.stClamp(curPitch, aimPitch, maxPitchStep), -90.0f, 90.0f);
        boolean finalOnBox = AuraUtil.a(finalYaw, finalPitch, reach, this.t, true);

        if (!finalOnBox && (onBox || swingReady)) {
            float cleanYaw = this.stClamp(curYaw, yawToTarget, swingReady ? Math.max(maxYawStep, 32.0f) : maxYawStep);
            float cleanPitch = MathHelper.clamp(this.stClamp(curPitch, pitchToTarget, swingReady ? Math.max(maxPitchStep, 24.0f) : maxPitchStep), -90.0f, 90.0f);
            if (AuraUtil.a(cleanYaw, cleanPitch, reach, this.t, true)) {
                finalYaw = cleanYaw;
                finalPitch = cleanPitch;
            } else if (onBox) {
                finalYaw = curYaw;
                finalPitch = curPitch;
            }
        }

        this.stLook(finalYaw, finalPitch);
    }

    private void stTickNoise(boolean onBox, boolean swingReady) {

    }

    public boolean aiRecording() {
        return this.aiRecord.c().booleanValue();
    }

    public String aiDatasetName() {
        return this.aiDataset.c();
    }

    public String startAiRecord(String requested) {
        this.h.a("AI");
        String name = requested;
        if (AiNamedRecorder.isAuto(name) || AiNamedRecorder.isAuto(this.aiDataset.c())) {
            name = AiNamedRecorder.nextAutoName(aM_);
        } else if (name == null || name.isBlank()) {
            name = this.aiDataset.c();
        }
        this.aiDataset.a(AiNamedRecorder.sanitize(name));
        this.aiRecord.a(true);
        if (!m()) {
            a(true);
        }
        return this.aiDataset.c();
    }

    public String stopAiRecord() {
        this.aiRecord.a(false);
        AiNamedRecorder.flush();
        return this.aiDataset.c();
    }

    private void aiAim(float yawToTarget, float pitchToTarget) {
        if (this.aiRecord.c().booleanValue() && AiNamedRecorder.isAuto(this.aiDataset.c())) {
            this.aiDataset.a(AiNamedRecorder.nextAutoName(aM_));
        }
        float[] features = AiFeatures.capture(aM_, this.t);
        float labelYaw = MathHelper.wrapDegrees(aM_.player.getYaw() - yawToTarget);
        float labelPitch = aM_.player.getPitch() - pitchToTarget;
        if (this.aiRecord.c().booleanValue()) {
            AiNamedRecorder.record(aM_, this.aiDataset.c(), features, labelYaw, labelPitch);
        }
        AiAimModel.ensureLoaded(aM_, this.aiDataset.c());
        float[] pred = AiAimModel.predict(features);
        float blend = AiAimModel.sampleCount() > 16 ? 0.65f : 0.25f;
        float wantYaw = yawToTarget + pred[0];
        float wantPitch = pitchToTarget + pred[1];
        float finalYaw = AuraUtil.a(aM_.player.getYaw(), wantYaw, MathHelper.lerp(blend, 0.18f, 0.42f));
        float finalPitch = AuraUtil.a(aM_.player.getPitch(), wantPitch, MathHelper.lerp(blend, 0.14f, 0.32f));
        HydrogenClient.h().d().k().a(new Rotation(finalYaw, finalPitch), 180.0f, 1, 1);
    }
}
