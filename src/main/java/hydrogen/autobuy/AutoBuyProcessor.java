package hydrogen.autobuy;

import hydrogen.lib.javassist.TokenId;
import hydrogen.core.NativeMethodLookup;
import hydrogen.lib.json.JSONArray;
import hydrogen.lib.json.JSONObject;
import hydrogen.core.InterfaceC0020Opcode;

import hydrogen.config.AttributeCondition;
import hydrogen.config.ConfigProcessor;
import hydrogen.config.PotionCondition;

import hydrogen.render.AnimationUtil;
import hydrogen.config.AttributeProcessor;
import hydrogen.config.DescriptionProcessor;
import hydrogen.config.EnchantmentProcessor;
import hydrogen.config.NBTProcessor;
import hydrogen.config.PotionProcessor;
import hydrogen.api.Compile;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;

public class AutoBuyProcessor extends ConfigProcessor<AutoBuyEntry> {
    @Override
    @Compile
    protected List<AutoBuyEntry> a(String str) {
        if (this.d.isEmpty()) {
            this.d.addAll(Arrays.asList(AutoBuyEntry.values()));
        }
        JSONArray jSONArray = new JSONArray(str);
        for (int i = 0; i < jSONArray.a(); i++) {
            JSONObject jSONObjectJ = jSONArray.j(i);
            String strL = jSONObjectJ.l("name");
            for (Object obj : this.d) {
                if (!(obj instanceof AutoBuyEntry)) {
                    throw new ClassCastException();
                }
                AutoBuyEntry aVar = (AutoBuyEntry) obj;
                if (aVar.b().equals(strL)) {
                    if (jSONObjectJ.m("status")) {
                        aVar.a(jSONObjectJ.b("status"));
                    }
                    if (jSONObjectJ.m("price")) {
                        aVar.a(jSONObjectJ.e("price"));
                    }
                }
            }
        }
        return new ArrayList(this.d);
    }

    @Override
    @Compile
    protected String a(List<AutoBuyEntry> data) {
        JSONArray jSONArray = new JSONArray();
        for (AutoBuyEntry aVar : data) {
            JSONObject jSONObject = new JSONObject();
            if (!(aVar instanceof AutoBuyEntry)) {
                throw new ClassCastException();
            }
            AutoBuyEntry aVar2 = aVar;
            jSONObject.c("name", aVar2.b());
            jSONObject.b("status", aVar2.l());
            jSONObject.b("price", aVar2.k());
            jSONArray.a(jSONObject);
        }
        return jSONArray.E(2);
    }

    static {
        NativeMethodLookup.lookup(AutoBuyProcessor.class, 20);
    }

    public AutoBuyProcessor() {
        this.d.addAll(Arrays.asList(AutoBuyEntry.values()));
    }

    @Override
    protected String b() {
        return "autobuy.json";
    }
}
