package io.github.virtualstocksim.account;

public enum AccountType
{
    USER("User"), ADMIN("Admin");

    private final String text;
    public String getText(){return this.text;}
    AccountType(String type) {this.text = type;}
}
