package io.github.virtualstocksim.account;
import  io.github.virtualstocksim.account.Account;


public class AccountController {
    // account instance
    private Account acc = new Account();

    public AccountController() {
         //hardcoded values for now 
        acc.username = "VSS Admin";
        acc.password = "virtualstocksim";
        acc.email = "test-admin@vss.com";

    }



}
