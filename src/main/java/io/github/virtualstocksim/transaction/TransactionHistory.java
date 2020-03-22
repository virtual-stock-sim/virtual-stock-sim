package io.github.virtualstocksim.transaction;
import io.github.virtualstocksim.stock.Stock;
import io.github.virtualstocksim.transaction.Transaction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TransactionHistory
{

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




}
