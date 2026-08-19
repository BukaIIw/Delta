package net.minecraft;

import net.minecraft.util.Identifier;

public final class class_2960 {
    private final Identifier delegate;

    private class_2960(Identifier delegate) {
        this.delegate = delegate;
    }

    public static class_2960 of(Identifier identifier) {
        return identifier == null ? null : new class_2960(identifier);
    }

    public Identifier toIdentifier() {
        return delegate;
    }
}
