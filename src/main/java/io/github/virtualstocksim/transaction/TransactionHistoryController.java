package io.github.virtualstocksim.transaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.database.SQL;
import io.github.virtualstocksim.stock.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

public class TransactionHistoryController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionHistoryController.class);


    public JsonArray getTransactionJSON (TransactionHistory transactionHistory){
        JsonArray ja = new JsonArray();
        for(Transaction t : transactionHistory.getTransactions()){
            JsonObject jo = new JsonObject();
            jo.addProperty("timestamp",t.getTimestamp().toString());
            jo.addProperty("stock",t.getStock().getId());
            jo.addProperty("type",t.getType().toString());
            jo.addProperty("shares",t.getNumShares());
            jo.addProperty("total",t.getVolumePrice());;
            jo.addProperty("price_per",t.getPricePerShare());
            ja.add(jo);
        }
        return ja;
    }
    /*TODO:
          Here is why Transaction should also probably have another constructor again to make this look better
    */

    public List<Transaction> parseTransactionFromJSON(JsonArray j){
        List tempList = new LinkedList();

        for(JsonElement x : j){

            tempList.add(new Transaction(TransactionType.valueOf(x.getAsJsonObject().get("type").getAsString()), Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString()),    x.getAsJsonObject().get("total").getAsBigDecimal(), x.getAsJsonObject().get("shares").getAsInt(), Stock.Find(x.getAsJsonObject().get("stock").getAsInt()).get()));
        }

        return tempList;
    }

/*
    public static void main(String[] args) {
        //This driver code is for testing only
        //will remove after debugging & ensured will stick with this JSON format
        LinkedList<Transaction> transactions = new LinkedList<>();
        transactions.add(new Transaction(TransactionType.BUY, SQL.GetTimeStamp(),new BigDecimal("1252.2"),2, Stock.Find(1).get()));
        transactions.add(new Transaction(TransactionType.BUY, SQL.GetTimeStamp(),new BigDecimal("50.12"),3, Stock.Find(2).get()));
        transactions.add(new Transaction(TransactionType.SELL,SQL.GetTimeStamp(),new BigDecimal("500.7"),100, Stock.Find(3).get()));
        transactions.add(new Transaction(TransactionType.BUY,SQL.GetTimeStamp(),new BigDecimal("123.8"),4, Stock.Find(4).get()));
        transactions.add(new Transaction(TransactionType.SELL,SQL.GetTimeStamp(),new BigDecimal("65.2"),120, Stock.Find(5).get()));
        TransactionHistory model = new TransactionHistory(transactions);
        TransactionHistoryController testController = new TransactionHistoryController();
      /*  logger.info(testController.getTransactionJSON(model).toString());
        List <Transaction> testList = testController.parseTransactionFromJSON(testController.getTransactionJSON(model));
        for (Transaction x : testList){
            logger.info(x.getStock().getSymbol());
        }
    }*/
}
