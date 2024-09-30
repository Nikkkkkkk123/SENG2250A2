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
            byte[] newMessage = null;
            String decryptedWebsite = null;
            boolean isEnding = false;
            if (new String (message).contains("end")) {
                newMessage = message;
                isEnding = true;
            }
            else if (!isEnding) {
                if (new String (message).substring(0, 3).equals("get")) {
                    String parts[] = new String (message).split(" ");
                    BigInteger encryptedTwo = rsa.encrypt(parts[1]);
                    decryptedWebsite = parts[1];
                    newMessage = (parts[0] + " " + encryptedTwo.toString()).getBytes();
                }
                else if (new String (message).substring(0, 5).equals("store")) {
                    String parts [] = new String (message).split(" ", 3);
                    decryptedWebsite = parts[1];
                    BigInteger encryptedTwo = rsa.encrypt(parts[1]);
                    BigInteger encryptedOne = rsa.encrypt(parts[2]);
                    newMessage = (parts[0] + " " + encryptedTwo.toString() + " " + encryptedOne.toString()).getBytes();
                }
            }

            out.writeObject(newMessage);  // Send the message to the server
            out.flush();
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            byte buffer[] = (byte[]) in.readObject(); // Receive a message from the server
            String response = new String(buffer);
            if (!isEnding) {
                if (new String (message).substring(0, 5).equals("store")) {
                    if (response.contains("Password already exists for")) {
                        String[] parts = response.split("Password already exists for ");
                        return ("Password already exists for " + decryptedWebsite).getBytes();
                    }
                }
                else if (new String (message).substring(0, 3).equals("get")) {
                    if (response.contains("Password not found.")) {
                        return "Password not found.".getBytes();
                    }
                    else {
                        String[] parts = response.split(" ");
                        String decryptedOne = rsa.decrypt(new BigInteger(parts[0]));
                        return ("Your password for "+decryptedWebsite+" is " + decryptedOne).getBytes();
                    }
                }
            }
            return buffer;
        } catch (Exception e) {
            System.err.println("An error occurred while communicating with the server: " + e.getMessage());
            e.printStackTrace();
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
                        System.out.println("Invalid input");
                        break;
                    }
                    System.out.println(new String(sendReceive(hostname, port, input.getBytes())));
                    break;

                case 3 :
                    if (input.split(" ").length != 2) {
                        System.out.println("No website specified");
                        break;
                    }
                    System.out.println(new String (sendReceive(hostname, port, input.getBytes())));
                    break;
                default :
                    System.out.println("Please enter a valid option: ");
                    break;
            }
        }
    }
}