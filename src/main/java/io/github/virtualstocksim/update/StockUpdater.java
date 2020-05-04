package io.github.virtualstocksim.update;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.config.Config;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.scraper.Scraper;
import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockData;
import io.github.virtualstocksim.stock.StockDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Pulls current stock data from iexcloud api and updates the database
 */
public class StockUpdater
{
    private static final Logger logger = LoggerFactory.getLogger(StockUpdater.class);
    private static final String apiUrl = Config.getConfig("stockapi.url");

    /**
     * Update the frequently changing data for stocks
     * @param stocks Stocks to update
     */
    public static void updateStocks(List<Stock> stocks) throws UpdateException
    {

        // Create String of all stock symbols separated by commas
        StringBuilder sb = new StringBuilder();
        Iterator<Stock> it = stocks.iterator();
        while(it.hasNext())
        {
            sb.append(it.next().getSymbol());
            // Add comma if there's another symbol
            if(it.hasNext()) sb.append(',');
        }

        final JsonObject apiData;

        try
        {
            logger.info("Requesting new data from api");
            // It is assumed that stock symbols can be added onto the end
            // of the URL as a comma separated list to get the correct data
            URL url = new URL(apiUrl + sb.toString());
            URLConnection request = url.openConnection();

            if(!request.getContentType().contains("application/json"))
            {
                throw new IOException("API response was not JSON");
            }

            // Read the data from the request
            try(InputStreamReader reader = new InputStreamReader((InputStream) request.getContent()))
            {
                JsonElement element = JsonParser.parseReader(reader);
                logger.info("API Response: \n" + element);
                if(element.isJsonObject())
                {
                    apiData = element.getAsJsonObject();
                }
                else
                {
                    throw new UpdateException("Bad stock API response");
                }
            }

        }
        catch (MalformedURLException e)
        {
            throw new UpdateException("Bad stock api URL", e);
        }
        catch (IOException e)
        {
            throw new UpdateException("Exception while acquiring/parsing data from api", e);
        }

        try(Connection conn = StockDatabase.getConnection())
        {
            conn.setAutoCommit(false);

            for (Stock stock : stocks)
            {
                logger.info("Processing API data for Stock '" + stock.getSymbol() + "'");
                final JsonObject quote = getJsonMember(apiData, stock.getSymbol(), (s) -> getJsonMember(s, "quote", JsonElement::getAsJsonObject)).orElse(Optional.empty()).orElse(null);

                if(quote != null)
                {
                    logger.info(String.valueOf(quote));
                    Optional<BigDecimal> latestPrice = getJsonMember(quote, "latestPrice", JsonElement::getAsBigDecimal);
                    latestPrice.ifPresent(stock::setCurrPrice);

                    Optional<BigDecimal> previousClose = getJsonMember(quote, "previousClose", JsonElement::getAsBigDecimal);
                    previousClose.ifPresent(stock::setPrevClose);

                    Optional<Integer> latestVolume = getJsonMember(quote, "latestVolume", JsonElement::getAsInt);
                    latestVolume.ifPresent(stock::setCurrVolume);

                    Optional<Integer> previousVolume = getJsonMember(quote, "previousVolume", JsonElement::getAsInt);
                    previousVolume.ifPresent(stock::setPrevVolume);

                    stock.setLastUpdated(SQL.GetTimeStamp());

                    try
                    {
                        stock.update();
                    }
                    catch (SQLException e)
                    {
                        logger.error("Failed to commit stock information for Stock '" + stock.getSymbol() + "'", e);
                    }
                }
                else
                {
                    logger.warn("API response did not contain a quote for '" + stock.getSymbol() + "'");
                }
            }

            conn.commit();
        }
        catch (SQLException e)
        {
            throw new UpdateException("Failed to commit some or all updated stock information", e);
        }
    }

    /**
     * Update the static data for stocks
     * @param stocks List of stocks to perform the update for
     * @param interval Desired price history time interval
     */
    public static void updateStockDatas(List<Stock> stocks, TimeInterval interval) throws UpdateException
    {
        for (Stock stock : stocks)
        {
            // Get the stock data for the stock
            List<StockData> stockDatas = StockData.FindCustom(
                    "SELECT id FROM stock_data WHERE id = ?", stock.getStockDataId());
            if (!stockDatas.isEmpty())
            {
                StockData data = stockDatas.get(0);

                // Update the data in the DB
                try
                {
                    JsonObject scraperData = Scraper.getDescriptionAndHistory(stock.getSymbol(), interval);
                    Timestamp now = SQL.GetTimeStamp();
                    scraperData.addProperty("lastUpdated", now.toString());
                    data.setData(String.valueOf(scraperData));
                    data.setLastUpdated(now);

                    // Not using dedicated connection without auto commit since there's such a delay in-between update calls
                    data.update();
                }
                catch (IllegalArgumentException e)
                {
                    throw new UpdateException("Bad stock symbol: " + stock.getSymbol(), e);
                }
                catch (IOException e)
                {
                    throw new UpdateException(
                            "Exception while getting description and history for Stock with symbol " + stock.getSymbol(), e);
                }
                catch (SQLException e)
                {
                    throw new UpdateException(
                            "Exception while committing updates for Stock with symbol " + stock.getSymbol(), e);
                }
            }
        }
    }

    /**
     * Wraps the retrieval of a json element as a type within a try/catch to avoid code bloat
     * @param parent Parent element to retrieve member from
     * @param memberName Name of member that was attempting to be retrieved
     * @param getFunc Anonymous function to retrieve element from parent element
     * @param <T> Type of returned element
     * @return Result of getFunc
     */
    public static <T> Optional<T> getJsonMember(JsonElement parent, String memberName, Function<? super JsonElement, T> getFunc)
    {
        try
        {
            if(parent.isJsonObject())
            {
                JsonObject parentObj = parent.getAsJsonObject();
                if(parentObj.has(memberName))
                {
                    JsonElement element = parentObj.get(memberName);
                    if(!element.isJsonNull())
                    {
                        return Optional.of(getFunc.apply(element));
                    }
                    else
                    {
                        logger.warn(memberName + " is JsonNull");
                    }
                }
                else
                {
                    logger.warn("Parent does not contain " + memberName);
                }
            }
            else
            {
                logger.warn("Parent must be a valid JsonObject");
            }
        }
        catch (IllegalStateException | ClassCastException | NumberFormatException e)
        {
            logger.error("'" + memberName + "' is an incorrect type");
        }
        catch (JsonParseException e)
        {
            logger.error("'" + memberName + "' is not valid JSON");
        }

        return Optional.empty();
    }
}