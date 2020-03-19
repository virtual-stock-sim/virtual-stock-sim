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
        TESLA("TSLA", 2),
        GOOGLE("GOOGL",3),
        FORD("F",4),
        APPLE("AAPL",5);


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
        stocks.put(StockSymbol.AMAZON, new Stock(StockSymbol.AMAZON.getID(), StockSymbol.AMAZON.getSymbol(), new BigDecimal("100.0"), StockSymbol.AMAZON.getID()));
        stocks.put(StockSymbol.TESLA, new Stock(StockSymbol.TESLA.getID(), StockSymbol.TESLA.getSymbol(), new BigDecimal("200.0"), StockSymbol.TESLA.getID()));
        stocks.put(StockSymbol.GOOGLE, new Stock(StockSymbol.GOOGLE.getID(), StockSymbol.GOOGLE.getSymbol(), new BigDecimal("300.0"), StockSymbol.GOOGLE.getID()));
        stocks.put(StockSymbol.FORD, new Stock(StockSymbol.FORD.getID(), StockSymbol.FORD.getSymbol(), new BigDecimal("400.0"), StockSymbol.FORD.getID()));
        stocks.put(StockSymbol.APPLE, new Stock(StockSymbol.APPLE.getID(), StockSymbol.APPLE.getSymbol(), new BigDecimal("500.0"), StockSymbol.APPLE.getID()));
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
        stockDatas.put(StockSymbol.GOOGLE, new StockData(StockSymbol.GOOGLE.getID(), "google test data"));
        stockDatas.put(StockSymbol.FORD, new StockData(StockSymbol.FORD.getID(), "ford test data"));
        stockDatas.put(StockSymbol.APPLE, new StockData(StockSymbol.APPLE.getID(), "apple test data"));

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
