package hydrogen.ui.screen;

import hydrogen.api.Compile;
import hydrogen.config.ThemeInfo;
import hydrogen.core.HydrogenClient;
import hydrogen.core.Interface;
import hydrogen.core.InterfaceC0020Opcode;
import hydrogen.core.NativeMethodLookup;
import hydrogen.core.Processor_2;
import hydrogen.network.AccountConstructor;
import hydrogen.render.AnimationUtil;
import hydrogen.render.ColorUtil;
import hydrogen.render.Draw2DProcessor;
import hydrogen.render.EasingList;
import hydrogen.render.Fonts;
import hydrogen.render.ScaleUtil;
import hydrogen.render.ScissorUtil;
import hydrogen.ui.element.TextField;
import hydrogen.ui.shader.BlurShader;
import hydrogen.ui.shader.GradientUtil;
import hydrogen.util.MathUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AltScreen extends Screen {
    private final AnimationUtil openAnim = new AnimationUtil();
    private final AnimationUtil scroll = new AnimationUtil();
    private final TextField nameField = new TextField(TextField.a.ALT_MANAGER);
    private final List<a> rows = new ArrayList<>();
    a hovered;
    private a dragging;
    private AccountConstructor lastSelected;
    private float dragOffset;
    private boolean didDrag;

    static {
        NativeMethodLookup.lookup(AltScreen.class, 15);
    }

    public AltScreen() {
        super(Text.empty());
        this.nameField.a("Никнейм");
        accounts().forEach(account -> this.rows.add(new a(account)));
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Compile
    @Override
    public void render(DrawContext context, int mx, int my, float delta) {
        super.render(context, mx, my, delta);
        MinecraftClient client = Interface.aM_;
        if (client == null) {
            return;
        }
        this.openAnim.a(client.currentScreen instanceof AltScreen);
        this.openAnim.a(0.0f, 1.0f, 0.15f, EasingList.g, delta);
        float open = Math.min(1.0f, this.openAnim.c() / 0.9f);
        double sx = MathUtil.scale(mx, 2);
        double sy = MathUtil.scale(my, 2);
        ScaleUtil.a(context, 2);
        Window window = client.getWindow();
        if (window == null) {
            ScaleUtil.a(context);
            return;
        }
        int width = window.getScaledWidth();
        int height = window.getScaledHeight();
        MainScreen.a(context, width, height, (int) sx, (int) sy, (EasingList.s.ease(open) * 0.2f) + 1.05f);
        Processor_2 processor = HydrogenClient.h() != null ? HydrogenClient.h().d() : null;
        Draw2DProcessor draw = processor != null ? processor.i() : null;
        BlurShader blur = draw != null ? draw.e() : null;
        if (blur == null) {
            ScaleUtil.a(context);
            return;
        }
        blur.a(context.getMatrices());
        float ease = EasingList.s.ease(open);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(width * 0.5f, height * 0.5f, 0.0f);
        float scale = (ease * 0.15f) + 0.85f;
        matrices.scale(scale, scale, 1.0f);
        matrices.translate((-width) * 0.5f, (-height) * 0.5f, 0.0f);
        float px = (width - 190.0f) * 0.5f;
        float py = (height - 250.0f) * 0.5f;
        paintChrome(matrices, width, px, py, open);
        paintList(matrices, width, height, scale, px, py, (int) sx, (int) sy, open);
        paintFooter(context, px, py, (int) sx, (int) sy, delta, open);
        matrices.pop();
        ScaleUtil.a(context);
    }

    @Compile
    @Override
    public boolean mouseClicked(double rawX, double rawY, int button) {
        double mx = MathUtil.scale(rawX, 2);
        double my = MathUtil.scale(rawY, 2);
        this.nameField.a(mx, my, button);
        float fieldX = this.nameField.d().getX();
        float fieldY = this.nameField.d().getY();
        float fieldW = this.nameField.e().getX();
        float right = fieldX + fieldW;
        if (MathUtil.a(mx, my, 5.0f + right, fieldY, 18.0f, 18.0f)) {
            addAccount(this.nameField.g().toString());
            return true;
        }
        if (MathUtil.a(mx, my, 28.0f + right, fieldY, 150.0f - fieldW, 18.0f)) {
            addAccount(randomName());
            return true;
        }
        if (MathUtil.a(mx, my, fieldX + 7.0f, fieldY + 33.0f, 160.0f, 20.0f)) {
            this.rows.forEach(row -> row.g = true);
            accounts().clear();
            return true;
        }
        if (this.hovered == null) {
            return super.mouseClicked(rawX, rawY, button);
        }
        if (button == 1) {
            removeRow(this.hovered);
            return true;
        }
        this.dragging = this.hovered;
        this.dragOffset = ((float) my) - this.hovered.e;
        this.didDrag = false;
        return true;
    }

    @Compile
    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        this.nameField.b(MathUtil.scale(mx, 2), MathUtil.scale(my, 2), button);
        if (this.dragging == null) {
            return super.mouseDragged(mx, my, button, dx, dy);
        }
        double next = MathUtil.scale(my, 2) - this.dragOffset;
        if (Math.abs(next - this.dragging.e) <= 8.0d) {
            return true;
        }
        this.didDrag = true;
        return true;
    }

    @Compile
    @Override
    public boolean mouseReleased(double rawX, double rawY, int button) {
        if (this.dragging == null) {
            return super.mouseReleased(rawX, rawY, button);
        }
        if (!this.didDrag) {
            if (this.dragging.f) {
                AccountConstructor account = this.dragging.b;
                account.b(!account.d());
            } else {
                select(this.dragging.b);
            }
            this.dragging = null;
            return true;
        }
        commitOrder();
        this.dragging = null;
        return true;
    }

    @Compile
    @Override
    public boolean mouseScrolled(double mx, double my, double dx, double dy) {
        this.scroll.a(((float) dy) * 29.0f);
        return true;
    }

    @Compile
    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.nameField.j()) {
            return super.charTyped(chr, modifiers);
        }
        this.nameField.a(chr, modifiers);
        return true;
    }

    @Compile
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((modifiers & 2) != 0 && keyCode == 86) {
            pasteClipboard();
            return true;
        }
        if (!this.nameField.j()) {
            if (keyCode != 256) {
                return super.keyPressed(keyCode, scanCode, modifiers);
            }
            Interface.aM_.setScreen(new MainScreen());
            return true;
        }
        if (keyCode == 257) {
            addAccount(this.nameField.g().toString());
            return true;
        }
        this.nameField.a(keyCode, scanCode, modifiers);
        return true;
    }

    private void paintChrome(MatrixStack matrices, int w, float px, float py, float open) {
        Draw2DProcessor draw = HydrogenClient.h().d().i();
        AccountConstructor selected = HydrogenClient.h().d().h().a();
        Fonts.e.a(matrices, (Text) GradientUtil.a("Менеджер Аккаунтов", accent(open), 5.0f, 0.5f),
                (w - Fonts.e.a("Менеджер Аккаунтов", 11.0f)) / 2.0f, py - 28.0f, 11.0f, 0.0f, open);
        String info = (selected != null ? selected.b() : "Не выбран") + "  |  " + accounts().size() + " аккаунтов";
        Fonts.b.a(matrices, info, (w - Fonts.b.a(info, 7.0f)) / 2.0f, py - 13.5f, 7.0f, ColorUtil.a(255, 255, 255, (int) (255.0f * open)));
        draw.b(matrices, px, py, 190.0f, 250.0f, 8.0f, ColorUtil.a(11, 11, 13, InterfaceC0020Opcode.bN), open);
        draw.a(matrices, px, py, 190.0f, 250.0f, 8.0f, 0.5f, ColorUtil.a(255, 255, 255, (int) (15.0f * open)));
    }

    private void paintList(MatrixStack matrices, int w, int h, float scale, float px, float py, int mx, int my, float open) {
        Draw2DProcessor draw = HydrogenClient.h().d().i();
        AccountConstructor selected = HydrogenClient.h().d().h().a();
        int accent = accent(open);
        float listTop = py + 10.0f;
        float listBottom = py + 218.0f;
        float listHeight = listBottom - listTop;
        float overflow = Math.min(0.0f, listHeight - (this.rows.size() * 29.0f));
        float offset = this.scroll.a(overflow, 0.0f, 0.5f);
        boolean drag = this.dragging != null && this.didDrag;
        float baseY = listTop + offset;
        float dragSlot = (my - this.dragOffset) - baseY;
        List<a> visual = this.rows.stream()
                .sorted(Comparator.comparing(row -> !row.b.d()))
                .collect(Collectors.toCollection(ArrayList::new));
        if (drag) {
            reorder(visual, dragSlot);
        }
        this.hovered = null;
        float selectedSlot = -1.0f;
        int index = 0;
        ScissorUtil.a(matrices, (w / 2.0f) + ((px - (w / 2.0f)) * scale),
                (h / 2.0f) + (((listTop - 2.0f) - (h / 2.0f)) * scale),
                190.0f * scale, (listHeight + 1.0f) * scale);
        for (a account : visual) {
            float targetSlot = account.g ? account.d : index * 29.0f;
            if (account != this.dragging || !drag) {
                account.a(matrices, draw, accent, px + 1.0f, baseY, targetSlot, listTop, listBottom, mx, my, open);
            }
            if (account.b == selected) {
                selectedSlot = targetSlot;
            }
            if (!account.g) {
                index++;
            }
        }
        if (drag) {
            this.dragging.d = dragSlot;
            this.dragging.a(matrices, draw, accent, px + 1.0f, baseY, dragSlot, listTop, listBottom, mx, my, open);
        }
        ScissorUtil.a(matrices);
        this.rows.removeIf(a::a);
        if (selected != this.lastSelected) {
            this.lastSelected = selected;
            if (selectedSlot >= 0.0f) {
                float top = selectedSlot + offset;
                float desired = top < 0.0f ? -selectedSlot : top + 29.0f > listHeight ? (listHeight - 29.0f) - selectedSlot : offset;
                this.scroll.a(MathUtil.b(desired, overflow, 0.0f) - offset);
            }
        }
        float content = Math.max(listHeight, this.rows.size() * 29.0f);
        float thumb = (listHeight * listHeight) / content;
        draw.a(matrices, px + 182.5f, listTop, 1.5f, listHeight, 0.75f, ColorUtil.a(255, 255, 255, (int) (20.0f * open)));
        draw.a(matrices, px + 182.5f, listTop - ((offset / Math.max(1.0f, content - listHeight)) * (listHeight - thumb)), 1.5f, thumb, 0.75f, accent);
    }

    private void paintFooter(DrawContext context, float px, float py, int mx, int my, float delta, float open) {
        MatrixStack matrices = context.getMatrices();
        Draw2DProcessor draw = HydrogenClient.h().d().i();
        int white = ColorUtil.a(222, 222, 222, (int) (222.0f * open));
        float fieldY = py + 224.0f;
        float randomWidth = Fonts.a.a("H", 8.0f) + 3.0f + Fonts.d.a("Случайный", 7.0f) + 14.0f;
        float fieldWidth = 146.0f - randomWidth;
        float right = px + 11.0f + fieldWidth;
        this.nameField.a(new Vector2f(px + 7.0f, fieldY));
        this.nameField.b(new Vector2f(fieldWidth, 18.0f));
        this.nameField.a(context, mx, my, delta, open);
        paintChip(matrices, draw, right + 0.5f, fieldY, 18.0f, 18.0f, new Vector4f(1.0f, 5.0f, 1.0f, 5.0f),
                null, "m", ColorUtil.a(255, 255, 255, 10), white, open, mx, my);
        paintChip(matrices, draw, right + 27.0f, fieldY, randomWidth, 18.0f, new Vector4f(6.0f, 6.0f, 6.0f, 6.0f),
                "Случайный", "", ColorUtil.a(255, 255, 255, 10), white, open, mx, my);
        paintChip(matrices, draw, px + 15.0f, py + 257.0f, 160.0f, 20.0f, new Vector4f(6.0f, 6.0f, 6.0f, 6.0f),
                "Удалить все аккаунты", null, ColorUtil.a(220, 80, 80, 20), ColorUtil.a(220, 80, 80, (int) (255.0f * open)), open, mx, my);
    }

    private int accent(float open) {
        int rgba = HydrogenClient.h().d().o().a(ThemeInfo.PRIMARY).a();
        return (rgba & 16777215) | (((int) (((rgba >>> 24) & 255) * open)) << 24);
    }

    private void paintChip(MatrixStack matrices, Draw2DProcessor draw, float x, float y, float width, float height,
                           Vector4f radius, String text, String icon, int background, int content, float open, int mx, int my) {
        boolean hover = MathUtil.a(mx, my, x, y, width, height);
        draw.a(matrices, x, y, width, height, radius, background);
        draw.a(matrices, x, y, width, height, radius, 0.25f, ColorUtil.a(255, 255, 255, (int) ((hover ? 25 : 12) * open)));
        float iconWidth = icon != null ? Fonts.a.a(icon, 8.0f) + (text != null ? 3.0f : 0.0f) : 0.0f;
        float startX = x + (((width - iconWidth) - (text != null ? Fonts.d.a(text, 7.0f) : 0.0f)) / 2.0f);
        if (icon != null) {
            Fonts.a.a(matrices, icon, startX, y + ((height - 10.0f) / 2.0f), 10.0f, content);
        }
        if (text != null) {
            Fonts.d.a(matrices, text, startX + 2.0f, y + ((height - 8.5f) / 2.0f), 7.0f, content);
        }
    }

    private List<AccountConstructor> accounts() {
        return HydrogenClient.h().d().h().e();
    }

    private void select(AccountConstructor account) {
        accounts().forEach(other -> other.a(other == account));
    }

    private String randomName() {
        StringBuilder name = new StringBuilder();
        int syllables = 2 + ((int) (Math.random() * 3.0d));
        for (int i = 0; i < syllables; i++) {
            char c = "bcdfghjklmnpqrstvwz".charAt((int) (Math.random() * "bcdfghjklmnpqrstvwz".length()));
            name.append(c);
            if (Math.random() < 0.12d) {
                name.append(c);
            }
            char v = "aeiouy".charAt((int) (Math.random() * "aeiouy".length()));
            name.append(v);
            if (Math.random() < 0.1d) {
                name.append(v);
            }
        }
        if (Math.random() < 0.3d) {
            name.setCharAt(0, Character.toUpperCase(name.charAt(0)));
        }
        if (Math.random() < 0.15d) {
            name.append('_');
        }
        if (Math.random() < 0.25d) {
            int digits = 1 + ((int) (Math.random() * 3.0d));
            for (int i = 0; i < digits; i++) {
                name.append((char) (48 + ((int) (Math.random() * 10.0d))));
            }
        }
        if (name.length() < 5) {
            return randomName();
        }
        return name.length() > 16 ? name.substring(0, 16) : name.toString();
    }

    private void addAccount(String raw) {
        String name = raw.trim();
        if (name.isEmpty() || accounts().stream().anyMatch(other -> other.b().equalsIgnoreCase(name))) {
            return;
        }
        AccountConstructor account = new AccountConstructor(name);
        select(account);
        accounts().add(account);
        this.rows.add(new a(account));
        this.nameField.a();
    }

    private void removeRow(a account) {
        account.g = true;
        boolean wasSelected = account.b.c();
        accounts().remove(account.b);
        if (wasSelected) {
            select(accounts().stream().findFirst().orElse(null));
        }
    }

    private void reorder(List<a> visual, float draggedY) {
        int from = visual.indexOf(this.dragging);
        int favorites = (int) visual.stream().filter(account -> account.b.d()).count();
        int lo = this.dragging.b.d() ? 0 : favorites;
        int hi = this.dragging.b.d() ? favorites - 1 : visual.size() - 1;
        int to = Math.max(lo, Math.min(hi, Math.round(draggedY / 29.0f)));
        if (from < 0 || from == to) {
            return;
        }
        visual.remove(from);
        visual.add(to, this.dragging);
        this.rows.clear();
        this.rows.addAll(visual);
    }

    private void commitOrder() {
        List<AccountConstructor> list = accounts();
        Stream<AccountConstructor> map = this.rows.stream().map(account -> account.b);
        Objects.requireNonNull(list);
        List<AccountConstructor> ordered = map.filter(list::contains).toList();
        list.clear();
        list.addAll(ordered);
    }

    private void pasteClipboard() {
        String clip = GLFW.glfwGetClipboardString(Interface.aM_.getWindow().getHandle());
        if (clip == null) {
            return;
        }
        String name = clip.replaceAll("[^a-zA-Z0-9_]", "");
        addAccount(name.substring(0, Math.min(16, name.length())));
    }

    class a {
        final AccountConstructor b;
        private final float[] c = new float[4];
        float d = Float.NaN;
        float e;
        boolean f;
        boolean g;

        a(AccountConstructor data) {
            this.b = data;
        }

        boolean a() {
            return this.g && this.c[3] < 0.01f;
        }

        void a(MatrixStack matrices, Draw2DProcessor draw, int accent, float px, float baseY, float targetSlot,
               float listTop, float listBottom, int mx, int my, float open) {
            this.d = Float.isNaN(this.d) ? targetSlot : MathUtil.c(this.d, targetSlot, 1.4f);
            this.e = baseY + this.d;
            boolean over = !this.g && my > listTop && my < listBottom && MathUtil.a((double) mx, (double) my, px + 7.0f, this.e, 168.0f, 25.0f);
            this.f = over && mx > px + 156.0f;
            if (over) {
                AltScreen.this.hovered = this;
            }
            float[] target = {over ? 1.0f : 0.0f, this.b.c() ? 1.0f : 0.0f, this.b.d() ? 1.0f : 0.0f, this.g ? 0.0f : 1.0f};
            for (int i = 0; i < 4; i++) {
                this.c[i] = MathUtil.c(this.c[i], target[i], 1.5f);
            }
            float hover = this.c[0];
            float select = this.c[1];
            float fav = this.c[2];
            float a = open * this.c[3];
            if (this.e + 25.0f < listTop - 2.0f || this.e > listBottom + 2.0f) {
                return;
            }
            if (select > 0.01f) {
                draw.a(matrices, px + 7.0f, this.e, 168.0f, 25.0f, 6.0f, ColorUtil.a(accent, 0.1f * select * a));
            } else if (hover > 0.01f) {
                draw.a(matrices, px + 7.0f, this.e, 168.0f, 25.0f, 6.0f, ColorUtil.a(255, 255, 255, (int) (6.0f * hover * a)));
            }
            draw.a(matrices, px + 7.0f, this.e, 168.0f, 25.0f, 6.0f, 0.5f,
                    ColorUtil.a(ColorUtil.a(255, 255, 255, (int) (8.0f * a)), ColorUtil.a(255, 205, 60, (int) (30.0f * a)), fav));
            draw.a(matrices, this.b.a(), null, px + 11.5f, this.e + 4.0f, 16.5f, 16.5f, 3.0f, a);
            Fonts.d.a(matrices, this.b.b(), px + 34.0f, this.e + 7.5f, 8.0f, ColorUtil.a(255, 255, 255, (int) (255.0f * a)));
            if (fav > 0.01f || hover > 0.01f) {
                Fonts.a.a(matrices, "\\", px + 159.0f, this.e + 8.0f, 9.0f,
                        ColorUtil.a(ColorUtil.a(255, 255, 255, (int) ((this.f ? InterfaceC0020Opcode.bW : 45) * hover * a)),
                                ColorUtil.a(255, 205, 60, (int) (255.0f * a)), fav));
            }
        }
    }
}
