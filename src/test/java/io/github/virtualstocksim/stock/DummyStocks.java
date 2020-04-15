package io.github.virtualstocksim.stock;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
        BDX("BDX", 5);


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
        stocks.put(StockSymbol.AMAZON, new Stock(StockSymbol.AMAZON.getID(), StockSymbol.AMAZON.getSymbol(), new BigDecimal("100.0"), new BigDecimal("0.0"), 10000, 0, StockSymbol.AMAZON.getID(), Timestamp.valueOf("2020-01-21 21:18:07.233")));
        stocks.put(StockSymbol.TESLA, new Stock(StockSymbol.TESLA.getID(), StockSymbol.TESLA.getSymbol(), new BigDecimal("200.0"), new BigDecimal("100.0"), 20000, 10000, StockSymbol.TESLA.getID(), Timestamp.valueOf("2020-02-21 21:18:07.233")));
        stocks.put(StockSymbol.GOOGLE, new Stock(StockSymbol.GOOGLE.getID(), StockSymbol.GOOGLE.getSymbol(), new BigDecimal("300.0"), new BigDecimal("200.0"), 30000, 20000, StockSymbol.GOOGLE.getID(), Timestamp.valueOf("2020-03-21 21:18:07.233")));
        stocks.put(StockSymbol.FORD, new Stock(StockSymbol.FORD.getID(), StockSymbol.FORD.getSymbol(), new BigDecimal("400.0"), new BigDecimal("300.0"), 40000, 30000, StockSymbol.FORD.getID(), Timestamp.valueOf("2020-04-21 21:18:07.233")));
        stocks.put(StockSymbol.BDX, new Stock(StockSymbol.BDX.getID(), StockSymbol.BDX.getSymbol(), new BigDecimal("500.0"), new BigDecimal("400.0"), 50000, 40000, StockSymbol.BDX.getID(), Timestamp.valueOf("2020-05-21 21:18:07.233")));
        return stocks;
    }

    private static Map<StockSymbol, StockData> createStockDataMap()
    {
        Map<StockSymbol, StockData> stockDatas =  new LinkedHashMap<>();

        /*
         * Add new stocks below
         */
        stockDatas.put(StockSymbol.AMAZON, new StockData(StockSymbol.AMAZON.getID(), "{\"symbol\":\"AMZN\",\"description\":\"amazon description\",\"history\":[{\"date\":\"2010-06-01\",\"open\":\"19.000000\",\"high\":\"30.420000\",\"low\":\"17.540001\",\"close\":\"23.830000\",\"adjclose\":\"23.830000\",\"volume\":\"35953400\"},{\"date\":\"2010-07-01\",\"open\":\"25.000000\",\"high\":\"25.920000\",\"low\":\"14.980000\",\"close\":\"19.940001\",\"adjclose\":\"19.940001\",\"volume\":\"64575800\"}]}",
                                                         Timestamp.valueOf("2020-01-21 21:18:07.233")));
        stockDatas.put(StockSymbol.TESLA, new StockData(StockSymbol.TESLA.getID(), "{\"symbol\":\"TSLA\",\"description\":\"tesla description\",\"history\":[{\"date\":\"2010-06-01\",\"open\":\"19.000000\",\"high\":\"30.420000\",\"low\":\"17.540001\",\"close\":\"23.830000\",\"adjclose\":\"23.830000\",\"volume\":\"35953400\"},{\"date\":\"2010-07-01\",\"open\":\"25.000000\",\"high\":\"25.920000\",\"low\":\"14.980000\",\"close\":\"19.940001\",\"adjclose\":\"19.940001\",\"volume\":\"64575800\"}]}",
                                                        Timestamp.valueOf("2020-02-21 21:18:07.233")));
        stockDatas.put(StockSymbol.GOOGLE, new StockData(StockSymbol.GOOGLE.getID(), "{\"symbol\":\"GOOGL\",\"description\":\"google description\",\"history\":[{\"date\":\"2010-06-01\",\"open\":\"19.000000\",\"high\":\"30.420000\",\"low\":\"17.540001\",\"close\":\"23.830000\",\"adjclose\":\"23.830000\",\"volume\":\"35953400\"},{\"date\":\"2010-07-01\",\"open\":\"25.000000\",\"high\":\"25.920000\",\"low\":\"14.980000\",\"close\":\"19.940001\",\"adjclose\":\"19.940001\",\"volume\":\"64575800\"}]}",
                                                         Timestamp.valueOf("2020-03-21 21:18:07.233")));
        stockDatas.put(StockSymbol.FORD, new StockData(StockSymbol.FORD.getID(), "{\"symbol\":\"F\",\"description\":\"ford description\",\"history\":[{\"date\":\"2010-06-01\",\"open\":\"19.000000\",\"high\":\"30.420000\",\"low\":\"17.540001\",\"close\":\"23.830000\",\"adjclose\":\"23.830000\",\"volume\":\"35953400\"},{\"date\":\"2010-07-01\",\"open\":\"25.000000\",\"high\":\"25.920000\",\"low\":\"14.980000\",\"close\":\"19.940001\",\"adjclose\":\"19.940001\",\"volume\":\"64575800\"}]}",
                                                       Timestamp.valueOf("2020-04-21 21:18:07.233")));
        stockDatas.put(StockSymbol.BDX, new StockData(StockSymbol.BDX.getID(), "{\"symbol\":\"BDX\",\"description\":\"bdx description\",\"history\":[{\"date\":\"2010-06-01\",\"open\":\"19.000000\",\"high\":\"30.420000\",\"low\":\"17.540001\",\"close\":\"23.830000\",\"adjclose\":\"23.830000\",\"volume\":\"35953400\"},{\"date\":\"2010-07-01\",\"open\":\"25.000000\",\"high\":\"25.920000\",\"low\":\"14.980000\",\"close\":\"19.940001\",\"adjclose\":\"19.940001\",\"volume\":\"64575800\"}]}",
                                                      Timestamp.valueOf("2020-05-21 21:18:07.233")));

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
