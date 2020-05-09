package io.github.virtualstocksim.util;

@FunctionalInterface
public interface ExceptionalConsumer<T, E extends Throwable>
{
    void apply(T t) throws E;
}
