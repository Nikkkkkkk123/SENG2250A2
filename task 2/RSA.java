import java.math.BigInteger;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class RSA {
    private BigInteger p; // Prime number 1
    private BigInteger q; // Prime number 2
    private BigInteger n; // Product of the two prime numbers
    private BigInteger phi; // Eulers Totient of the two prime numbers
    private BigInteger e; // Public exponent
    private BigInteger d; // Private exponent

    public RSA() {
        getInput();
        n = findN();
        phi = getEulersTotient();
        e = new BigInteger("65537");
        d = privateExponent();
    }

        /* 
     * Description: Get the two prime numbers from the file
     * 
     * @param none
     * @return none
     * 
     * @throws IOException if the file is not found
     * @throws NumberFormatException if the number is not a valid BigInteger
     */
    public void getInput() {
        File file = new File("primes.txt");
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.charAt(0) == 'p') {
                    p = new BigInteger(line.substring(2));
                } else if (line.charAt(0) == 'q') {
                    q = new BigInteger(line.substring(2));
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    /*
     * Description: Encrypt the plain text using the public key
     * 
     * @param plainText the text to be encrypted
     * @return BigInteger the encrypted text
     */
    public BigInteger encrypt (String plainText) {
        byte[] messageBytes = plainText.getBytes();
        BigInteger messageInt = new BigInteger(1, messageBytes);
        BigInteger ciphertext = fastModExp(messageInt, e);
        return ciphertext;
    }

    /*
     * Description: Decrypt the encrypted message using the private key
     * 
     * @param encryptedMessage the message to be decrypted
     * @return String the decrypted message
     */
    public String decrypt (BigInteger encryptedMessage) {
        BigInteger decryptedInt = fastModExp(encryptedMessage, d);
        byte[] decryptedBytes = decryptedInt.toByteArray();
        return new String(decryptedBytes);
    }

      /*
     * Description: Perform fast modular exponentiation
     * 
     * @param base the base number
     * @param exponential the exponent
     * @return BigInteger the result of the modular exponentiation
     * 
     */
    private BigInteger fastModExp(BigInteger base, BigInteger exponential) {
        BigInteger result = BigInteger.ONE;
        base = base.mod(n);
        while (exponential.compareTo(BigInteger.ZERO) > 0) {
            if (exponential.mod(BigInteger.TWO).equals(BigInteger.ONE)) {
                result = result.multiply(base).mod(n);
            }
            exponential = exponential.shiftRight(1); 
            base = base.multiply(base).mod(n);
        }
        return result;
    }

    /*
     * Description: Find the product of the two prime numbers
     * 
     * @param none
     * @return BigInteger the product of the two prime numbers
     */
    private BigInteger findN() {
        return p.multiply(q);
    }

    /*
     * Description: Find the Eulers Totient of the two prime numbers
     * 
     * @param none
     * @return BigInteger the Eulers Totient
     */
    private BigInteger getEulersTotient() {
        return p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
    }

    /*
     * Description: Find the private exponent
     * 
     * @param none
     * @return BigInteger the private exponent
     */
    private BigInteger privateExponent() {
        return e.modInverse(phi);
    }
}