package io.github.virtualstocksim.util;

@FunctionalInterface
public interface ExceptionalBiConsumer<A, B, R, E extends Throwable>
{
    R apply(A a, B b) throws E;
}
