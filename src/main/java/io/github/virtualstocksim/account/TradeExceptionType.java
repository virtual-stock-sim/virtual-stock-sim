package io.github.virtualstocksim.account;

public enum TradeExceptionType {

    NOT_ENOUGH_FUNDS("NOT_ENOUGH_FUNDS"),
    NOT_FOLLOWING_STOCK("NOT_FOLLOWING_STOCK"),
    NOT_ENOUGH_SHARES("NOT_ENOUGH_SHARES"),
    NOT_INVESTED("NOT_INVESTED"),
    USER_NOT_FOUND("USER_NOT_FOUND");

    private final String text;
    public String getText() {
        return this.text; }
    TradeExceptionType(String type) {
        this.text = type; }
}
