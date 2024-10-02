import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * A password manager server.
 * You will need to give it the ability to correctly store passwords and to send them back to the
 * client when requested.
 */
public class Server {
    private static HashMap<BigInteger, BigInteger> passwords = new HashMap<>();
    public static boolean handleMessage(Socket conn, byte[] data) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(conn.getOutputStream());
        ) {
            String message = new String(data);
            if (message.trim().equals("end")) {
                out.writeObject("end okay".getBytes());
                return true;
            }
            // TODO: Add handling for the other messages (including handling the passwords correctly)
            if (message.substring(0, 3).equals("get")) { // Checks if the message is a get request
                String[] parts = message.split(" "); // Splits the message into parts
                BigInteger encryptedTwo = new BigInteger(parts[1]); // Gets the encrypted password
                if (passwords.containsKey(encryptedTwo)) { // Checks if the password is in the hashmap, if it is, it sends the password back to the client
                    out.writeObject((passwords.get(encryptedTwo)+"").getBytes()); // Sends the password back to the client
                } 
                else {
                    out.writeObject("Password not found.".getBytes()); // If the password is not in the hashmap, it sends a message back to the client
                }
            }
            else if (message.substring(0, 5).equals("store")) { // Checks if the message is a store request
                String[] parts = message.split(" ", 3);
                BigInteger encryptedOne = new BigInteger(parts[2]); // Gets the encrypted password
                BigInteger encryptedTwo = new BigInteger(parts[1]); // Gets the encrypted website

                if (!passwords.containsKey(encryptedTwo)) { // Checks if the password is already in the hashmap, if it is not, it stores the password
                    passwords.put(encryptedTwo, encryptedOne); // Stores the password
                    out.writeObject("Password successfully stored.".getBytes()); // Sends a message back to the client
                } 
                else {
                    out.writeObject("Password already exists.".getBytes()); // If the password is already in the hashmap, it sends a message back to the client
                }
            }

            out.flush(); // Flushes the output stream
            return false; // returns false to continue the loop
        } 
        catch (Exception e) {
            e.printStackTrace(); // Prints the stack trace
        }
        return false;
    }

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 22500;  // Arbitrary non-privileged port
        boolean end = false;
        System.out.format("Listening for a client on port %d\n", port);

        try (
            ServerSocket serverSocket = new ServerSocket(port)
        ) {
            do {
                Socket socket = serverSocket.accept();
                System.out.format(
                    "Connected by %s:%d\n",
                    socket.getInetAddress().toString(),
                    socket.getPort()
                );
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                byte data[] = (byte[]) in.readObject();
                end = handleMessage(socket, data);
                in.close();
                socket.close();
            } while (!end);

        } catch (Exception e) {
            // TODO: Add some better error handling
            e.printStackTrace();
        }
    }
}