package hydrogen.ui.screen;

import hydrogen.api.Compile;
import hydrogen.config.ThemeInfo;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Interface;
import hydrogen.core.InterfaceC0020Opcode;
import hydrogen.core.NativeMethodLookup;
import hydrogen.render.AnimationUtil;
import hydrogen.render.ColorUtil;
import hydrogen.render.Draw2DProcessor;
import hydrogen.render.EasingList;
import hydrogen.render.Fonts;
import hydrogen.render.ScaleUtil;
import hydrogen.ui.element.Button;
import hydrogen.ui.shader.GradientUtil;
import hydrogen.ui.widget.EffectMarker;
import hydrogen.util.MathUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends Screen {
    private static final float[] PARALLAX = new float[2];

    private final AnimationUtil openAnim = new AnimationUtil();
    private final Button singleplayer;
    private final Button multiplayer;
    private final Button accounts;
    private final Button settings;
    private final List<Button> buttons;
    private final List<EffectMarker.a> particles = new ArrayList<>();
    private float exitProgress;
    private float exitGrab = -1.0f;
    private float exitX;
    private float exitY;

    static {
        NativeMethodLookup.lookup(MainScreen.class, 16);
    }

    public MainScreen() {
        super(Text.empty());
        if (Interface.aM_.currentScreen instanceof MainScreen) {
            this.openAnim.c(1.0f);
            this.openAnim.d(1.0f);
            this.openAnim.e(1.0f);
        }
        this.singleplayer = new Button(88.0f, 38.0f, "Одиночный Режим", () ->
                Interface.aM_.setScreen(new SelectWorldScreen(null)));
        this.multiplayer = new Button(88.0f, 38.0f, "Сетевая Игра", () ->
                Interface.aM_.setScreen(new MultiplayerScreen(null)));
        this.accounts = new Button(181.0f, 30.0f, "Выбор аккаунта", () ->
                Interface.aM_.setScreen(new AltScreen()));
        this.settings = new Button(79.0f, 19.5f, "Настройки", () ->
                Interface.aM_.setScreen(new OptionsScreen(null, Interface.aM_.options)));
        this.buttons = List.of(this.singleplayer, this.multiplayer, this.accounts, this.settings);
    }

    @Override
    public void close() {
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Compile
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.openAnim.a(Interface.aM_.currentScreen instanceof MainScreen);
        this.openAnim.a(0.0f, 1.0f, 0.15f, EasingList.g, delta);
        float open = Math.min(1.0f, this.openAnim.c() / 0.9f);
        double mx = MathUtil.scale(mouseX, 2);
        double my = MathUtil.scale(mouseY, 2);
        ScaleUtil.a(context, 2);
        int width = Interface.aM_.getWindow().getScaledWidth();
        int height = Interface.aM_.getWindow().getScaledHeight();
        paintBackdrop(context, width, height, (int) mx, (int) my, 1.25f - (EasingList.s.ease(open) * 0.2f));
        HydrogenClient.h().d().i().e().a(context.getMatrices());
        layout(width, height);
        paintTitle(context, width * 0.5f, ((height - this.singleplayer.c()) * 0.5f) - 58.0f, open);
        for (Button button : this.buttons) {
            button.a(context, (int) mx, (int) my, delta, open);
        }
        paintExit(context, open, (int) mx);
        EffectMarker.a(context.getMatrices(), delta, this.particles);
        ScaleUtil.a(context);
    }

    @Compile
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double mx = MathUtil.scale(mouseX, 2);
        double my = MathUtil.scale(mouseY, 2);
        EffectMarker.a(this.particles, (float) mx, (float) my);
        float knob = this.exitX + 1.75f + (this.exitProgress * 59.5f);
        if (MathUtil.a(mx, my, knob, this.exitY + 1.75f, 16.0f, 16.0f)) {
            this.exitGrab = ((float) mx) - knob;
            return true;
        }
        for (Button item : this.buttons) {
            if (item.e() != null && MathUtil.a(mx, my, item.f(), item.g(), item.b(), item.c())) {
                item.e().run();
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Compile
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.exitGrab >= 0.0f) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Compile
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.exitGrab < 0.0f) {
            return super.mouseReleased(mouseX, mouseY, button);
        }
        double mx = MathUtil.scale(mouseX, 2);
        float amount = MathHelper.clamp((((((float) mx) - this.exitGrab) - this.exitX) - 1.75f) / 59.5f, 0.0f, 1.0f);
        this.exitGrab = -1.0f;
        if (amount < 0.95f) {
            return true;
        }
        Interface.aM_.scheduleStop();
        return true;
    }

    public static void a(DrawContext context, int width, int height, int mouseX, int mouseY, float scale) {
        paintBackdrop(context, width, height, mouseX, mouseY, scale);
    }

    public static void paintBackdrop(DrawContext context, int width, int height, int mouseX, int mouseY, float scale) {
        float marginX = width * 0.025f;
        float marginY = height * 0.025f;
        PARALLAX[0] += (MathHelper.clamp((((mouseX / (float) width) - 0.5f) * 2.0f) * marginX, -marginX * 0.9f, marginX * 0.9f) - PARALLAX[0]) * 0.03f;
        PARALLAX[1] += (MathHelper.clamp((((mouseY / (float) height) - 0.5f) * 2.0f) * marginY, -marginY * 0.9f, marginY * 0.9f) - PARALLAX[1]) * 0.03f;
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(width / 2.0f, height / 2.0f, 0.0f);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate((-width) / 2.0f, (-height) / 2.0f, 0.0f);
        HydrogenClient.h().d().i().a(matrices, Identifier.of("hydrogen", "pictures/main.png"),
                (-marginX) + PARALLAX[0], (-marginY) + PARALLAX[1],
                width + (marginX * 2.0f), height + (marginY * 2.0f), 0.0f, -1);
        matrices.pop();
    }

    private void layout(int width, int height) {
        float mainY = (height - this.singleplayer.c()) / 2.0f;
        float mainX = (((width - this.singleplayer.b()) - 5.0f) - this.multiplayer.b()) / 2.0f;
        this.singleplayer.a(mainX, mainY);
        this.multiplayer.a(mainX + this.singleplayer.b() + 5.0f, mainY);
        this.accounts.a((width - this.accounts.b()) / 2.0f, mainY + this.singleplayer.c() + 5.0f);
        this.exitX = (width - 79.0f) / 2.0f;
        this.exitY = height * 0.85f;
        this.settings.a((width - this.settings.b()) / 2.0f, (this.exitY - this.settings.c()) - 5.0f);
    }

    private void paintExit(DrawContext context, float open, int mouseX) {
        float target = this.exitGrab >= 0.0f
                ? MathHelper.clamp((((mouseX - this.exitGrab) - this.exitX) - 1.75f) / 59.5f, 0.0f, 1.0f)
                : 0.0f;
        this.exitProgress += (target - this.exitProgress) * 0.25f;
        float scale = 0.85f + (0.15f * EasingList.s.ease(open));
        Draw2DProcessor draw = HydrogenClient.h().d().i();
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(this.exitX + 39.5f, this.exitY + 9.75f, 0.0f);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate((-this.exitX) - 39.5f, (-this.exitY) - 9.75f, 0.0f);
        float knobX = this.exitX + 1.75f + (this.exitProgress * 59.5f);
        float knobY = this.exitY + 1.75f;
        float centerX = knobX + 8.0f;
        float centerY = knobY + 8.0f;
        draw.b(matrices, this.exitX, this.exitY, 79.0f, 19.5f, 8.0f, ColorUtil.a(11, 11, 13, InterfaceC0020Opcode.bN), open);
        draw.a(matrices, this.exitX, this.exitY, 79.0f, 19.5f, 8.0f, 0.5f, ColorUtil.a(255, 255, 255, (int) (15.0f * open)));
        Fonts.e.c(matrices, "Выйти из игры", this.exitX + 9.0f, this.exitY + 5.75f, 7.0f,
                ColorUtil.a(ColorUtil.a(220, 80, 80, 255), this.exitProgress * open),
                ((knobX - 3.0f) - this.exitX) - 9.0f);
        int knob = ColorUtil.a(ColorUtil.a(255, 255, 255, 13), ColorUtil.a(220, 80, 80, 40), this.exitProgress);
        draw.a(matrices, knobX, knobY, 16.0f, 16.0f, 7.0f, ColorUtil.a(knob, (ColorUtil.b(knob)[3] / 255.0f) * open));
        matrices.push();
        matrices.translate(centerX, centerY, 0.0f);
        matrices.multiply(new Quaternionf().rotateZ((float) Math.toRadians(-90.0f + (180.0f * this.exitProgress))));
        matrices.translate(-centerX, -centerY, 0.0f);
        Fonts.a.a(matrices, "c", (centerX - (Fonts.a.a("c", 8.5f) / 2.0f)) + 1.0f, centerY - 4.5f, 8.5f,
                ColorUtil.a(ColorUtil.a(-1, ColorUtil.a(220, 80, 80, 255), this.exitProgress), open));
        matrices.pop();
        matrices.pop();
    }

    private void paintTitle(DrawContext context, float centerX, float titleY, float open) {
        float titleWidth = Fonts.e.a("HydrogenDLC", 12.0f);
        MatrixStack matrices = context.getMatrices();
        float scale = 0.85f + (0.15f * EasingList.s.ease(open));
        matrices.push();
        matrices.translate(centerX, titleY + 8.0f, 0.0f);
        matrices.scale(scale, scale, 1.0f);
        matrices.translate(-centerX, (-titleY) - 8.0f, 0.0f);
        int primary = HydrogenClient.h().d().o().a(ThemeInfo.PRIMARY).a();
        Fonts.e.a(matrices, (Text) GradientUtil.a("HydrogenDLC", primary, 5.0f, 0.5f),
                centerX - (titleWidth / 2.0f), titleY + 1.5f, 12.0f, 0.0f, open);
        Fonts.e.a(matrices, "1.21.4", centerX - (Fonts.e.a("1.21.4", 12.0f) / 2.0f), titleY + 15.0f, 12.0f,
                ColorUtil.a(255, 255, 255, (int) (160.0f * open)));
        matrices.pop();
    }
}
