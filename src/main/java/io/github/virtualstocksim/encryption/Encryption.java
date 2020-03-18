package io.github.virtualstocksim.encryption;



import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

/* The getNextSalt(), hash(), and validateInput() methods are from the example given by assylias on Stack Overflow
 https://stackoverflow.com/questions/18142745/how-do-i-generate-a-salt-in-java-for-salted-hash/18143616#18143616
 */

public class Encryption {
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public Encryption() {}


    /**
     * Returns a random salt to be used to hash an input
     * @return a 16 byte random salt
     */
    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    /**
     * Returns a salted and hashed password
     * @param input, credential to be hashed
     * @param salt, a 16 byte salt
     * @return the hashed credential
     */
    public static byte[] hash(char[] input, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(input, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(input, Character.MIN_VALUE);

        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new AssertionError("Error while hashing a password:" +e.getMessage(), e);
        }  finally {
            spec.clearPassword();
        }
    }

    /**
     * Returns true if the given input and salt match the hashed value, false otherwise.
     *
     * @param input        the input to check
     * @param salt         the salt used to hash the password
     * @param expectedHash the expected hashed value of the password
     *
     * @return true if the given input and salt match the hashed value, false otherwise
     */
    public static boolean validateInput(char[] input, byte[] salt, byte[] expectedHash) {
        byte[] pwdHash = hash(input, salt);
        Arrays.fill(input, Character.MIN_VALUE);
        if (pwdHash.length != expectedHash.length) return false;
        for (int i = 0; i < pwdHash.length; i++) {
            if (pwdHash[i] != expectedHash[i]) return false;
        }
        return true;
    }

}
