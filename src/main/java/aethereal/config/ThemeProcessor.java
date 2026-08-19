package aethereal.config;

import aethereal.core.NativeMethodLookup;
import aethereal.lib.json.JSONObject;
import aethereal.config.ConfigProcessor;
import aethereal.config.ThemeConstructor;
import aethereal.config.ThemeInfo;
import aethereal.config.ThemeType;

import aethereal.api.Compile;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public class ThemeProcessor extends ConfigProcessor<ThemeConstructor> {
    private ThemeType e = ThemeType.DARK;

    @Override
    @Compile
    protected List<ThemeConstructor> a(String json) throws Exception {
        if (json == null || json.isBlank() || json.trim().startsWith("[")) {
            return createDefaultThemes();
        }
        JSONObject jSONObject = new JSONObject(json);
        this.e = ThemeType.valueOf(jSONObject.a("type", ThemeType.DARK.name()));
        List<ThemeConstructor> listE = e();
        if (listE == null) {
            throw new NullPointerException();
        }
        listE.clear();
        ThemeInfo[] themeInfoArrValues = ThemeInfo.values();
        if (themeInfoArrValues == null) {
            throw new NullPointerException();
        }
        for (ThemeInfo themeInfo : themeInfoArrValues) {
            if (themeInfo == null) {
                throw new NullPointerException();
            }
            ThemeConstructor themeConstructorA = themeInfo.a(this.e);
            List<ThemeConstructor> listE2 = e();
            if (themeConstructorA == null) {
                throw new NullPointerException();
            }
            ThemeConstructor themeConstructor = new ThemeConstructor(themeConstructorA.c(), themeConstructorA.d(), themeConstructorA.e(), themeConstructorA.f(), themeConstructorA.g());
            if (listE2 == null) {
                throw new NullPointerException();
            }
            listE2.add(themeConstructor);
        }
        if (!jSONObject.m("primary")) {
            return null;
        }
        ThemeConstructor themeConstructorA2 = a(ThemeInfo.PRIMARY);
        int iH = jSONObject.h("primary");
        if (themeConstructorA2 == null) {
            throw new NullPointerException();
        }
        themeConstructorA2.a(iH);
        return null;
    }

    @Override
    @Compile
    protected String a(List<ThemeConstructor> data) throws Exception {
        JSONObject jSONObject = new JSONObject();
        jSONObject.c("type", this.e.name());
        jSONObject.b("primary", a(ThemeInfo.PRIMARY).a());
        return jSONObject.a(2);
    }

    static {
        NativeMethodLookup.lookup(ThemeProcessor.class, 38);
    }

    @Generated
    public ThemeType a() {
        return this.e;
    }

    @Override
    protected String b() {
        return "theme.json";
    }

    public ThemeConstructor a(ThemeInfo type) {
        return (ThemeConstructor) this.d.stream().filter(constructor -> {
            return constructor.c().equalsIgnoreCase(type.a().c());
        }).findFirst().orElse(type.a(this.e));
    }

    private List<ThemeConstructor> createDefaultThemes() {
        List<ThemeConstructor> themes = new ArrayList<>();
        for (ThemeInfo themeInfo : ThemeInfo.values()) {
            ThemeConstructor defaults = themeInfo.a(this.e);
            themes.add(new ThemeConstructor(defaults.c(), defaults.d(), defaults.e(), defaults.f(), defaults.g()));
        }
        return themes;
    }
}
