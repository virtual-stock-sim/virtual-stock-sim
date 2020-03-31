package io.github.virtualstocksim.scraper;

public enum TimeInterval {
    //https://query1.finance.yahoo.com/v7/finance/download/AAPL?period1=1553996999&period2=1585619399&interval=2wk&events=history
    //all of the arguments allowed for this long of a price history by yahoo are:
    ONEDAY("1d"),
    FIVEDAYS("5d"),
    ONEWEEK("1wk"),
    ONEMONTH("1mo"),
    THREEMONTH("3mo");


    private final String field;

    public String getPeriod(){
        return this.field;
    }
    TimeInterval(String s) {
        this.field=s;
    }
}
