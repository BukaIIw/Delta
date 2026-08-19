package aethereal.core;


import aethereal.api.InternalApi;

@FunctionalInterface
@InternalApi
public interface Supplier<T> extends java.util.function.Supplier<T> {
    @Override
    T get();
}
