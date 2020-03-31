package io.github.virtualstocksim.transaction;

public enum  TransactionType
{
    BUY("Buy"), SELL("Sell");

    private final String text;
    public String getText() {
        return this.text; }
    TransactionType(String type) {
        this.text = type; }
}