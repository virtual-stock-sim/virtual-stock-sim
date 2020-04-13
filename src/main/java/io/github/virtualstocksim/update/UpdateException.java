package io.github.virtualstocksim.update;


public class UpdateException extends RuntimeException
{
    UpdateException(String message, Throwable cause)
    {
        super(message, cause);
    }

    UpdateException(String message)
    {
        super(message);
    }
}
