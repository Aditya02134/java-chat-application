package Server;

import java.io.*;       // For input-output streams
import java.net.*;      // For socket communication
import java.util.Map;   // For storing client connections

// Thread class to handle communication with one client
public class ClientHandler extends Thread {

    private Socket socket;                 // Socket for this client
    private BufferedReader in;             // To read data from client
    private PrintWriter out;               // To send data to client
    private String username;               // Username of connected client

    // Shared map storing username → output stream
    private Map<String, PrintWriter> clientWriters;

    // Constructor
    public ClientHandler(Socket socket, Map<String, PrintWriter> clientWriters) {
        this.socket = socket;
        this.clientWriters = clientWriters;
    }

    public void run() {

        try {
            // Initialize input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // First message received from client is username
            username = in.readLine();

            // Add user to map
            clientWriters.put(username, out);

            // ================= SEND EXISTING USERS =================
            // Send list of already connected users to new client
            for (String user : clientWriters.keySet()) {
                out.println("USERLIST:" + user);
            }

            // ================= NOTIFY ALL USERS =================
            // Inform all users about the new user
            for (PrintWriter writer : clientWriters.values()) {
                writer.println("USERLIST:" + username);
                writer.println(username + " joined the chat");
            }

            String message;

            // Continuously read messages from client
            while ((message = in.readLine()) != null) {

                // Logout condition
                if (message.equals("##LOGOUT##")) break;

                // ================= PRIVATE MESSAGE =================
                if (message.startsWith("PRIVATE:")) {

                    // Format: PRIVATE:targetUser:message
                    String[] parts = message.split(":", 3);
                    String target = parts[1];
                    String msg = parts[2];

                    // Get target user's writer
                    PrintWriter targetWriter = clientWriters.get(target);

                    // Send message to target user
                    if (targetWriter != null) {
                        targetWriter.println(username + " → you: " + msg);
                    }

                    // Show message to sender as confirmation
                    out.println(username + " → " + target + ": " + msg);
                }

                // ================= BROADCAST MESSAGE =================
                else {

                    // Format message
                    String formatted = username + ": " + message;

                    // Send message to all connected clients
                    for (PrintWriter writer : clientWriters.values()) {
                        writer.println(formatted);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            try {
                // Remove user from map
                clientWriters.remove(username);

                // Notify all clients that user has left
                for (PrintWriter writer : clientWriters.values()) {
                    writer.println("REMOVEUSER:" + username);
                    writer.println(username + " left the chat");
                }

                // Close socket connection
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}