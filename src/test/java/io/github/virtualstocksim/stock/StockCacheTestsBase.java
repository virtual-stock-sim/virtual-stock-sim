package io.github.virtualstocksim.stock;


import io.github.virtualstocksim.database.DatabaseTestsBase;
import org.junit.BeforeClass;
import org.junit.Ignore;

@Ignore
public class StockCacheTestsBase extends DatabaseTestsBase
{
    public static StockCache sc;

    @BeforeClass
    public static void setupStockCacheInstance()
    {
        StockCache.changeDatabase(dbPath);
        sc = StockCache.Instance();
    }
}
