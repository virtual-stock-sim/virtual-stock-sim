package io.github.virtualstocksim.scraper;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Scraper {

    private static final Logger logger = LoggerFactory.getLogger(Scraper.class);

    //will be called within the getJson method
    public static String getDescription(String tickerSymbol) throws IOException, ConnectException {
        logger.info("Getting company description for stock symbol: " + tickerSymbol);
        String URL = "https://finance.yahoo.com/quote/" + tickerSymbol + "/profile?p=" + tickerSymbol;
        Document doc = Jsoup.connect(URL).timeout(0).get();//timeout set to 0 indicates infinite
        Element s = doc.getElementsByClass("Mt(15px) Lh(1.6)").first();
        return s.text();
    }

    //Might not need to use this depending on what we end up doing with the API
    public static BigDecimal getCurrentPrice(String tickerSymbol)throws IOException{
        String URL = "https://finance.yahoo.com/quote/"+tickerSymbol+"/profile?p="+tickerSymbol;

        Document doc = Jsoup.connect(URL).get();
        Element e = doc.getElementsByClass("Trsdu(0.3s) Fw(b) Fz(36px) Mb(-4px) D(ib)").first();
        return new BigDecimal(e.text().replace(",",""));
    }

    //this method checks if the stock exists on Yahoo finance
    //Because Yahoo finance doesn't 404 if you look for a stock that isn't there
    //I just checked if it redirected to a "lookup" page rather than a "quote" page
    public static boolean checkStockExists(String ticker) throws IOException {
        logger.info("Checking if stock symbol `" + ticker + "` exists");
        try {
            Connection.Response response = Jsoup.connect("https://finance.yahoo.com/quote/"+ticker).followRedirects(true).execute();
            int statusCode = response.statusCode();
            String redirect = response.url().toString();

            if (redirect.contains("lookup") || statusCode!=200) {
                System.out.println("status code: " + statusCode);
                //was redirected to a lookup page, not on yahoo finance
                return false;
            } else {
                //stock is in yahoo finance
                return true;
            }
        }catch (HttpStatusException e){ //this will account for MALFORMED tickers only (ex: starting name with a slash)
            logger.warn("Status code of " + e.getStatusCode() + " returned from " + e.getUrl() + "\n", e);
        }
        return false;
    }


    public static JsonObject getDescriptionAndHistory(String ticker, TimeInterval timeInterval) throws IOException, IllegalArgumentException {
        logger.info("Getting company description and price history for stock symbol `" + ticker + "`");
        long unixTime = System.currentTimeMillis() / 1000L;
        //calculate seconds passed & sub from current unix time

        JsonArray priceHistory = new JsonArray();

        JsonObject content = new JsonObject();
        content.addProperty("description", getDescription(ticker));
        if(checkStockExists(ticker)) {

            logger.info("Getting max price history for `" + ticker + "` with time interval of " + timeInterval.getPeriod());
            //since unix time calculates time from epoch, 0 and current time are really min and max values, do not need integer.max
            String webResponse = new Scanner(new URL("https://query1.finance.yahoo.com/v7/finance/download/" + ticker + "?period1=" + "0" + "&period2=" + unixTime + "&interval="+timeInterval.getPeriod()+"&events=history").openStream(), "UTF-8").useDelimiter("\\A").next();

            List<String> rowsList = new LinkedList<String>(Arrays.asList(webResponse.split("\\n")));//get rows separated by line
            rowsList.remove(0);      //get rid of header

            List<String> col = new LinkedList<String>();

            for (int i = 0; i < rowsList.size(); i++) {
                col.addAll(Arrays.asList(rowsList.get(i).split(",")));
            }


            for (int i = 0; i < col.size() - 6; i += 7) {
                JsonObject jo = new JsonObject();
                jo.addProperty("date", col.get(i));
                jo.addProperty("open", col.get(i + 1));
                jo.addProperty("high", col.get(i + 2));
                jo.addProperty("low", col.get(i + 3));
                jo.addProperty("close", col.get(i + 4));
                jo.addProperty("adjclose", col.get(i + 5));
                jo.addProperty("volume", col.get(i + 6));
                priceHistory.add(jo);
            }
            content.add("history", priceHistory);

            JsonObject finalObject = new JsonObject();
            finalObject.add(ticker, content);

            return finalObject;
        }else{
            logger.warn("Stock symbol `" + ticker + "` does not exist. Unable to retrieve description and history");
            throw new IllegalArgumentException("Stock symbol " + ticker + " does not exist");
        }

        //return gson.toJson(ja); this would enable pretty printing if returned
    }













}
