package hydrogen.module.misc;

import hydrogen.core.Interface;

import static hydrogen.core.Interface.aM_;
import hydrogen.core.Module;

import hydrogen.core.Category;
import hydrogen.core.ModuleRegister;

import hydrogen.setting.ModeSetting;
import hydrogen.setting.SliderSetting;
import java.io.BufferedInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import lombok.Generated;
import net.minecraft.util.Identifier;

@ModuleRegister(a = "Sounds", b = "Воспроизводит выбранные звуки при определённых игровых событиях", c = Category.Misc)
public class Sounds extends Module {
    private final ModeSetting b = (ModeSetting) new ModeSetting("Звук для воспроизведения", "Тип 1", "Тип 1", "Тип 2", "Тип 3", "Тип 4").a(selected -> {
        a(e(true));
    });
    private final SliderSetting c = new SliderSetting("Громкость воспроизведения", 0.25f, 0.0f, 1.0f, 0.05f);
    private final ExecutorService d = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ClientSound-Thread");
        thread.setDaemon(true);
        return thread;
    });

    @Generated
    public SliderSetting q() {
        return this.c;
    }

    public Sounds() {
        a(this.b, this.c);
    }

    public void d(boolean active) {
        if (m()) {
            a(e(active));
        }
    }

    public void a(String filename) {
        if (filename != null && !filename.isEmpty() && aM_.getResourceManager() != null) {
            this.d.execute(() -> {
                try {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(aM_.getResourceManager().open(Identifier.of("hydrogen", "sounds/" + filename))));
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        gain.setValue(Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), (float) (20.0d * Math.log10(Math.max(this.c.c().floatValue(), 1.0E-4f))))));
                    }
                    clip.start();
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public String e(boolean active) {
        switch (this.b.c()) {
            case "Тип 1":
                return active ? "enable.wav" : "disable.wav";
            case "Тип 2":
                return active ? "enable1.wav" : "disable1.wav";
            case "Тип 3":
                return active ? "enable2.wav" : "disable2.wav";
            case "Тип 4":
                return active ? "enable3.wav" : "disable3.wav";
            default:
                return null;
        }
    }
}
