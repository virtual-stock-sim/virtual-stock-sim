package io.github.virtualstocksim.stock.stockrequest;

public class StockRequestException extends IllegalArgumentException
{
    /** @serial  */
    private final StockResponseCode errorCode;
    public StockRequestException(String message, StockResponseCode errorCode, Throwable cause)
    {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public StockRequestException(String message, StockResponseCode errorCode)
    {
        super(message);
        this.errorCode = errorCode;
    }

    public StockResponseCode getErrorCode() { return this.errorCode; }

    @Override
    public String getMessage()
    {
        String msg = super.getMessage();
        return "Error Code: " + this.errorCode.asInt() + " -- " + msg;
    }
}
