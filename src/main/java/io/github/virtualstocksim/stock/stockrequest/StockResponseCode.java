package io.github.virtualstocksim.stock.stockrequest;

public enum StockResponseCode
{
    PROCESSING(102),
    OK(200),
    BAD_REQUEST(400),
    SYMBOL_NOT_FOUND(404),
    INVALID_SYMBOL(412),
    SERVER_ERROR(500),
    INVALID_TYPE(501);

    private final int code;
    StockResponseCode(int code)
    {
        this.code = code;
    }
    public int asInt() { return code; }
}
