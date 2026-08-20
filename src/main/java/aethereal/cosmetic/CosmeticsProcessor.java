package aethereal.cosmetic;

import aethereal.core.NativeMethodLookup;
import aethereal.core.Interface;

import static aethereal.core.Interface.aM_;

import aethereal.config.BaseProcessor;
import aethereal.core.EventTarget;
import aethereal.cosmetic.Cosmetic;
import aethereal.cosmetic.CosmeticsCategory;
import aethereal.cosmetic.CosmeticsRenderer;
import aethereal.cosmetic.CosmeticsType;
import aethereal.event.BackendEvent;
import aethereal.network.PacketSecurity;

import aethereal.api.Compile;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Generated;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.EntityType;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.joml.Vector3f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;

public class CosmeticsProcessor extends BaseProcessor {
    private final List<Cosmetic> cosmetics = new CopyOnWriteArrayList();
    private final ScheduledExecutorService bootstrapper = Executors.newSingleThreadScheduledExecutor();

    @Override
    @Compile
    public void setup() {
        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(new LivingEntityFeatureRendererRegistrationCallback() {
            public void registerRenderers(EntityType class_1299Var, LivingEntityRenderer class_922Var, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper registrationHelper, EntityRendererFactory.Context class_5618Var) {
                CosmeticsProcessor.lambda$setup$0(class_1299Var, class_922Var, registrationHelper, class_5618Var);
            }
        });
    }

    static {
        NativeMethodLookup.lookup(CosmeticsProcessor.class, 24);
    }

    @Generated
    public List<Cosmetic> getCosmetics() {
        return this.cosmetics;
    }

    @Generated
    public ScheduledExecutorService getBootstrapper() {
        return this.bootstrapper;
    }

    public static void lambda$setup$0(EntityType type, LivingEntityRenderer renderer, LivingEntityFeatureRendererRegistrationCallback.RegistrationHelper helper, EntityRendererFactory.Context context) {
        if (type == EntityType.PLAYER) {
            FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> playerRenderer = (FeatureRendererContext) renderer;
            helper.register(new CosmeticsRenderer(playerRenderer));
        }
    }

    @EventTarget
    public void onBackend(BackendEvent event) {
        if (event.b() && "cosmetics".equals(event.d().b())) {
            PacketSecurity security = event.d().a();
            String payload = event.d().c();
            String action = security.a(payload, "action");
            String type = security.a(payload, "type");
            String uuid = security.a(payload, "uuid");
            String cosmetic = security.a(payload, "cosmetic");
            if (cosmetic != null && uuid != null) {
                if ("WEAR".equals(action)) {
                    if (CosmeticsType.COSMETIC.name().equals(type)) {
                        this.bootstrapper.execute(() -> {
                            registerCosmetic(security, UUID.fromString(uuid), cosmetic);
                        });
                    }
                } else if ("UNWEAR".equals(action) && CosmeticsType.COSMETIC.name().equals(type)) {
                    this.bootstrapper.execute(() -> {
                        unregisterCosmetic(UUID.fromString(uuid), CosmeticsCategory.valueOf(security.a(cosmetic, "category")));
                    });
                }
            }
        }
        if (event.c()) {
            this.cosmetics.clear();
        }
    }

    private void registerCosmetic(PacketSecurity security, UUID uuid, String cosmetic) {
        CosmeticsCategory category = CosmeticsCategory.valueOf(security.a(cosmetic, "category"));
        String name = security.a(cosmetic, "name");
        String geometry = security.a(cosmetic, "geometry");
        String animations = security.a(cosmetic, "animation");
        byte[] texture = java.util.Base64.getDecoder().decode(security.a(cosmetic, "texture"));
        float scale = Float.parseFloat(security.a(cosmetic, "scale"));
        Vector3f offset = new Vector3f(Float.parseFloat(security.a(cosmetic, "x")), Float.parseFloat(security.a(cosmetic, "y")), Float.parseFloat(security.a(cosmetic, "z")));
        BakedGeoModel bakedModel = BakedModelFactory.getForNamespace("hydrogen").constructGeoModel(GeometryTree.fromModel((Model) KeyFramesAdapter.GEO_GSON.fromJson(geometry, Model.class)));
        BakedAnimations bakedAnimations = animations == null ? null : (BakedAnimations) KeyFramesAdapter.GEO_GSON.fromJson((JsonElement) JsonParser.parseString(animations).getAsJsonObject().getAsJsonObject("animations"), BakedAnimations.class);
        NativeImage image;
        try {
            image = NativeImage.read(new ByteArrayInputStream(texture));
        } catch (IOException e) {
            return;
        }
        aM_.execute(() -> {
            register(new Cosmetic(name, uuid, category, scale, offset, bakedModel, bakedAnimations, image));
        });
    }

    public Cosmetic register(Cosmetic cosmetic) {
        this.cosmetics.removeIf(existing -> {
            return existing.getType() == cosmetic.getType() && existing.getCategory() == cosmetic.getCategory() && Objects.equals(existing.getUuid(), cosmetic.getUuid());
        });
        this.cosmetics.add(cosmetic);
        return cosmetic;
    }

    public void unregisterCosmetic(UUID uuid, CosmeticsCategory category) {
        this.cosmetics.removeIf(existing -> {
            return existing.getType() == CosmeticsType.COSMETIC && existing.getCategory() == category && Objects.equals(existing.getUuid(), uuid);
        });
    }

    @Override
    public void unSetup() {
        this.cosmetics.clear();
    }
}
