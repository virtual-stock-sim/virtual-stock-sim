package io.github.virtualstocksim.account;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class AccountTest
{
    private Account account;

    @Before
    public void setup() {
        account = new Account();
    }

    @Test
    public void testGetUsername() {
        account.setUname("Dan");
        assertEquals("Dan", account.getUname());
    }

    @Test
    public void testGetPassword() {
        account.setPword("VSS");
        assertEquals("VSS", account.getPword());
    }

    @Test
    public void testGetEmail() {
        account.setEmail("dpalmieri@ycp.edu");
        assertEquals("dpalmieri@ycp.edu", account.getEmail());
    }
}
