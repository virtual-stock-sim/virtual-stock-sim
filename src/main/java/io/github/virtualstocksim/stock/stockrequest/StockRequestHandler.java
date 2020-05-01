package io.github.virtualstocksim.stock.stockrequest;

import com.google.gson.*;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.scraper.Scraper;
import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.servlet.HttpRequestListener;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockData;
import io.github.virtualstocksim.stock.StockDatabase;
import io.github.virtualstocksim.update.ClientUpdater;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class StockRequestHandler implements HttpRequestListener
{
    private static final Logger logger = LoggerFactory.getLogger(StockRequestHandler.class);
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    @Override
    public void onGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {

    }

    @Override
    public void onPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String requestParam = req.getParameter("stockRequest");

        JsonObject stockResponse = new JsonObject();

        StockResponseCode overallCode = StockResponseCode.OK;
        JsonArray responseItems = new JsonArray();

        try
        {
            JsonObject stockRequest = getOrError(() -> JsonParser.parseString(requestParam).getAsJsonObject(), StockResponseCode.BAD_REQUEST, "stockRequest");

            JsonArray requestItems = getOrError(() -> stockRequest.getAsJsonArray("items"), StockResponseCode.BAD_REQUEST, "items");


            long dataTTL = ClientUpdater.getStockDataUpdateInterval().toMillis();

            for(JsonElement requestItemElem : requestItems)
            {
                JsonObject responseItem = new JsonObject();
                StockType type;
                StockResponseCode responseCode = StockResponseCode.OK;
                try
                {
                    JsonObject requestItem = getOrError(requestItemElem::getAsJsonObject, StockResponseCode.BAD_REQUEST, "Request Item");

                    type = getOrError(() -> StockType.of(requestItem.get("type").getAsString()), StockResponseCode.INVALID_TYPE, "type");
                    responseItem.addProperty("type", type.asString());

                    String symbol = getOrError(() -> requestItem.get("symbol").getAsString(), StockResponseCode.INVALID_SYMBOL, "symbol");
                    symbol = symbol.trim().toUpperCase();
                    // Stocks with a ^ denote an index, not an actual company
                    if(symbol.isEmpty() || symbol.contains("^") || symbol.length() > StockDatabase.getMaxSymbolLen())
                        throw new StockRequestException("Invalid stock symbol: " + symbol, StockResponseCode.INVALID_SYMBOL);

                    responseItem.addProperty("symbol", symbol);

                    Pair<Optional<Stock>, Optional<StockData>> results = processRequest(type, symbol);

                    Optional<Stock> stock = results.getLeft();
                    if(stock.isPresent())
                    {
                        responseItem.add("stock", stock.get().asJson());
                        // If last updated is null, then the stock was newly found and is a placeholder
                        // until the next update cycle
                        if(stock.get().getLastUpdated() == null)
                        {
                            responseCode = StockResponseCode.PROCESSING;
                        }
                    }

                    Optional<StockData> data = results.getRight();

                    data.ifPresent(d ->
                                   {
                                       JsonObject dataObj = d.asJson();
                                       dataObj.addProperty("ttl", dataTTL);
                                       responseItem.add("data", dataObj);
                                   });
                }
                catch (StockRequestException e)
                {
                    logger.error("Exception processing stock request item: \n" + requestItemElem + "\n", e);
                    responseCode = e.getErrorCode();
                }

                responseItem.addProperty("code", responseCode.asInt());
                responseItems.add(responseItem);
            }

        }
        catch (StockRequestException e)
        {
            overallCode = e.getErrorCode();
            logger.error("Exception processing stock request. \n", e);
        }

        stockResponse.addProperty("code", overallCode.asInt());
        stockResponse.add("items", responseItems);

        resp.setContentType("application/json");

        // Write the response
        PrintWriter writer = resp.getWriter();
        writer.write(String.valueOf(stockResponse));
        writer.flush();
    }

    private Pair<Optional<Stock>, Optional<StockData>> processRequest(StockType type, String symbol) throws StockRequestException
    {
        switch (type)
        {
            // Only stock was requested
            case STOCK:
            {
                Optional<Stock> stock = Stock.Find(symbol);
                if(stock.isPresent())
                {
                    return new ImmutablePair<>(stock, Optional.empty());
                }
                break;
            }
            // Only stock data was requested
            case STOCK_DATA:
            {
                Optional<StockData> data = StockData.Find(symbol);
                if(data.isPresent())
                {
                    return new ImmutablePair<>(Optional.empty(), data);
                }
                else
                {
                    throw new StockRequestException("Stock was found but data wasn't", StockResponseCode.SERVER_ERROR);
                }
            }
            // Stock and stock data where both requested
            case BOTH:
            {
                Optional<Stock> stock = Stock.Find(symbol);
                if(stock.isPresent())
                {
                    Optional<StockData> data = StockData.Find(stock.get().getStockDataId());
                    if(data.isPresent())
                    {
                        return new ImmutablePair<>(stock, data);
                    }
                    else
                    {
                        throw new StockRequestException("Stock was found but data wasn't", StockResponseCode.SERVER_ERROR);
                    }
                }
                break;
            }
            default:
                throw new StockRequestException("Invalid Type: " + type, StockResponseCode.INVALID_TYPE);
        }

        // If this was reached, a stock was not found in the database
        return findNewStock(symbol);
    }

    private static final BigDecimal BIGD_NEG_ONE = new BigDecimal("-1.0");
    private Pair<Optional<Stock>, Optional<StockData>> findNewStock(String symbol) throws StockRequestException
    {
        try
        {
            // Retrieve and add new stock data to database
            JsonObject descAndHist = Scraper.getDescriptionAndHistory(symbol, TimeInterval.ONEMONTH);
            Optional<StockData> data = StockData.Create(String.valueOf(descAndHist), SQL.GetTimeStamp());

            if(data.isPresent())
            {
                // Create a placeholder stock to be updated in the next update cycle
                Optional<Stock> stock = Stock.Create(symbol, BIGD_NEG_ONE, BIGD_NEG_ONE, -1, -1, data.get().getId(), null);
                if(stock.isPresent())
                {
                    return new ImmutablePair<>(stock, data);
                }
                else
                {
                    throw new StockRequestException("New Stock was unable to be created in the database", StockResponseCode.SERVER_ERROR);
                }
            }
            else
            {
                throw new StockRequestException("New StockData was unable to be created in the database", StockResponseCode.SERVER_ERROR);
            }

        }
        catch (IOException e)
        {
            throw new StockRequestException("Exception finding stock with scraper", StockResponseCode.SERVER_ERROR, e);
        }
        catch (IllegalArgumentException e)
        {
            throw new StockRequestException("Stock '" + symbol + "' was unable to be found", StockResponseCode.INVALID_SYMBOL);
        }
    }

    /**
     * Wraps the retrieval of a json element as a type within a try/catch to avoid code bloat
     * @param getFunc Anonymous function to retrieve element
     * @param onError Error code to add to StockRequestException if retrieval fails
     * @param memberName Name of member tha was attempting to be retrieved
     * @param <T> Type of returned element
     * @return Result of getFunc
     */
    public static  <T> T getOrError(Supplier<T> getFunc, StockResponseCode onError, String memberName) throws StockRequestException
    {
        try
        {
            return getFunc.get();
        }
        catch (NullPointerException e)
        {
            throw new StockRequestException("'" + memberName + "' does not exist\n", onError, e);
        }
        catch (IllegalStateException | ClassCastException | NumberFormatException e)
        {
            throw new StockRequestException("'" + memberName + "' is an incorrect type", onError, e);
        }
        catch (JsonParseException e)
        {
            throw new StockRequestException("'" + memberName + "' is not valid JSON", onError, e);
        }
    }
}
