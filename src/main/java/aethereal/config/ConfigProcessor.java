package aethereal.config;






import aethereal.core.NativeMethodLookup;
import aethereal.core.Interface;

import static aethereal.core.Interface.aM_;

import aethereal.config.BaseProcessor;

import aethereal.api.Compile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;

public abstract class ConfigProcessor<T> extends BaseProcessor {
    protected final File b = new File(aM_.runDirectory, "configs");
    protected final File c = new File(aM_.runDirectory, "configs\\general");
    protected final List<T> d = new ArrayList();

    protected abstract String b();

    @Override
    @Compile
    public void setup() {
        try {
            List<T> list = this.d;
            if (b() == null) {
                return;
            }
            File fileD = d();
            if (!fileD.exists()) {
                fileD.mkdirs();
            }
            File file = new File(fileD, b());
            String string = file.exists() ? Files.readString(file.toPath()) : "";
            List<T> listA = a(string.length() == 0 ? "[]" : string);
            if (listA != null) {
                list.clear();
                list.addAll(listA);
            }
            if (string.length() == 0) {
                Files.writeString(file.toPath(), a(list), new OpenOption[0]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Compile
    protected abstract List<T> a(String str) throws Exception;

    @Compile
    protected abstract String a(List<T> list) throws Exception;

    static {
        NativeMethodLookup.lookup(ConfigProcessor.class, 23);
    }

    @Generated
    public File c() {
        return this.b;
    }

    @Generated
    public File d() {
        return this.c;
    }

    @Generated
    public List<T> e() {
        return this.d;
    }

    @Override
    public void unSetup() {
        if (b() != null) {
            try {
                File file = new File(d(), b());
                Files.writeString(file.toPath(), a(this.d), new OpenOption[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
