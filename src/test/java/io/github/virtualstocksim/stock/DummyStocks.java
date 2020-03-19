package io.github.virtualstocksim.stock;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class DummyStocks
{
    public enum StockSymbol
    {
        /*
         * Add new stocks below
         */
        AMAZON("AMZN", 1),
        TESLA("TSLA", 2);

        private final String text;
        private final int id;

        public String getSymbol() { return this.text; }
        private int getID() { return this.id; }

        StockSymbol(String type, int id) {
            this.text = type;
            this.id = id;
        }
    }

    private static final Map<StockSymbol, Stock> STOCKS = createStockMap();
    private static final Map<StockSymbol, StockData> STOCK_DATAS = createStockDataMap();

    private static Map<StockSymbol, Stock> createStockMap()
    {
        Map<StockSymbol, Stock> stocks = new LinkedHashMap<>();

        /*
         * Add new stocks below
         */
        stocks.put(StockSymbol.AMAZON, new Stock(StockSymbol.AMAZON.getID(), StockSymbol.AMAZON.getSymbol(), new BigDecimal("100.0"), 1));
        stocks.put(StockSymbol.TESLA, new Stock(StockSymbol.TESLA.getID(), StockSymbol.TESLA.getSymbol(), new BigDecimal("200.0"), 2));

        return stocks;
    }

    private static Map<StockSymbol, StockData> createStockDataMap()
    {
        Map<StockSymbol, StockData> stockDatas =  new LinkedHashMap<>();

        /*
         * Add new stocks below
         */
        stockDatas.put(StockSymbol.AMAZON, new StockData(StockSymbol.AMAZON.getID(), "amazon test data"));
        stockDatas.put(StockSymbol.TESLA, new StockData(StockSymbol.TESLA.getID(), "tesla test data"));


        return stockDatas;
    }

    public static Stock GetDummyStock(StockSymbol s)
    {
        return STOCKS.get(s);
    }

    public static Map<StockSymbol, Stock> GetDummyStocks()
    {
        return STOCKS;
    }

    public static StockData GetDummyStockData(StockSymbol s)
    {
        return STOCK_DATAS.get(s);
    }

    public static Map<StockSymbol, StockData> GetDummyStockDatas()
    {
        return STOCK_DATAS;
    }

}
