package io.github.virtualstocksim.account;

public class TradeException extends Exception
{
    private TradeExceptionType type;

    public TradeExceptionType getType() { return this.type; }

    TradeException(String message, TradeExceptionType type, Throwable cause)
    {
        super(message, cause);
        this.type = type;
    }

    public TradeException(String message, TradeExceptionType reason)
    {
        super(message);
        this.type = reason;
    }

    TradeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    TradeException(String message)
    {
        super(message);
    }


}
