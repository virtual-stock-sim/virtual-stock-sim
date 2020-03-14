package io.github.virtualstocksim.account;

import org.junit.Before;
import org.junit.Test;

public class AccountControllerTest
{
    private AccountController conn;
    private Account acc;

    @Before
    public void setup(){
        conn = new AccountController();
        acc = new Account();

        conn.setModel(acc);
    }

    // No tests yet because account controller has no methods


}
