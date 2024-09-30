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
            if (message.substring(0, 3).equals("get")) {
                String[] parts = message.split(" ");
                BigInteger encryptedTwo = new BigInteger(parts[1]);
                if (passwords.containsKey(encryptedTwo)) {
                    out.writeObject((passwords.get(encryptedTwo)+"").getBytes());
                } 
                else {
                    throw new Exception("Password not found.");
                }
            }
            else if (message.substring(0, 5).equals("store")) {
                String[] parts = message.split(" ", 3);
                BigInteger encryptedOne = new BigInteger(parts[2]);
                BigInteger encryptedTwo = new BigInteger(parts[1]);

                if (!passwords.containsKey(encryptedTwo)) {
                    passwords.put(encryptedTwo, encryptedOne);
                    out.writeObject("Password successfully stored.".getBytes());
                } 
                else {
                    throw new Exception("Password already exists.");
                }
            }

            out.flush();
            return false;
        } 
        catch (Exception e) {
            if (e.getMessage().equals("Password already exists.")) {
                System.out.println("Password already exists.");
            }
            else if (e.getMessage().equals("Password not found.")) {
                System.out.println("Password not found.");
            }
            else {
                e.printStackTrace();
            }
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