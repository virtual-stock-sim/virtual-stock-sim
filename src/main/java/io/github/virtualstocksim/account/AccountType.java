package io.github.virtualstocksim.account;

import java.util.NoSuchElementException;

public enum AccountType
{
    ADMIN("ADMIN"), USER("USER");

    public static AccountType getByID(int id) throws NoSuchElementException
    {
        for(AccountType at : values())
        {
            if(at.ordinal() == id) return at;
        }

        throw new NoSuchElementException(String.format("Invalid ID - AccountType of id '%s' doesn't exist", id));
    }

    private final String text;

    public String getText(){return this.text;}

    AccountType(String type) {this.text = type;}
}
