package io.github.virtualstocksim.account;

public class Account {
    public String username;
    public String password;
    public String email;


    public Account(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }



    // this method should return a list of transactions (I think?)
    public void getTransactionHistory () {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
