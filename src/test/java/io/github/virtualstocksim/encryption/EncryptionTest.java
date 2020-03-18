package io.github.virtualstocksim.encryption;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class EncryptionTest {
    private Encryption encrypt;
    private String test_cred1;
    private String test_cred2;
    private byte[] hash1;
    private byte[] hash2;
    private byte[] salt1;
    private byte[] salt2;

    @Before
    public void setup(){
        // create encryption object
        encrypt = new Encryption();
        test_cred1 = "topSecret";
        test_cred2 = "superSecret";

    }

    @Test
    public void testSalt() {
        salt1 = encrypt.getNextSalt();
        salt2 = encrypt.getNextSalt();
        assertNotEquals(salt1,salt2);
    }

    @Test
    public void testHash() {
        salt1 = encrypt.getNextSalt();
        hash1 = encrypt.hash(test_cred1.toCharArray(), salt1);
        hash2 = encrypt.hash(test_cred1.toCharArray(), salt1);
        assertTrue(Arrays.equals(hash1, hash2));


       salt2 = encrypt.getNextSalt();
       hash1 = encrypt.hash(test_cred1.toCharArray(),salt1);
       hash2 = encrypt.hash(test_cred2.toCharArray(), salt2);
       assertFalse(Arrays.equals(hash1, hash2));

    }

    @Test
    public void testValidateInput() {
        salt1 = encrypt.getNextSalt();
        hash1 = encrypt.hash(test_cred1.toCharArray(), salt1);
        assertTrue(encrypt.validateInput(test_cred1.toCharArray(), salt1, hash1));

    }

}
