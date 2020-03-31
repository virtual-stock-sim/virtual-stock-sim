package io.github.virtualstocksim.scraper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class ScraperTest {
    List<String> stressTickers = new LinkedList<String>(Arrays.asList("MMM", "ABT", "ABBV", "ABMD", "ACN", "ATVI", "ADBE", "AMD", "AAP", "AES",
            "AFL", "AMZN", "TSLA", "T", "F", "ABC", "AME", "BA", "BR", "COG",
            "CAT", "CE", "CTL", "SCHW", "CB", "CHD", "C", "ORCL", "IBM", "PH",
            "PYPL", "PEP", "PPL", "RL", "LUV", "AAL", "WU", "WHR", "GE", "GM"));      //fourty example Stocks to search for


    List<String> tickers = new LinkedList<String>(Arrays.asList("MMM", "ABT", "ABBV", "GOOGL", "LUV"));
    List<String> descriptions = new LinkedList<String>();
    List<JsonArray> JsonArrays = new LinkedList<JsonArray>();
    List<JsonArray> stressJsonArrays = new LinkedList<JsonArray>();
    List<BigDecimal> openingPrices = new LinkedList<BigDecimal>();

    Scraper scraper = new Scraper();
    @Before //used for every test but stress test
    public void setUp() throws IOException, InterruptedException {
        Random r = new Random();

        for (int i = 0; i < tickers.size(); i++) {
            JsonObject jo = new JsonObject();
            int y = r.nextInt(6);
            if (i != 0 && i != tickers.size()) {
                TimeUnit.SECONDS.sleep(10+y);
            }
            JsonArrays.add(scraper.getDescriptionAndHistory(tickers.get(i), TimeInterval.ONEMONTH));

        }
    }

    Map<String, ArrayList<BigDecimal>> priceMap = new HashMap<String, ArrayList<BigDecimal>>();

    @Test
    public void checkStockOnYahooFinance() throws IOException {
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
            ArrayList temp = new ArrayList();
            for (JsonElement je : ja) {
                y++;
                if(je.getAsJsonObject().get("open") != null) {
                    temp.add(je.getAsJsonObject().get("open").getAsBigDecimal());

                }
                priceMap.put(tickers.get(x-1),temp);
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
        }*/
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
        for  (int i=0; i< stressTickers.size();i++){
            JsonObject jo = new JsonObject();
            int y = r.nextInt(6);
            System.out.println("Testing next stock");
            if(i!=0 && i!=stressTickers.size()){TimeUnit.SECONDS.sleep(10+y); }
            stressJsonArrays.add(scraper.getDescriptionAndHistory(stressTickers.get(i),TimeInterval.ONEMONTH));
        }
        assertEquals(stressJsonArrays.size(),40);
        System.out.println("done");

    }



}
