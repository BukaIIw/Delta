package hydrogen.ai;

import hydrogen.core.EventManager;
import hydrogen.core.EventTarget;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Interface;
import hydrogen.event.TickEvent;
import hydrogen.module.combat.Aura;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Manual + per-tick recorder. Works with or without Aura.
 */
public final class AiRecordService implements Interface {
    private static final AiRecordService INSTANCE = new AiRecordService();

    private boolean recording;
    private String name = "auto";

    private AiRecordService() {
    }

    public static AiRecordService get() {
        return INSTANCE;
    }

    public static void bind() {
        EventManager.a(INSTANCE);
    }

    public boolean recording() {
        return this.recording;
    }

    public String name() {
        return this.name;
    }

    public String start(String requested) {
        String next = requested;
        if (AiNamedRecorder.isAuto(next) || AiNamedRecorder.isAuto(this.name)) {
            next = AiNamedRecorder.nextAutoName(aM_);
        } else if (next == null || next.isBlank()) {
            next = this.name;
        }
        this.name = AiNamedRecorder.sanitize(next);
        this.recording = true;
        return this.name;
    }

    public String stop() {
        this.recording = false;
        AiNamedRecorder.flush();
        return this.name;
    }

    public void select(String requested) {
        if (requested != null && !requested.isBlank()) {
            this.name = AiNamedRecorder.sanitize(requested);
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (!this.recording || aM_ == null || aM_.player == null || aM_.world == null) {
            return;
        }
        if (AiNamedRecorder.isAuto(this.name)) {
            this.name = AiNamedRecorder.nextAutoName(aM_);
        }
        LivingEntity target = target();
        float[] features = AiFeatures.capture(aM_, target);
        float labelYaw = 0.0f;
        float labelPitch = 0.0f;
        if (target != null) {
            Vec3d eye = aM_.player.getEyePos();
            Vec3d aim = target.getBoundingBox().getCenter();
            double dx = aim.x - eye.x;
            double dy = aim.y - eye.y;
            double dz = aim.z - eye.z;
            float wantYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float wantPitch = (float) (-Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz))));
            labelYaw = MathHelper.wrapDegrees(aM_.player.getYaw() - wantYaw);
            labelPitch = aM_.player.getPitch() - wantPitch;
        }
        AiNamedRecorder.record(aM_, this.name, features, labelYaw, labelPitch);
    }

    private LivingEntity target() {
        try {
            if (HydrogenClient.h() != null && HydrogenClient.h().d() != null && HydrogenClient.h().d().t() != null) {
                Aura aura = HydrogenClient.h().d().t().B();
                if (aura != null && aura.m() && aura.s() != null) {
                    return aura.s();
                }
            }
        } catch (Exception ignored) {
        }
        HitResult hit = aM_.crosshairTarget;
        if (hit instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (entity instanceof LivingEntity living) {
                return living;
            }
        }
        return null;
    }
}
