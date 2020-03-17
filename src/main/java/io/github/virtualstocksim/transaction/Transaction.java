package io.github.virtualstocksim.transaction;

import io.github.virtualstocksim.stock.Stock;

import java.math.BigDecimal;

public class Transaction {

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


    public String getDate()
    {
        return date;
    }


    public BigDecimal getPricePerShare()
    {
        return pricePerShare;
    }


    public int getNumShares()
    {
        return numShares;
    }


    public BigDecimal getVolumePrice(){
        return pricePerShare.multiply(new BigDecimal(this.numShares));
    }

    public Stock getStock()
    {
        return stock;
    }


    public void setType(TransactionType type)
    {
        this.type = type;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public void setPricePerShare(BigDecimal pricePerShare)
    {
        this.pricePerShare = pricePerShare;
    }

    public void setStock(Stock stock)
    {
        this.stock = stock;
    }

    public void setNumShares(int numShares)
    {
        this.numShares = numShares;
    }





}
