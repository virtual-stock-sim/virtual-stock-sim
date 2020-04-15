package io.github.virtualstocksim.account;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CreateAccountModelTest
{
    private CreateAccountModel accountModel;

    @Before
    public void setup()
    {
         accountModel = new CreateAccountModel("dpalmieri@ycp.edu","DanPalm5");
    }

    @Test
    public void testGetEmail()
    {
        assertEquals(accountModel.getEmail(), "dpalmieri@ycp.edu");
    }

    @Test
    public void testGetUsername()
    {
        assertEquals(accountModel.getUsername(), "DanPalm5");
    }

    @Test
    public void testSetEmail()
    {
        accountModel.setEmail("VSSAdmin@vss.com");
        assertEquals(accountModel.getEmail(), "VSSAdmin@vss.com");
    }

    @Test
    public void testSetUsername()
    {
        accountModel.setUsername("AdminDan");
        assertEquals(accountModel.getUsername(), "AdminDan");
    }


}
