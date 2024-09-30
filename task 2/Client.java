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
            if (e instanceof java.io.EOFException) {
                return null;
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

        Scanner scanner = new Scanner(System.in);
        boolean end = false;
        System.out.println("Welcome to the SENG2250 password manager client, you have the following options:");
        while (!end) {
            // TODO: Add a user interface and correct cryptographic handling of passwords
            int want = 0;
            System.out.println("- store <website> <password>");
            System.out.println("- get <website>");
            System.out.println("- end");

            System.out.print(">>> ");
            String input = scanner.nextLine();
            if (input.contains("store")) {
                want = 2;
            }
            else if (input.contains("get")) {
                want = 3;
            }
            else if (input.contains("end")) {
                want = 1;
            }

            switch (want) {
                case 1 :
                    sendReceive(hostname, port, "end".getBytes());
                    System.out.println("bye.");
                    end = true;
                    break;

                case 2 : 
                    if (input.split(" ", 3).length != 3) {
                        System.out.println("To store a password, please use the format: store <website> <password>");
                        break;
                    }
                    String parts [] = new String (input).split(" ", 3);
                    String website = parts[1];
                    BigInteger encryptedTwo = rsa.encrypt(parts[1]);
                    BigInteger encryptedOne = rsa.encrypt(parts[2]);
                    byte[] newMessage = (parts[0] + " " + encryptedTwo.toString() + " " + encryptedOne.toString()).getBytes();
                    try {
                        String response = new String (sendReceive(hostname, port, newMessage));
                        System.out.println(response);
                    } 
                    catch (Exception e) {
                        System.out.println("Password already exists for " + website);
                    }
                    break;

                case 3 :
                    if (input.split(" ").length != 2) {
                        System.out.println("To get a password, please use the format: get <website>");
                        break;
                    }
                    String getParts[] = new String (input).split(" ");
                    BigInteger encryptedGetWebsite = rsa.encrypt(getParts[1]);
                    String decryptedWebsite = getParts[1];
                    byte[] getNewMessage = (getParts[0] + " " + encryptedGetWebsite.toString()).getBytes();
                    try {
                        String getResponse = new String (sendReceive(hostname, port, getNewMessage));
                        BigInteger encryptedPassword = new BigInteger(getResponse);
                        String decryptedPassword = rsa.decrypt(encryptedPassword);
                        System.out.println("Your password for " + decryptedWebsite + " is " + decryptedPassword);
                    } 
                    catch (Exception e) {
                        System.out.println("Password not found for " + decryptedWebsite);
                    }
                    break;
                default :
                    System.out.println("Please enter a valid option: ");
                    break;
            }
        }
        scanner.close();
    }
}