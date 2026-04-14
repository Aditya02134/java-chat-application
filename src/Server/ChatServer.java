package Server;

import java.io.*;      // For input-output streams
import java.net.*;     // For networking (Socket, ServerSocket)
import java.util.*;    // For data structures (Map, HashMap)

public class ChatServer {

    // Port number on which server will run
    private static final int PORT = 8000;

    // Map to store username and corresponding client output stream
    // Used to send messages to specific clients
    private static Map<String, PrintWriter> clientWriters =
            Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {

        System.out.println("Server started...");
        System.out.println("Waiting for clients...");

        // Create server socket on defined port
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Server runs continuously
            while (true) {

                // Accept incoming client connection
                Socket socket = serverSocket.accept();

                // Create new thread for each client
                new ClientHandler(socket).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 Thread class to handle each client separately
    static class ClientHandler extends Thread {

        private Socket socket;             // Client socket
        private BufferedReader in;         // To read messages from client
        private PrintWriter out;           // To send messages to client
        private String username;           // Client username

        // Constructor
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {

            try {
                // Initialize input and output streams
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First message from client is username
                username = in.readLine();

                // Add user to map
                synchronized (clientWriters) {
                    clientWriters.put(username, out);
                }

                // Send existing users list to new client
                synchronized (clientWriters) {
                    for (String user : clientWriters.keySet()) {
                        out.println("USERLIST:" + user);
                    }
                }

                // Notify other users about new user
                synchronized (clientWriters) {
                    for (Map.Entry<String, PrintWriter> entry : clientWriters.entrySet()) {
                        if (!entry.getKey().equals(username)) {
                            entry.getValue().println("USERLIST:" + username);
                        }
                    }
                }

                // Broadcast join message
                synchronized (clientWriters) {
                    for (PrintWriter writer : clientWriters.values()) {
                        writer.println(username + " joined the chat");
                    }
                }

                String message;

                // Read messages continuously from client
                while ((message = in.readLine()) != null) {

                    // Logout condition
                    if (message.equals("##LOGOUT##")) break;

                    // ================= TYPING INDICATOR =================
                    if (message.startsWith("TYPING:")) {

                        // Send typing status to all other users
                        synchronized (clientWriters) {
                            for (Map.Entry<String, PrintWriter> entry : clientWriters.entrySet()) {

                                if (!entry.getKey().equals(username)) {
                                    entry.getValue().println(message);
                                }
                            }
                        }

                        continue; // Prevent further processing
                    }

                    // ================= PRIVATE MESSAGE =================
                    if (message.startsWith("PRIVATE:")) {

                        // Format: PRIVATE:targetUser:message
                        String[] parts = message.split(":", 3);
                        String targetUser = parts[1];
                        String msg = parts[2];

                        PrintWriter targetWriter;

                        synchronized (clientWriters) {
                            targetWriter = clientWriters.get(targetUser);
                        }

                        // Send to target user
                        if (targetWriter != null) {
                            targetWriter.println(username + " → you: " + msg);
                        }

                        // Send confirmation to sender
                        out.println(username + " → " + targetUser + ": " + msg);
                    }

                    // ================= BROADCAST MESSAGE =================
                    else {

                        // Format message
                        String formattedMessage = username + ": " + message;

                        // Send to all clients
                        synchronized (clientWriters) {
                            for (PrintWriter writer : clientWriters.values()) {
                                writer.println(formattedMessage);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            finally {
                try {

                    // Remove user from list
                    synchronized (clientWriters) {
                        clientWriters.remove(username);
                    }

                    // Notify all users that this user left
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters.values()) {
                            writer.println("REMOVEUSER:" + username);
                            writer.println(username + " left the chat");
                        }
                    }

                    // Close socket connection
                    socket.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}