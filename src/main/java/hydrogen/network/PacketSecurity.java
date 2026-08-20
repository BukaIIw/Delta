package hydrogen.network;


import hydrogen.core.NativeMethodLookup;
import hydrogen.api.Compile;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Compile
public class PacketSecurity {
    private byte[] a;
    private Gson b;

    public String a(String payload, String key) {
        Object objFromJson;
        Gson gson = this.b;
        if (payload == null || payload.isBlank() || (objFromJson = gson.fromJson(payload, JsonObject.class)) == null) {
            return null;
        }
        if (!(objFromJson instanceof JsonObject)) {
            throw new ClassCastException();
        }
        JsonObject jsonObject = (JsonObject) objFromJson;
        if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
            return jsonObject.get(key).getAsString();
        }
        return null;
    }

    public JsonElement b(String payload, String key) {
        Gson gson = this.b;
        if (payload == null || payload.isBlank()) {
            return null;
        }
        if (gson == null) {
            throw new NullPointerException();
        }
        Object objFromJson = gson.fromJson(payload, JsonObject.class);
        if (objFromJson == null) {
            return null;
        }
        if (!(objFromJson instanceof JsonObject)) {
            throw new ClassCastException();
        }
        JsonObject jsonObject = (JsonObject) objFromJson;
        if (!jsonObject.has(key)) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            throw new NullPointerException();
        }
        if (jsonElement.isJsonNull()) {
            return null;
        }
        return jsonObject.get(key);
    }

    public List<String> c(String payload, String key) {
        Gson gson = this.b;
        if (payload == null || payload.isBlank()) {
            return null;
        }
        if (gson == null) {
            throw new NullPointerException();
        }
        Object objFromJson = gson.fromJson(payload, JsonObject.class);
        if (objFromJson == null) {
            return null;
        }
        if (!(objFromJson instanceof JsonObject)) {
            throw new ClassCastException();
        }
        JsonObject jsonObject = (JsonObject) objFromJson;
        if (!jsonObject.has(key)) {
            return null;
        }
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null) {
            throw new NullPointerException();
        }
        if (!jsonElement.isJsonArray()) {
            return null;
        }
        JsonArray asJsonArray = jsonObject.getAsJsonArray(key);
        if (asJsonArray == null) {
            throw new NullPointerException();
        }
        Stream stream = StreamSupport.stream(asJsonArray.spliterator(), false);
        Function function = new Function() {
            @Override
            public Object apply(Object obj) {
                return PacketSecurity.a((JsonElement) obj);
            }
        };
        if (stream == null) {
            throw new NullPointerException();
        }
        Stream map = stream.map(function);
        java.util.stream.Collector list = Collectors.toList();
        if (map == null) {
            throw new NullPointerException();
        }
        Object objCollect = map.collect(list);
        if (objCollect == null) {
            return null;
        }
        if (objCollect instanceof List) {
            return (List) objCollect;
        }
        throw new ClassCastException();
    }

    public String a(Object... keyValues) {
        JsonElement jsonPrimitive;
        Gson gson = this.b;
        if (keyValues == null || (keyValues.length & 1) != 0) {
            throw new IllegalArgumentException("keyValues must contain even count of elements");
        }
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < keyValues.length; i += 2) {
            String strValueOf = String.valueOf(keyValues[i]);
            Object obj = keyValues[i + 1];
            if (obj instanceof JsonNode) {
                jsonPrimitive = JsonParser.parseString(((JsonNode) obj).toString());
            } else if (obj instanceof Number) {
                jsonPrimitive = new JsonPrimitive((Number) obj);
            } else {
                jsonPrimitive = obj instanceof Boolean ? new JsonPrimitive((Boolean) obj) : gson.toJsonTree(obj);
            }
            jsonObject.add(strValueOf, jsonPrimitive);
        }
        return gson.toJson((JsonElement) jsonObject);
    }

    public String d(String packetId, String payload) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", packetId);
        jsonObject.add("payload", this.b.toJsonTree(b(payload)));
        return this.b.toJson((JsonElement) jsonObject);
    }

    public Optional<a> a(String message) {
        try {
            JsonObject jsonObject = (JsonObject) this.b.fromJson(message, JsonObject.class);
            return (!jsonObject.has("id") || !jsonObject.has("payload") || jsonObject.get("id").isJsonNull() || jsonObject.get("id").getAsString().isBlank()) ? Optional.empty() : Optional.of(new a(jsonObject.get("id").isJsonNull() ? null : jsonObject.get("id").getAsString(), c(jsonObject.get("payload").getAsString())));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private JsonElement a(Object value) {
        if (value instanceof JsonNode) {
            return JsonParser.parseString(((JsonNode) value).toString());
        }
        if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        }
        return value instanceof Boolean ? new JsonPrimitive((Boolean) value) : this.b.toJsonTree(value);
    }

    public String b(String plain) {
        try {
            byte[] bArr = this.a;
            byte[] bArr2 = new byte[12];
            new SecureRandom().nextBytes(bArr2);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(1, new SecretKeySpec(bArr, "AES"), new GCMParameterSpec(128, bArr2));
            byte[] bArrDoFinal = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            byte[] bArr3 = new byte[bArr2.length + bArrDoFinal.length];
            System.arraycopy(bArr2, 0, bArr3, 0, bArr2.length);
            System.arraycopy(bArrDoFinal, 0, bArr3, bArr2.length, bArrDoFinal.length);
            return java.util.Base64.getEncoder().encodeToString(bArr3);
        } catch (Exception e) {
            return null;
        }
    }

    public String c(String encoded) {
        try {
            byte[] bArr = this.a;
            byte[] bArrDecode = java.util.Base64.getDecoder().decode(encoded);
            byte[] bArrCopyOfRange = Arrays.copyOfRange(bArrDecode, 0, 12);
            byte[] bArrCopyOfRange2 = Arrays.copyOfRange(bArrDecode, 12, bArrDecode.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(2, new SecretKeySpec(bArr, "AES"), new GCMParameterSpec(128, bArrCopyOfRange));
            return new String(cipher.doFinal(bArrCopyOfRange2), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public static String a(JsonElement e) {
        if (e == null) {
            throw new NullPointerException();
        }
        if (e.isJsonNull()) {
            return null;
        }
        return e.getAsString();
    }

    private void jc$init$0() {
        this.a = HexFormat.of().parseHex("9f3b7c1e6a8d4205b1e7c9a2f4d60837");
        this.b = new Gson();
    }

    static {
        NativeMethodLookup.lookup(PacketSecurity.class, 3);
    }

    public PacketSecurity() {
        jc$init$0();
    }

    public static final class a {
        private final String a;
        private final String b;

        public a(String id, String payload) {
            this.a = id;
            this.b = payload;
        }
public String a() {
            return this.a;
        }

        public String b() {
            return this.b;
        }
    }
}
