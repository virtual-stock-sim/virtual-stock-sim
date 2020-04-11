package io.github.virtualstocksim.transaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.virtualstocksim.stock.Stock;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TransactionHistory
{
//take a string and turn it into the transaction history
    private List<Transaction> transactions;
    private String jsonString;

    public TransactionHistory(List<Transaction> transactions)
    {
        this.transactions = new LinkedList<>(transactions);
    }

    public TransactionHistory(String jsonString){
        this.jsonString=jsonString;
    }

    public List<Transaction> getTransactions()
    {
        return this.transactions;
    }

    public void setTransactions(Transaction... transactions)
    {
        this.transactions = new LinkedList<>(Arrays.asList(transactions));
    }

    public void addTransaction(Transaction transaction)
    {
        this.transactions.add(transaction);
    }


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

    public TransactionHistory parseTransactionFromJSON(JsonArray j){
        List tempList = new LinkedList();
        for(JsonElement x : j){

            tempList.add(new Transaction(TransactionType.valueOf(x.getAsJsonObject().get("type").getAsString()), Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString()),    x.getAsJsonObject().get("total").getAsBigDecimal(), x.getAsJsonObject().get("shares").getAsInt(), Stock.Find(x.getAsJsonObject().get("stock").getAsInt()).get()));
        }
        return new TransactionHistory (tempList);
    }


}
