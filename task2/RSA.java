/*
 * File Name: RSA.java
 * Author: Nikkita Nichols (c3362623)
 * Course: SENG2250
 * Date Created: 2024/09/30
 * Last Updated: 2024/10/01
 * Description: This class is used to encrypt and decrypt messages using the RSA algorithm. It reads two prime numbers from a file, calculates the public and private keys, 
 * and then encrypts and decrypts messages using these keys.
 */

import java.math.BigInteger;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class RSA {
    private BigInteger p; // Prime number 1
    private BigInteger q; // Prime number 2
    private BigInteger n; // Product of the two prime numbers
    private BigInteger phi; // Eulers Totient of the two prime numbers
    private BigInteger e; // Public exponent
    private BigInteger d; // Private exponent

    public RSA() {
        getInput(); // Get the two prime numbers from the file
        n = findN(); // Find the product of the two prime numbers
        phi = getEulersTotient(); // Find the Eulers Totient of the two prime numbers
        e = publicExponent(); // Public exponent
        d = privateExponent(); // Private exponent
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
        File file = new File("primes.txt"); // Sets the file to get the prime numbers from
        try {
            Scanner scanner = new Scanner(file); // Scanner to read the file
            while (scanner.hasNextLine()) { // While there are still lines in the file
                String line = scanner.nextLine(); // Get the next line
                if (line.charAt(0) == 'p') { // If the line is the first prime number then set p to the number
                    p = new BigInteger(line.substring(2));
                } else if (line.charAt(0) == 'q') { // If the line is the second prime number then set q to the number
                    q = new BigInteger(line.substring(2));
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("File not found"); // Throw an error if the file is not found
        }
    }

    /*
     * Description: Encrypt the plain text using the public key
     * 
     * @param plainText the text to be encrypted
     * @return BigInteger the encrypted text
     */
    public BigInteger encrypt (String plainText) {
        byte[] messageBytes = plainText.getBytes(); // Convert the message to bytes
        BigInteger messageInt = new BigInteger(1, messageBytes); // Convert the bytes to a BigInteger (1 is positive)
        BigInteger ciphertext = fastModExp(messageInt, e); // Encrypt the message using the public key
        return ciphertext; // Return the encrypted message
    }

    /*
     * Description: Decrypt the encrypted message using the private key
     * 
     * @param encryptedMessage the message to be decrypted
     * @return String the decrypted message
     */
    public String decrypt (BigInteger encryptedMessage) {
        BigInteger decryptedInt = fastModExp(encryptedMessage, d); // Decrypt the message using the private key
        byte[] decryptedBytes = decryptedInt.toByteArray(); // Convert the decrypted message to bytes
        return new String(decryptedBytes); // Return the decrypted message as a string
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
        BigInteger result = BigInteger.ONE; // Set the result to 1
        base = base.mod(n);
        while (exponential.compareTo(BigInteger.ZERO) > 0) {
            if (exponential.mod(BigInteger.TWO).equals(BigInteger.ONE)) { // Checks if the exponent is odd, if it is then multiply the result by the base
                result = result.multiply(base).mod(n);
            }
            exponential = exponential.shiftRight(1);  // Divide the exponent by 2 and take the floor value. shiftRight(1) is equivalent to dividing by 2. Computes floor(exponential/2)
            base = base.multiply(base).mod(n); // Square the base and take the modulus
        }
        return result; // Return the result
    }

        /*
     * Description: Find a coprime of the Eulers Totient
     * 
     * @param phi the Eulers Totient
     * @return BigInteger a coprime of the Eulers Totient
     */
    private BigInteger findCoprime (BigInteger phi) {
        Random randNo = new Random();
        BigInteger temp = new BigInteger(1024, randNo);

        while (temp.compareTo(phi) < 0) {
            if (gcd(temp, phi).equals(BigInteger.ONE)) {
                return temp;
            }
            temp = temp.add(BigInteger.ONE);
        }
        return BigInteger.ZERO;
    }

    /*
     * Description: Find the greatest common divisor of two numbers
     * 
     * @param a the first number
     * @param b the second number
     * @return BigInteger the greatest common divisor
     */
    private BigInteger gcd(BigInteger a, BigInteger b) {
        while (b != BigInteger.ZERO) {
            BigInteger temp = b;
            b = a.mod(b);
            a = temp;
        }
        return a;
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

    /*
     * Description: Find the public exponent
     * 
     * @param none
     * @return BigInteger the public exponent
     */
    private BigInteger publicExponent() {
        return findCoprime(phi);
    }
}