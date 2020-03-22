package io.github.virtualstocksim.transaction;

import io.github.virtualstocksim.stock.Stock;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

public class Transaction {

    private TransactionType type;
    private Timestamp timeStamp;
    private BigDecimal pricePerShare;
    private int numShares;
    private Stock stock;
    public Transaction (TransactionType type, Timestamp timeStamp, BigDecimal pricePerShare, int numShares, Stock stock)
    {
        this.type = type;
        this.timeStamp=timeStamp;
        this.pricePerShare = pricePerShare;
        this.numShares = numShares;
        this.stock = stock;
    }

    public TransactionType getType()
    {
        return type;
    }


    public Timestamp getTimestamp()
    {
        return this.timeStamp;
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

    public void setTimeStamp(Timestamp date)
    {
        this.timeStamp = date;
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
