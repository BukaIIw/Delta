package aethereal.friend;

import aethereal.core.NativeMethodLookup;
import aethereal.lib.json.JSONArray;
import aethereal.lib.json.JSONObject;
import aethereal.config.ConfigProcessor;
import aethereal.core.EventTarget;
import aethereal.event.BackendEvent;
import aethereal.friend.FriendConstructor;

import aethereal.api.Compile;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FriendProcessor extends ConfigProcessor<FriendConstructor> {
    @Override
    @Compile
    protected List<FriendConstructor> a(String json) throws Exception {
        JSONArray jSONArray = new JSONArray(json);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.a(); i++) {
            arrayList.add(new FriendConstructor(jSONArray.j(i).l("name")));
        }
        return arrayList;
    }

    @Override
    @Compile
    protected String a(List<FriendConstructor> data) throws Exception {
        JSONArray jSONArray = new JSONArray();
        for (FriendConstructor friendConstructor : data) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.c("name", friendConstructor.a());
            jSONArray.a(jSONObject);
        }
        return jSONArray.E(2);
    }

    static {
        NativeMethodLookup.lookup(FriendProcessor.class, 30);
    }

    @Override
    protected String b() {
        return "friends.json";
    }

    @EventTarget
    public void a(BackendEvent event) {
        String minecraft;
        if (event.b() && "friend".equals(event.d().b())) {
            String payload = event.d().c();
            if ("rename".equals(event.d().a().a(payload, "type")) && (minecraft = event.d().a().a(payload, "minecraft")) != null) {
                b(minecraft);
                unSetup();
            }
        }
    }

    public List<FriendConstructor> a() {
        return new ArrayList(this.d);
    }

    public void b(String str) {
        if (!d(str)) {
            this.d.add(new FriendConstructor(str));
        }
    }

    public void c(String str) {
        this.d.removeIf(friend -> friend.a().equalsIgnoreCase(str));
    }

    public boolean d(String name) {
        return this.d.stream().anyMatch(friend -> {
            return friend.a().equalsIgnoreCase(name);
        });
    }

    public void f() {
        this.d.clear();
    }
}
