package io.github.virtualstocksim.transaction;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.virtualstocksim.stock.Stock;

import java.sql.Timestamp;
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

    public TransactionHistory(String s){
      this.setTransactions(this.parseTransactionFromJSON(s));
    }

    public List<Transaction> getTransactions()
    {
        return this.transactions;
    }

    public void setTransactions(List<Transaction> t )
    {
        this.transactions=t;
    }

    public void addTransaction(Transaction transaction)
    {
        this.transactions.add(transaction);
    }

    //right now, I see no reason why there needs to be any remove methods or checks for a particular stock in transaction
    //can implement easily later if we decide users can delete their transaction history

    public String buildTransactionJSON (){
        JsonArray ja = new JsonArray();
        for(Transaction t : this.transactions){
            JsonObject jo = new JsonObject();
            jo.addProperty("timestamp",t.getTimestamp().toString());
            jo.addProperty("stock",t.getStock().getId());
            jo.addProperty("type",t.getType().toString());
            jo.addProperty("shares",t.getNumShares());
            jo.addProperty("total",t.getVolumePrice());;
            jo.addProperty("price_per",t.getPricePerShare());
            ja.add(jo);
        }
        return ja.toString();
    }


    //builds new transactionHistory from a Json Array
    public List<Transaction> parseTransactionFromJSON(String  s){
        List <Transaction>tempList = new LinkedList();
        System.out.println("String input for parsetransaction from json");
        System.out.println(s);
        JsonArray j  = JsonParser.parseString(s).getAsJsonArray();
        for(JsonElement x : j){
            tempList.add(new Transaction(TransactionType.valueOf(x.getAsJsonObject().get("type").getAsString()), Timestamp.valueOf(x.getAsJsonObject().get("timestamp").getAsString()),    x.getAsJsonObject().get("total").getAsBigDecimal(), x.getAsJsonObject().get("shares").getAsInt(), Stock.Find(x.getAsJsonObject().get("stock").getAsInt()).get()));
        }
       return tempList;
    }

    //This method will probably not be used outside of tests
    public void updateTransactions(String s ){
        this.transactions= (this.parseTransactionFromJSON(s));
    }

}
