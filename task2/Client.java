import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Scanner;  
import java.net.Socket;
/**
 * A password manager client.
 * You will need to give it an interface with the options to send the server a password to store, to
 * get a stored password from the server, and to end the programs. You will also have to implement RSA
 * and use it correctly so that the server and any possible interceptor can never read the passwords.
 */
public class Client {
    private static RSA rsa = new RSA();

    /**
     * Send and receive a message in bytes to the specified host at the specified port.
     * 
     * @param host string stating the name or ip address of the server
     * @param port int stating the port number of the server
     * @param message byte array containing the message to send to the server
     * @return a byte array containing the message that is received back from the server
     */
    public static byte[] sendReceive(String hostname, int port, byte[] message) {
        try (
            Socket s = new Socket(hostname, port);
        ) {
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(message);  // Send the message to the server
            out.flush();
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            byte buffer[] = (byte[]) in.readObject(); // Receive a message from the server
            return buffer;
        } catch (Exception e) {
            if (e instanceof java.io.IOException) {
                return ("You have not connected to the server.").getBytes();
            } 
            else {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 22500;

        Scanner scanner = new Scanner(System.in); // Scanner to get user input
        boolean end = false; // Flag to end the program
        System.out.println("Welcome to the SENG2250 password manager client, you have the following options:");
        while (!end) {
            // TODO: Add a user interface and correct cryptographic handling of passwords
            int want = 0; // Flag to determine what the request the user has made is
            System.out.println("- store <website> <password>");
            System.out.println("- get <website>");
            System.out.println("- end");
            System.out.print(">>> ");

            String input = scanner.nextLine(); // Get the user input

            // Determine what the user wants to do
            if (input.contains("store")) {
                want = 2;
            }
            else if (input.contains("get")) {
                want = 3;
            }
            else if (input.contains("end")) {
                want = 1;
            }

            // Perform the action the user wants
            switch (want) {
                case 1 :
                    sendReceive(hostname, port, "end".getBytes()); // Send the end message to the server
                    System.out.println("bye."); // Print a goodbye message
                    end = true; // Set the end flag to true to end the program
                    break;

                case 2 : 
                    if (input.split(" ", 3).length != 3) { // Checks if the input is in the correct format, if it is not, it prints an error message
                        System.out.println("To store a password, please use the format: store <website> <password>");
                        break;
                    }
                    String parts [] = new String (input).split(" ", 3); // Splits the input into parts
                    String website = parts[1]; // Stores the website
                    BigInteger encryptedWebsite = rsa.encrypt(parts[1]); // Encrypts and stores the website
                    BigInteger encryptedPassword = rsa.encrypt(parts[2]); // Encrypts and stores the password
                    byte[] newMessage = (parts[0] + " " + encryptedWebsite.toString() + " " + encryptedPassword.toString()).getBytes(); // Creates a new message,  converts it to bytes
                    try {
                        String response = new String (sendReceive(hostname, port, newMessage)); // Sends the message to the server and stores the response
                        System.out.println(response); // Prints the response
                    } 
                    catch (Exception e) { // Catches any exceptions
                        System.out.println("Password already exists for " + website); // Prints an error message
                    }
                    break;

                case 3 :
                    if (input.split(" ").length != 2) { // Checks if the input is in the correct format, if it is not, it prints an error message
                        System.out.println("To get a password, please use the format: get <website>");
                        break;
                    }
                    String getParts[] = new String (input).split(" ");
                    BigInteger encryptedGetWebsite = rsa.encrypt(getParts[1]); // Encrypts the website and stores it
                    String decryptedWebsite = getParts[1]; // Stores the plaintext website name
                    byte[] getNewMessage = (getParts[0] + " " + encryptedGetWebsite.toString()).getBytes(); // Creates a new message, converts it to bytes
                    try {
                        String getResponse = new String (sendReceive(hostname, port, getNewMessage)); // Sends the message to the server and stores the response
                        BigInteger getRequestEncryptedPassword = new BigInteger(getResponse); // Gets the encrypted password
                        String decryptedPassword = rsa.decrypt(getRequestEncryptedPassword); // Decrypts the password
                        System.out.println("Your password for " + decryptedWebsite + " is " + decryptedPassword); // Notifies the user of the password for the website
                    } 
                    catch (Exception e) {
                        System.out.println("Password not found for " + decryptedWebsite); 
                    }
                    break;
                default :
                    System.out.println("Please enter a valid option: "); // Prints an error message if the user input is not valid
                    break;
            }
        }
        scanner.close(); // Closes the scanner
    }
}