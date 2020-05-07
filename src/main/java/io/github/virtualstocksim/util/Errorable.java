package io.github.virtualstocksim.util;

import java.util.function.Consumer;

public class Errorable<V, E>
{
    private final V value;
    private final E error;

    protected Errorable(V value, E error)
    {
        this.value = value;
        this.error = error;
    }

    public static <V, E> Errorable<V, E> WithValue(V value)
    {
        return new Errorable<>(value, null);
    }

    public static <V, E> Errorable<V, E> WithError(E error)
    {
        return new Errorable<>(null, error);
    }

    public boolean isError() { return error != null; }

    public V getValue() { return value; }
    public E getError() { return error; }

    public void ifValue(Consumer<? super V> consumer)
    {
        if(value != null)
        {
            consumer.accept(value);
        }
    }

    public void ifError(Consumer<? super E> consumer)
    {
        if(error != null)
        {
            consumer.accept(error);
        }
    }
}
