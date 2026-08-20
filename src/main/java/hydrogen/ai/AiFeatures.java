package hydrogen.ai;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Compact combat-state vector. Names match the recorded columns so a later
 * model can be swapped without changing the schema.
 */
public final class AiFeatures {
    public static final String[] COLUMNS = {
            "fallDistance",
            "velX",
            "velY",
            "velZ",
            "speedHoriz",
            "onGround",
            "inWater",
            "sprinting",
            "hurtTime",
            "attackCooldown",
            "yaw",
            "pitch",
            "targetDx",
            "targetDy",
            "targetDz",
            "targetDist",
            "targetHurt",
            "targetHealth",
            "deltaYawToTarget",
            "deltaPitchToTarget"
    };

    private AiFeatures() {
    }

    public static float[] capture(MinecraftClient mc, LivingEntity target) {
        float[] v = new float[COLUMNS.length];
        if (mc == null || mc.player == null) {
            return v;
        }
        var p = mc.player;
        Vec3d vel = p.getVelocity();
        v[0] = p.fallDistance;
        v[1] = (float) vel.x;
        v[2] = (float) vel.y;
        v[3] = (float) vel.z;
        v[4] = (float) Math.hypot(vel.x, vel.z);
        v[5] = p.isOnGround() ? 1f : 0f;
        v[6] = p.isTouchingWater() ? 1f : 0f;
        v[7] = p.isSprinting() ? 1f : 0f;
        v[8] = p.hurtTime;
        v[9] = p.getAttackCooldownProgress(0.5f);
        v[10] = p.getYaw();
        v[11] = p.getPitch();
        if (target != null) {
            Vec3d eye = p.getEyePos();
            Vec3d aim = target.getBoundingBox().getCenter();
            double dx = aim.x - eye.x;
            double dy = aim.y - eye.y;
            double dz = aim.z - eye.z;
            v[12] = (float) dx;
            v[13] = (float) dy;
            v[14] = (float) dz;
            v[15] = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            v[16] = target.hurtTime;
            v[17] = target.getHealth();
            float wantYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float wantPitch = (float) (-Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz))));
            v[18] = MathHelper.wrapDegrees(wantYaw - p.getYaw());
            v[19] = wantPitch - p.getPitch();
        }
        return v;
    }

    public static String header() {
        return String.join(",", COLUMNS) + ",labelYaw,labelPitch";
    }

    public static String row(float[] features, float labelYaw, float labelPitch) {
        StringBuilder sb = new StringBuilder(256);
        for (float f : features) {
            sb.append(f).append(',');
        }
        sb.append(labelYaw).append(',').append(labelPitch);
        return sb.toString();
    }
}
