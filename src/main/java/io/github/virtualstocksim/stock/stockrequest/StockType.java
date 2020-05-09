package io.github.virtualstocksim.stock.stockrequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum StockType
{
    STOCK("stock"),
    STOCK_DATA("data"),
    BOTH("both"),
    FOLLOW("follow");

    private static final Logger logger = LoggerFactory.getLogger(StockType.class);
    private final String type;
    StockType(String type)
    {
        this.type = type;
    }
    public String asString() { return type; }
    public static StockType of(String type)
    {
        String lowerType = type.toLowerCase();
        for(StockType t : values())
        {
            if(t.type.equals(lowerType))
            {
                return t;
            }
        }
        throw new StockRequestException("StockRequestType of " + type + " does not exist", StockResponseCode.INVALID_TYPE);
    }
}