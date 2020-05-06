package io.github.virtualstocksim.scraper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.github.virtualstocksim.stock.stockrequest.StockResponseCode;
import io.github.virtualstocksim.util.Errorable;
import io.github.virtualstocksim.util.priority.Priority;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.Assert.fail;

public class ScraperTest {
    private static final Logger logger = LoggerFactory.getLogger(ScraperTest.class);

    private static List<String> stressSymbols = new LinkedList<String>(Arrays.asList("MMM", "ABT", "ABBV", "ABMD", "ACN", "ATVI", "ADBE", "AMD", "AAP", "AES",
            "AFL", "AMZN", "TSLA", "T", "F", "ABC", "AME", "BA", "BR", "COG",
            "CAT", "CE", "CTL", "SCHW", "CB", "CHD", "C", "ORCL", "IBM", "PH",
            "PYPL", "PEP", "PPL", "RL", "LUV", "AAL", "WU", "WHR", "GE", "GM"));      //fourty example Stocks to search for



    @Test
    public void testExists() throws InterruptedException, ExecutionException
    {
        runTestFunction(p -> Scraper.checkStockExists(p.getLeft(), p.getRight()));
    }

    @Test
    public void testGetDescription() throws InterruptedException, ExecutionException
    {
        runTestFunction(p -> Scraper.getDescription(p.getLeft(), p.getRight()));
    }

    @Test
    public void testGetDescAndHist() throws InterruptedException, ExecutionException
    {
        runTestFunction(p -> Scraper.getDescriptionAndHistory(p.getLeft(), TimeInterval.ONEMONTH, p.getRight()));
    }

    private static final List<Pair<String, Priority>> symbols = new LinkedList<>();
    static
    {
        symbols.add(new ImmutablePair<>("GOOGL", Priority.MEDIUM));
        symbols.add(new ImmutablePair<>("BDX", Priority.MEDIUM));
        symbols.add(new ImmutablePair<>("RCL", Priority.LOW));
        symbols.add(new ImmutablePair<>("DISCK", Priority.URGENT));
        symbols.add(new ImmutablePair<>("aaaaasdlksdf", Priority.LOW));
        symbols.add(new ImmutablePair<>("^NASDAQ", Priority.HIGH));
        symbols.add(new ImmutablePair<>("AAPL", Priority.URGENT));
    }

    private static <T> void runTestFunction(Function<Pair<String, Priority>, Errorable<T, StockResponseCode>> func) throws InterruptedException, ExecutionException
    {
        final ExecutorService executor = Executors.newCachedThreadPool();
        List<Callable<Boolean>> tasks = new LinkedList<>();

        for(Pair<String, Priority> p : symbols)
        {
            tasks.add(() ->
                      {
                          Errorable<T, StockResponseCode> r = func.apply(p);
                          logger.info(p.getLeft());
                          if(r.isError())
                          {
                              if(p.getLeft().equals("aaaaasdlksdf") || p.getLeft().contains("^NASDAQ"))
                              {
                                  logger.info(r.getError().toString());
                                  return true;
                              }
                              else
                              {
                                  logger.error(r.getError().toString());
                                  return false;
                              }
                          }
                          else
                          {
                              logger.info(r.getValue() + "");
                              return true;
                          }
                      });
        }

        List<Future<Boolean>> futures = executor.invokeAll(tasks);
        for(Future<Boolean> future : futures)
        {
            if(!future.get())
            {
                fail();
            }
        }
    }

    /*
   s
    @BeforeClass //used for every test but stress test
    public static void setUp() throws IOException, InterruptedException {
        logger.info("Setting up scraper tests");
        Random r = new Random();

        for (int i = 0; i < symbols.size(); i++) {
            JsonObject jo = new JsonObject();
            if (i != 0 && i != symbols.size()) {
                pause();
            }
            JsonArrays.add(scraper.getDescriptionAndHistory(symbols.get(i), TimeInterval.ONEMONTH));

        }
    }

    Map<String, ArrayList<BigDecimal>> priceMap = new HashMap<String, ArrayList<BigDecimal>>();

    @Test
    public void checkStockOnYahooFinance() throws IOException {
        logger.info("Running stock existence checks");
        assertTrue(scraper.checkStockExists("googl"));
        assertTrue(scraper.checkStockExists("appl"));
        assertTrue(scraper.checkStockExists("msft"));

        assertFalse(scraper.checkStockExists("Brett"));
        assertFalse(scraper.checkStockExists("java"));
    }


    @Test
    public void testGetOpenPriceHistory() {

        int x = 0, y = 0;
        for (JsonArray ja : JsonArrays) {
            x++;
            ArrayList<BigDecimal> temp = new ArrayList<>();
            for (JsonElement je : ja) {
                y++;
                JsonElement openPrice = je.getAsJsonObject().get("open");
                if(openPrice != null && !openPrice.isJsonNull() && !openPrice.toString().contains("null")) {
                    temp.add(openPrice.getAsBigDecimal());

                }
                priceMap.put(symbols.get(x-1),temp);
            }
        }
        List <BigDecimal> googleList = priceMap.get("GOOGL");       //I know this is really inefficient, but the test is already slow from webscraping limitations
        assertTrue(googleList.contains(BigDecimal.valueOf(1027.199951)));//Compare againt values manually downloaded from CSV file
        assertTrue(googleList.contains(BigDecimal.valueOf(1066.930054)));
        assertFalse(googleList.contains(BigDecimal.valueOf(1234.526)));


        List <BigDecimal> abtList = priceMap.get("ABT");
        assertTrue(abtList.contains(BigDecimal.valueOf(36.169998)));
        assertTrue(abtList.contains(BigDecimal.valueOf(53.889999)));
        assertTrue(abtList.contains(BigDecimal.valueOf(84.029999)));
        assertFalse(abtList.contains(BigDecimal.valueOf(10000.23)));

        List <BigDecimal> mmmList = priceMap.get("MMM");
        assertTrue(mmmList.contains(BigDecimal.valueOf(148.050003)));
        assertTrue(mmmList.contains(BigDecimal.valueOf(175.139999)));
        assertTrue(mmmList.contains(BigDecimal.valueOf(178.830002)));
        assertFalse(mmmList.contains(BigDecimal.valueOf(45646)));

        Iterator iter = priceMap.entrySet().iterator();
        /*
        while (iter.hasNext()){
            Map.Entry pair = (Map.Entry)iter.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
            iter.remove();
        }
    }
    @Test
    public void testCompanyDescription() throws IOException {
        for (JsonArray ja : JsonArrays) {
            for (JsonElement je : ja) {
                if (je.getAsJsonObject().get("description") != null) {
                    descriptions.add(je.getAsJsonObject().get("description").getAsString());
                }
            }
        }

        assertEquals(descriptions.size(), 5);
        assertTrue(descriptions.get(0).contains("Transportation & Electronics, Health Care, and Consumer. The Safety & Industrial segment offers personal safety products, adhesives and tapes, abrasives, closure and masking systems"));
        assertTrue(descriptions.get(4).contains("Southwest Airlines Co. operates a passenger airline that provides scheduled air transportation services in the United States and near-international markets."));
        assertTrue(descriptions.get(1).contains("Abbott Laboratories discovers, develops, manufactures, and sells health care products worldwide. Its Established Pharmaceutical Products segment offers branded generic pharmaceuticals for the treatment of pancreatic exocrine insufficiency; irritable bowel syndrome or biliary spasm"));

    }





    @Test//This test will take some time to do (~9 minutes)
    //works the same as setUp, but is meant to check to see if the scraper can handle a load of forty stocks

    public void stressTest() throws InterruptedException, IOException {
        Random r = new Random();
        Scraper scraper = new Scraper();
        for  (int i=0; i< stressSymbols.size();i++){
            JsonObject jo = new JsonObject();
            int y = r.nextInt(6);
            System.out.println("Testing next stock");
            if(i!=0 && i!=stressSymbols.size()){TimeUnit.SECONDS.sleep(10+y); }
            stressJsonArrays.add(scraper.getDescriptionAndHistory(stressSymbols.get(i),TimeInterval.ONEMONTH));
        }
        assertEquals(stressJsonArrays.size(),40);
        System.out.println("done");

    }*/



}
