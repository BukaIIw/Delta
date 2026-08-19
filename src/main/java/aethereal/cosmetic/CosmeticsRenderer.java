package aethereal.cosmetic;

import static aethereal.core.Interface.aM_;
import aethereal.core.Delta;

import aethereal.core.Interface;
import aethereal.cosmetic.CosmeticsType;

import java.util.UUID;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

public class CosmeticsRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> implements Interface {
    public CosmeticsRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    public void render(MatrixStack matrices, VertexConsumerProvider buffers, int light, PlayerEntityRenderState state, float limbAngle, float limbDistance) {
        Entity entity = aM_.world.getEntityById(state.id);
        if (entity != null) {
            UUID uuid = entity.getUuid();
            float tickDelta = aM_.getRenderTickCounter().getTickDelta(false);
            for (Cosmetic cosmetic : Delta.h().d().r().getCosmetics()) {
                if (uuid.equals(cosmetic.getUuid()) && cosmetic.getType() == CosmeticsType.COSMETIC) {
                    matrices.push();
                    cosmetic.getCategory().transform(matrices, (PlayerEntityModel) getContextModel(), cosmetic);
                    cosmetic.getRenderer().render(matrices, cosmetic, buffers, null, null, light, tickDelta);
                    matrices.pop();
                }
            }
        }
    }
}
