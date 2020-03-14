package io.github.virtualstocksim.util;

import java.util.function.Supplier;

// Container for an object to be lazily evaluated
public class Lazy<T>
{
    private Supplier<T> supplier;
    private T obj = null;

    private Lazy(Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    // Has the object been evaluated
    public boolean hasEvaluated()
    {
        return obj != null;
    }

    // Evaluate the object and return
        // If already evaluated, then return stored instance
    public synchronized T get()
    {
        if(obj == null)
        {
            obj = supplier.get();
        }
        return obj;
    }

    // Lazy evaluation for lambda function
    public static <T> Lazy<T> lazily(Supplier<T> toBeEval)
    {
        return new Lazy<>(toBeEval);
    }
}