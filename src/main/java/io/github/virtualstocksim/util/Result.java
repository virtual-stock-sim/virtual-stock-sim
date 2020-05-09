package io.github.virtualstocksim.util;

public class Result<T, U>
{
    private final T value;
    private final U error;

    protected Result(T value, U error)
    {
        this.value = value;
        this.error = error;
    }

    public static <T, U> Result<T, U> WithValue(T value) { return new Result<>(value, null); }
    public static <T, U> Result<T, U> WithError(U error) { return new Result<>(null, error); }

    public boolean isError() { return error != null; }

    public T getValue() { return value; }
    public U getError() { return error; }

    /**
     * Process the result or error and return a custom type based on Result's value
     * @param callback Callback function to process value or error
     * @param <R> Type returned from callback
     * @param <E> Type of exception thrown from callback (if any)
     * @return Object returned by callback function
     * @throws E Exception thrown from within the callback
     */
    public <R, E extends Throwable> R process(ExceptionalBiConsumer<T, U, R, E> callback) throws E
    {
        return callback.apply(value, error);
    }

    /**
     * Gets the value from within the result or executes the onError callback and returns null
     * @param onError Callback for what to do in the case of an error
     * @param <E> Type of exception thrown from within onError callback (if any)
     * @return Value of result or null
     * @throws E Exception thrown from within onError callback (if any)
     */
    public <E extends Throwable> T getOrNull(ExceptionalConsumer<U, E> onError) throws E
    {
        if(value != null)
            return value;

        if(onError != null)
            onError.apply(error);

        return null;
    }
}
