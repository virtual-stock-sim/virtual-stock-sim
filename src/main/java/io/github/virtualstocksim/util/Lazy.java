package io.github.virtualstocksim.util;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;

import java.util.function.Supplier;

// Container for an object to be lazily evaluated
public class Lazy<T> extends LazyInitializer<T>
{
    private Supplier<T> supplier;

    public Lazy(Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    @Override
    protected T initialize() throws ConcurrentException
    {
        return supplier.get();
    }
}