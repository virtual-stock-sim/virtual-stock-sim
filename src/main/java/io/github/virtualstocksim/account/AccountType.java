package io.github.virtualstocksim.account;

public enum AccountType
{
    ADMIN("Admin"), USER("User");

    private final String text;

    public String getText(){return this.text;}

    AccountType(String type) {this.text = type;}
}
