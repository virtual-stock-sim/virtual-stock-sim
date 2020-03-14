package io.github.virtualstocksim.transaction;

import io.github.virtualstocksim.stock.Stock;

import java.math.BigDecimal;

public class Transaction {
    public enum TransactionType
    {
        BUY("Buy"), SELL("Sell");

        private final String text;
        public String getText() { return this.text; }
        TransactionType(String type) { this.text = type; }
    }

    private TransactionType type;
    private String date;
    private BigDecimal pricePerShare;
    private int numShares;
    private Stock stock;
    public Transaction (TransactionType type, String date, BigDecimal pricePerShare, int numShares, Stock stock)
    {
        this.type = type;
        this.date = date;
        this.pricePerShare = pricePerShare;
        this.numShares = numShares;
        this.stock = stock;
    }

    public TransactionType getType()
    {
        return type;
    }

    public void setType(TransactionType type)
    {
        this.type = type;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public BigDecimal getPricePerShare()
    {
        return pricePerShare;
    }

    public void setPricePerShare(BigDecimal pricePerShare)
    {
        this.pricePerShare = pricePerShare;
    }

    public int getNumShares()
    {
        return numShares;
    }

    public void setNumShares(int numShares)
    {
        this.numShares = numShares;
    }

    public Stock getStock()
    {
        return stock;
    }

    public void setStock(Stock stock)
    {
        this.stock = stock;
    }
}
