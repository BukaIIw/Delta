package hydrogen.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigStore {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path file;

    public ConfigStore(Path file) {
        this.file = file;
    }

    public JsonObject load() throws IOException {
        if (!Files.exists(file)) return new JsonObject();
        String json = Files.readString(file, StandardCharsets.UTF_8);
        return JsonParser.parseString(json).getAsJsonObject();
    }

    public void save(JsonObject root) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.writeString(file, gson.toJson(root), StandardCharsets.UTF_8);
    }
}
