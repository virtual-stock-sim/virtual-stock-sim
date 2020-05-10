package io.github.virtualstocksim.stock.stockrequest;

import com.google.gson.*;
import io.github.virtualstocksim.account.Account;
import io.github.virtualstocksim.account.AccountController;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.following.FollowedStock;
import io.github.virtualstocksim.following.FollowedStocks;
import io.github.virtualstocksim.scraper.Scraper;
import io.github.virtualstocksim.scraper.TimeInterval;
import io.github.virtualstocksim.servlet.HttpRequestListener;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.stock.StockData;
import io.github.virtualstocksim.stock.StockDatabase;
import io.github.virtualstocksim.update.ClientUpdater;
import io.github.virtualstocksim.util.json.JsonUtil;
import io.github.virtualstocksim.util.priority.Priority;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        if(requestParam != null)
        {
            logger.trace(requestParam);

            try
            {
                JsonElement requestElem = JsonParser.parseString(requestParam);
                JsonObject stockRequest = JsonUtil.getAs(requestElem, JsonElement::getAsJsonObject)
                                                  .getOrNull(err ->
                                                             {
                                                                 throw new StockRequestException("Couldn't get stock request as json object: " + err, StockResponseCode.BAD_REQUEST);
                                                             });

                JsonArray requestItems = JsonUtil.getMemberAs(stockRequest, "items", JsonElement::getAsJsonArray)
                                                 .getOrNull(err ->
                                                            {
                                                                logger.error("Exception getting items array from stock request: " + err);
                                                                throw  new StockRequestException("Couldn't get items array: " + err, StockResponseCode.BAD_REQUEST);
                                                            });


                long dataTTL = ClientUpdater.getStockDataUpdateInterval().toMillis();

                Account account = null;
                for (JsonElement requestItemElem : requestItems)
                {
                    logger.trace(String.valueOf(requestItemElem));

                    JsonObject responseItem = new JsonObject();
                    StockType type;
                    StockResponseCode responseCode = StockResponseCode.OK;
                    try
                    {
                        // Get the request item
                        JsonObject requestItem = JsonUtil.getAs(requestItemElem, JsonElement::getAsJsonObject)
                                                         .getOrNull(err ->
                                                                    {
                                                                        throw new StockRequestException("Bad request item: " + err, StockResponseCode.BAD_REQUEST);
                                                                    });

                        type = StockType.from(
                                JsonUtil.getMemberAs(requestItem, "type", JsonElement::getAsString)
                                        .getOrNull(err ->
                                                   {
                                                       throw new StockRequestException("Bad request type: " + err, StockResponseCode.BAD_REQUEST);
                                                   })
                                             );

                        responseItem.addProperty("type", type.asString());

                        if (type == StockType.FOLLOW)
                        {
                            HttpSession session = req.getSession(false);
                            String uuid;
                            if (session == null || (uuid = session.getAttribute("uuid").toString()) == null || uuid.trim().isEmpty())
                                throw new StockRequestException("Follow request received without a user attached to the session", StockResponseCode.BAD_REQUEST);

                            account = Account.Find("uuid", uuid).orElse(null);

                            if (account == null)
                                throw new StockRequestException("Invalid user attached to the session", StockResponseCode.SERVER_ERROR);
                        }

                        String symbol = JsonUtil.getMemberAs(requestItem, "symbol", JsonElement::getAsString)
                                                .getOrNull(err ->
                                                           {
                                                               throw new StockRequestException("Unable to get symbol from request item: " + err, StockResponseCode.BAD_REQUEST);
                                                           });

                        symbol = symbol.trim().toUpperCase();
                        // Stocks with a ^ denote an index, not an actual company
                        if (symbol.isEmpty() || symbol.contains("^") || symbol.length() > StockDatabase.getMaxSymbolLen())
                        {
                            logger.warn("Invalid stock symbol: " + symbol);
                            responseCode = StockResponseCode.INVALID_SYMBOL;
                        }
                        else
                        {
                            responseItem.addProperty("symbol", symbol);

                            Pair<Optional<Stock>, Optional<StockData>> results = processRequest(type, symbol, account);

                            Optional<Stock> stock = results.getLeft();
                            if (stock.isPresent())
                            {
                                responseItem.add("stock", stock.get().asJson());
                                // If last updated is null, then the stock was newly found and is a placeholder
                                // until the next update cycle
                                if (stock.get().getLastUpdated() == null)
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
        }
        else
        {
            overallCode = StockResponseCode.BAD_REQUEST;
        }

        stockResponse.addProperty("code", overallCode.asInt());
        stockResponse.add("items", responseItems);

        resp.setContentType("application/json");

        // Write the response
        PrintWriter writer = resp.getWriter();
        writer.write(String.valueOf(stockResponse));
        writer.flush();
    }

    private Pair<Optional<Stock>, Optional<StockData>> processRequest(StockType type, String symbol, Account account) throws StockRequestException
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
                break;
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
            case FOLLOW:
            {
                Optional<Stock> stock = Stock.Find(symbol);
                if(stock.isPresent())
                {
                    AccountController controller = new AccountController();
                    controller.setModel(account);
                    try
                    {
                        controller.followStock(stock.get());
                        return new ImmutablePair<>(Optional.empty(), Optional.empty());
                    }
                    catch (SQLException e)
                    {
                        throw new StockRequestException("Exception while committing account after following stock; Uuid: " + account.getUUID(), StockResponseCode.SERVER_ERROR, e);
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
        // Retrieve and add new stock data to database
        JsonObject descAndHist = Scraper.getDescriptionAndHistory(symbol, TimeInterval.ONEMONTH, Priority.HIGH)
                                        .getOrNull(err ->
                                                   {
                                                       if(err != StockResponseCode.SYMBOL_NOT_FOUND)
                                                       {
                                                           throw new StockRequestException("Scraper was unable to get description and history for stock symbol: " + symbol, err);
                                                       }
                                                   });
        // Null means the symbol wasn't found since an exception gets thrown for any
        // response code other than symbol not found
        if(descAndHist == null)
        {
            logger.info("Stock symbol was not found");
            return new ImmutablePair<>(Optional.empty(), Optional.empty());
        }

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
}
