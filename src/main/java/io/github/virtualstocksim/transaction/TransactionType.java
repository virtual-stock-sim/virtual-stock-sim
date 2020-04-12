package io.github.virtualstocksim.transaction;

public enum  TransactionType
{
    BUY("BUY"), SELL("SELL");

    private final String text;
    public String getText() {
        return this.text; }
    TransactionType(String type) {
        this.text = type; }
}