
package io.github.virtualstocksim.following;

import com.google.gson.*;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.util.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class FollowedStocks
{
    private static final Logger logger = LoggerFactory.getLogger(FollowedStocks.class);
    private Map<String, FollowedStock> followedStocks;

    public FollowedStocks(Map<String, FollowedStock> followedStocks)
    {
        this.followedStocks = followedStocks;
    }

    public FollowedStocks(String followedJson) throws JsonParseException
    {
        followedStocks = parseFollowedObjects(followedJson);
    }


    public Map<String, FollowedStock> getFollowedStocks()
    {
        return followedStocks;
    }

    public void setFollowedStocks(Map<String, FollowedStock> followedStocks)
    {
        this.followedStocks = followedStocks;
    }

    public void setFollowedStocks(String followedJson)
    {
        followedStocks = parseFollowedObjects(followedJson);
    }

    /**
     * Get a followed stock if it exists
     * @param stockSymbol Stock symbol to search for
     * @return An optional containing the FollowedStock object if found, otherwise Optional.empty()
     */
    public Optional<FollowedStock> getFollowedStock(String stockSymbol)
    {
        if(followedStocks.containsKey(stockSymbol))
        {
            return Optional.of(followedStocks.get(stockSymbol));
        }
        else
        {
            return Optional.empty();
        }
    }

    public void addFollowedStock(FollowedStock followed)
    {
        followedStocks.put(followed.getStock().getSymbol(), followed);
    }

    public void removeFollowedStock(String stockSymbol)
    {
        followedStocks.remove(stockSymbol);
    }

    /**
     * Is the account following this stock
     * @param stockSymbol Stock symbol to search for
     * @return If the account is following the stock
     */
    public boolean contains(String stockSymbol)
    {
        return followedStocks.containsKey(stockSymbol);
    }

    public JsonArray asJsonArray()
    {
        JsonArray followedArr = new JsonArray();

        for(FollowedStock followedStock : followedStocks.values())
        {
            followedArr.add(followedStock.asJsonObj());
        }

        return followedArr;
    }

    /**
     * Parses a JSON string of followed stocks in a Map of FollowedStock objects
     * @param followedJson JSON string of followed stocks
     * @return Map of FollowedStock objects with the stock symbol as the key
     * @throws JsonParseException followedJson argument was not able to be parsed
     */
    private Map<String, FollowedStock> parseFollowedObjects(String followedJson) throws JsonParseException
    {
        if(followedJson.trim().isEmpty())
        {
            logger.warn("followedJson is empty");
            return new HashMap<>();
        }

        logger.trace("Followed Json: \n" + followedJson);

        // Store followed stocks as a hash map
        Map<String, FollowedStock> stocksFollowed = new HashMap<>();

        // Attempt to get followedJson as a JsonArray
        JsonElement parsed = JsonParser.parseString(followedJson);
        JsonArray followedArr = JsonUtil.getAs(parsed, JsonElement::getAsJsonArray)
                                        .getOrNull(err -> logger.error("Error while converting followedJson to a json array; Error: " + err + "\n" + followedJson));

        // Attempt to add each element of followedArr into stocksFollowed
        for(JsonElement element : followedArr)
        {
            logger.trace("Follow Object: \n" + element);
            JsonObject followObj = JsonUtil.getAs(element, JsonElement::getAsJsonObject)
                                           .getOrNull(err -> logger.error("Error while converting follow object from json; Error: " + err + "\n" + element));

            if(followObj != null)
            {
                String symbol = JsonUtil.getMemberAs(followObj, "symbol", JsonElement::getAsString)
                                        .getOrNull(err -> logger.error("Error while getting symbol as string from follow object; Error " + err + "\n" + followObj));

                BigDecimal initialPrice = JsonUtil.getMemberAs(followObj, "initialPrice", JsonElement::getAsBigDecimal)
                                                  .getOrNull(err -> logger.error("Error while getting initial price as big decimal from follow object; Error: " + err + "\n" + followObj));

                Timestamp timestamp = null;
                // Timestamp.valueOf can throw an illegal argument exception if the timestamp is malformed
                try
                {
                    timestamp = JsonUtil.getMemberAs(followObj, "timestamp", JsonElement::getAsString)
                                        .process((obj, err) ->
                                                 {
                                                     if(err != null)
                                                     {
                                                         logger.error("Error while getting timestamp as string from follow object; Error: " + err + "\n" + followObj);
                                                         return null;
                                                     }
                                                     else
                                                     {
                                                         return Timestamp.valueOf(obj);
                                                     }
                                                 });
                }
                catch (IllegalArgumentException e)
                {
                    logger.error("Exception while converting follow timestamp from string to Timestamp\n", e);
                }

                if(symbol != null && initialPrice != null && timestamp != null)
                {
                    Optional<Stock> stock = Stock.Find(symbol);
                    if(stock.isPresent())
                    {
                        FollowedStock followedStock = new FollowedStock(stock.get(), initialPrice, timestamp);
                        stocksFollowed.put(symbol, followedStock);
                    }
                }
            }
        }

        return stocksFollowed;
    }
}