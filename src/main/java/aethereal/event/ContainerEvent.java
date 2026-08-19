package aethereal.event;

import aethereal.core.Event;
import aethereal.core.IEvent;

import java.util.List;
import lombok.Generated;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class ContainerEvent extends Event implements IEvent {
    public enum Phase {
        PRE,
        POST,
        TITLE
    }

    private final HandledScreen<?> screen;
    private final ScreenHandler handler;
    private final DrawContext context;
    private final List<Slot> slots;
    private final int mouseX;
    private final int mouseY;
    private final Phase phase;
    private Text title;

    @Generated
    public HandledScreen<?> b() {
        return this.screen;
    }

    @Generated
    public ScreenHandler c() {
        return this.handler;
    }

    @Generated
    public DrawContext d() {
        return this.context;
    }

    @Generated
    public List<Slot> e() {
        return this.slots;
    }

    @Generated
    public int f() {
        return this.mouseX;
    }

    @Generated
    public int g() {
        return this.mouseY;
    }

    @Generated
    public Phase h() {
        return this.phase;
    }

    @Generated
    public void a(Text title) {
        this.title = title;
    }

    @Generated
    public Text i() {
        return this.title;
    }

    public ContainerEvent(HandledScreen<?> screen, DrawContext context, int mouseX, int mouseY, Phase type) {
        this.screen = screen;
        this.handler = screen.getScreenHandler();
        this.slots = screen.getScreenHandler().slots;
        this.context = context;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.phase = type;
        this.title = screen.getTitle();
    }

    public ContainerEvent(HandledScreen<?> screen, Text title) {
        this.screen = screen;
        this.handler = screen.getScreenHandler();
        this.slots = screen.getScreenHandler().slots;
        this.context = null;
        this.mouseX = 0;
        this.mouseY = 0;
        this.phase = Phase.TITLE;
        this.title = title;
    }
}
